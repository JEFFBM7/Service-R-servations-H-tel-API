package com.hotel.reservations.service;

import com.hotel.reservations.dto.RapportReservationsDTO;
import com.hotel.reservations.dto.ReservationDTO;
import com.hotel.reservations.entity.Reservation;
import com.hotel.reservations.entity.StatutReservation;
import com.hotel.reservations.exception.ChambreNonDisponibleException;
import com.hotel.reservations.exception.ReservationException;
import com.hotel.reservations.exception.ReservationNotFoundException;
import com.hotel.reservations.mapper.ReservationMapper;
import com.hotel.reservations.client.ChambreServiceClient;
import com.hotel.reservations.client.ClientServiceClient;
import com.hotel.reservations.dto.ChambreDTO;
import com.hotel.reservations.dto.ClientDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service métier pour la gestion des réservations.
 */
@ApplicationScoped
@Transactional
public class ReservationService {
    
    private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());
    
    @PersistenceContext(unitName = "HotelReservationPU")
    private EntityManager em;
    
    @Inject
    private ReservationMapper mapper;
    
    @Inject
    private ChambreServiceClient chambreClient;
    
    @Inject
    private ClientServiceClient clientClient;
    
    /**
     * Crée une nouvelle réservation.
     */
    public ReservationDTO creerReservation(@Valid ReservationDTO dto) {
        LOGGER.info("Création d'une nouvelle réservation pour client: " + dto.getClientId() + ", chambre: " + dto.getChambreId());
        
        // Validation des dates
        validerDates(dto.getDateDebut(), dto.getDateFin());
        
        // Vérifier la disponibilité de la chambre
        verifierDisponibiliteChambre(dto.getChambreId(), dto.getDateDebut(), dto.getDateFin(), null);
        
        // Valider le client
        ClientDTO client = clientClient.getClient(dto.getClientId());
        if (client != null && client.isFraisImpayes()) {
            throw new ReservationException("CLIENT_FRAIS_IMPAYES", 
                "Le client a des frais impayés et ne peut pas effectuer de réservation");
        }
        
        // Récupérer les informations de la chambre pour le prix
        ChambreDTO chambre = chambreClient.getChambre(dto.getChambreId());
        
        // Créer l'entité
        Reservation reservation = mapper.toEntity(dto);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        
        // Calculer le montant
        if (chambre != null && chambre.getPrixParNuit() != null) {
            reservation.setPrixParNuit(chambre.getPrixParNuit());
        } else if (dto.getPrixParNuit() != null) {
            reservation.setPrixParNuit(dto.getPrixParNuit());
        } else {
            reservation.setPrixParNuit(BigDecimal.valueOf(100)); // Prix par défaut
        }
        reservation.calculerMontantTotal();
        
        em.persist(reservation);
        em.flush();
        
        LOGGER.info("Réservation créée avec succès, ID: " + reservation.getId());
        
        // Enrichir le DTO avec les informations supplémentaires
        ReservationDTO result = mapper.toDTO(reservation);
        if (client != null) {
            result.setNomClient(client.getNomComplet());
        }
        if (chambre != null) {
            result.setNumeroChambre(chambre.getNumero());
            result.setTypeChambre(chambre.getType());
        }
        
        return result;
    }
    
    /**
     * Récupère une réservation par son ID.
     */
    public ReservationDTO getReservation(Long id) {
        Reservation reservation = em.find(Reservation.class, id);
        if (reservation == null) {
            throw new ReservationNotFoundException(id);
        }
        return mapper.toDTO(reservation);
    }
    
    /**
     * Liste toutes les réservations.
     */
    public List<ReservationDTO> listerReservations() {
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findAll", Reservation.class);
        return query.getResultList().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Liste les réservations par statut.
     */
    public List<ReservationDTO> listerReservationsParStatut(StatutReservation statut) {
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findByStatut", Reservation.class);
        query.setParameter("statut", statut);
        return query.getResultList().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Liste les réservations d'un client.
     */
    public List<ReservationDTO> listerReservationsParClient(Long clientId) {
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findByClientId", Reservation.class);
        query.setParameter("clientId", clientId);
        return query.getResultList().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Modifie une réservation existante.
     */
    public ReservationDTO modifierReservation(Long id, @Valid ReservationDTO dto) {
        LOGGER.info("Modification de la réservation ID: " + id);
        
        Reservation reservation = em.find(Reservation.class, id);
        if (reservation == null) {
            throw new ReservationNotFoundException(id);
        }
        
        if (!reservation.peutEtreModifiee()) {
            throw new ReservationException("MODIFICATION_IMPOSSIBLE", 
                "La réservation ne peut pas être modifiée dans son état actuel: " + reservation.getStatut());
        }
        
        // Valider les nouvelles dates si elles changent
        LocalDate newDateDebut = dto.getDateDebut() != null ? dto.getDateDebut() : reservation.getDateDebut();
        LocalDate newDateFin = dto.getDateFin() != null ? dto.getDateFin() : reservation.getDateFin();
        validerDates(newDateDebut, newDateFin);
        
        // Vérifier la disponibilité si la chambre ou les dates changent
        Long newChambreId = dto.getChambreId() != null ? dto.getChambreId() : reservation.getChambreId();
        if (!newChambreId.equals(reservation.getChambreId()) || 
            !newDateDebut.equals(reservation.getDateDebut()) || 
            !newDateFin.equals(reservation.getDateFin())) {
            verifierDisponibiliteChambre(newChambreId, newDateDebut, newDateFin, id);
        }
        
        mapper.updateEntity(reservation, dto);
        reservation.calculerMontantTotal();
        
        em.merge(reservation);
        
        LOGGER.info("Réservation modifiée avec succès, ID: " + id);
        return mapper.toDTO(reservation);
    }
    
    /**
     * Confirme une réservation.
     */
    public ReservationDTO confirmerReservation(Long id) {
        LOGGER.info("Confirmation de la réservation ID: " + id);
        
        Reservation reservation = em.find(Reservation.class, id);
        if (reservation == null) {
            throw new ReservationNotFoundException(id);
        }
        
        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new ReservationException("CONFIRMATION_IMPOSSIBLE", 
                "Seules les réservations en attente peuvent être confirmées");
        }
        
        reservation.setStatut(StatutReservation.CONFIRMEE);
        em.merge(reservation);
        
        // Notifier le service des chambres
        chambreClient.mettreAJourStatutChambre(reservation.getChambreId(), "RESERVEE");
        
        LOGGER.info("Réservation confirmée avec succès, ID: " + id);
        return mapper.toDTO(reservation);
    }
    
    /**
     * Annule une réservation.
     */
    public ReservationDTO annulerReservation(Long id) {
        LOGGER.info("Annulation de la réservation ID: " + id);
        
        Reservation reservation = em.find(Reservation.class, id);
        if (reservation == null) {
            throw new ReservationNotFoundException(id);
        }
        
        if (!reservation.peutEtreAnnulee()) {
            throw new ReservationException("ANNULATION_IMPOSSIBLE", 
                "La réservation ne peut pas être annulée dans son état actuel: " + reservation.getStatut());
        }
        
        reservation.setStatut(StatutReservation.ANNULEE);
        em.merge(reservation);
        
        // Libérer la chambre
        chambreClient.mettreAJourStatutChambre(reservation.getChambreId(), "LIBRE");
        
        LOGGER.info("Réservation annulée avec succès, ID: " + id);
        return mapper.toDTO(reservation);
    }
    
    /**
     * Effectue le check-in.
     */
    public ReservationDTO checkIn(Long id) {
        LOGGER.info("Check-in pour la réservation ID: " + id);
        
        Reservation reservation = em.find(Reservation.class, id);
        if (reservation == null) {
            throw new ReservationNotFoundException(id);
        }
        
        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw new ReservationException("CHECKIN_IMPOSSIBLE", 
                "Le check-in n'est possible que pour les réservations confirmées");
        }
        
        reservation.setStatut(StatutReservation.EN_COURS);
        em.merge(reservation);
        
        // Mettre à jour le statut de la chambre
        chambreClient.mettreAJourStatutChambre(reservation.getChambreId(), "OCCUPEE");
        
        LOGGER.info("Check-in effectué avec succès, ID: " + id);
        return mapper.toDTO(reservation);
    }
    
    /**
     * Effectue le check-out.
     */
    public ReservationDTO checkOut(Long id) {
        LOGGER.info("Check-out pour la réservation ID: " + id);
        
        Reservation reservation = em.find(Reservation.class, id);
        if (reservation == null) {
            throw new ReservationNotFoundException(id);
        }
        
        if (reservation.getStatut() != StatutReservation.EN_COURS) {
            throw new ReservationException("CHECKOUT_IMPOSSIBLE", 
                "Le check-out n'est possible que pour les réservations en cours");
        }
        
        reservation.setStatut(StatutReservation.TERMINEE);
        em.merge(reservation);
        
        // Libérer la chambre
        chambreClient.mettreAJourStatutChambre(reservation.getChambreId(), "LIBRE");
        
        LOGGER.info("Check-out effectué avec succès, ID: " + id);
        return mapper.toDTO(reservation);
    }
    
    /**
     * Génère le rapport des réservations.
     */
    public RapportReservationsDTO genererRapport() {
        LOGGER.info("Génération du rapport des réservations");
        
        RapportReservationsDTO rapport = new RapportReservationsDTO();
        
        // Total des réservations
        Long total = em.createQuery("SELECT COUNT(r) FROM Reservation r", Long.class).getSingleResult();
        rapport.setTotalReservations(total.intValue());
        
        // Occupations actuelles
        TypedQuery<Reservation> queryOccupations = em.createNamedQuery("Reservation.findOccupationsActuelles", Reservation.class);
        List<Reservation> occupations = queryOccupations.getResultList();
        rapport.setOccupationsActuelles(occupations.size());
        rapport.setListeOccupationsActuelles(occupations.stream().map(mapper::toDTO).collect(Collectors.toList()));
        
        // Réservations à venir
        TypedQuery<Reservation> queryAVenir = em.createNamedQuery("Reservation.findReservationsAVenir", Reservation.class);
        List<Reservation> aVenir = queryAVenir.getResultList();
        rapport.setReservationsAVenir(aVenir.size());
        rapport.setListeReservationsAVenir(aVenir.stream().map(mapper::toDTO).collect(Collectors.toList()));
        
        // Réservations annulées
        Long annulees = em.createQuery(
            "SELECT COUNT(r) FROM Reservation r WHERE r.statut = :statut", Long.class)
            .setParameter("statut", StatutReservation.ANNULEE)
            .getSingleResult();
        rapport.setReservationsAnnulees(annulees.intValue());
        
        // Date de génération
        rapport.setDateGeneration(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        
        LOGGER.info("Rapport généré avec succès");
        return rapport;
    }
    
    // --- Méthodes privées ---
    
    /**
     * Valide que les dates sont cohérentes.
     */
    private void validerDates(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new ReservationException("DATES_INVALIDES", "Les dates de début et de fin sont obligatoires");
        }
        if (dateDebut.isAfter(dateFin)) {
            throw new ReservationException("DATES_INVALIDES", "La date de début doit être antérieure à la date de fin");
        }
        if (dateDebut.isBefore(LocalDate.now())) {
            throw new ReservationException("DATES_INVALIDES", "La date de début ne peut pas être dans le passé");
        }
    }
    
    /**
     * Vérifie la disponibilité d'une chambre pour les dates données.
     */
    private void verifierDisponibiliteChambre(Long chambreId, LocalDate dateDebut, LocalDate dateFin, Long excludeReservationId) {
        // Vérifier les chevauchements dans nos réservations
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.checkChevauchement", Reservation.class);
        query.setParameter("chambreId", chambreId);
        query.setParameter("dateDebut", dateDebut);
        query.setParameter("dateFin", dateFin);
        
        List<Reservation> conflits = query.getResultList();
        
        // Exclure la réservation en cours de modification
        if (excludeReservationId != null) {
            conflits = conflits.stream()
                .filter(r -> !r.getId().equals(excludeReservationId))
                .collect(Collectors.toList());
        }
        
        if (!conflits.isEmpty()) {
            throw new ChambreNonDisponibleException(chambreId, 
                "La chambre est déjà réservée pour les dates demandées");
        }
        
        // Vérifier auprès du service des chambres
        boolean disponible = chambreClient.verifierDisponibilite(chambreId, dateDebut, dateFin);
        if (!disponible) {
            throw new ChambreNonDisponibleException(chambreId);
        }
    }
}
