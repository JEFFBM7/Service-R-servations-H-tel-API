package com.hotel.reservations.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Entité représentant une réservation d'hôtel.
 */
@Entity
@Table(name = "reservations")
@NamedQueries({
    @NamedQuery(
        name = "Reservation.findAll",
        query = "SELECT r FROM Reservation r ORDER BY r.dateCreation DESC"
    ),
    @NamedQuery(
        name = "Reservation.findByStatut",
        query = "SELECT r FROM Reservation r WHERE r.statut = :statut ORDER BY r.dateDebut"
    ),
    @NamedQuery(
        name = "Reservation.findByClientId",
        query = "SELECT r FROM Reservation r WHERE r.clientId = :clientId ORDER BY r.dateDebut DESC"
    ),
    @NamedQuery(
        name = "Reservation.findByChambreId",
        query = "SELECT r FROM Reservation r WHERE r.chambreId = :chambreId ORDER BY r.dateDebut DESC"
    ),
    @NamedQuery(
        name = "Reservation.findOccupationsActuelles",
        query = "SELECT r FROM Reservation r WHERE r.statut = com.hotel.reservations.entity.StatutReservation.EN_COURS"
    ),
    @NamedQuery(
        name = "Reservation.findReservationsAVenir",
        query = "SELECT r FROM Reservation r WHERE r.dateDebut > CURRENT_DATE AND r.statut IN (com.hotel.reservations.entity.StatutReservation.CONFIRMEE, com.hotel.reservations.entity.StatutReservation.EN_ATTENTE) ORDER BY r.dateDebut"
    ),
    @NamedQuery(
        name = "Reservation.checkChevauchement",
        query = "SELECT r FROM Reservation r WHERE r.chambreId = :chambreId AND r.statut NOT IN (com.hotel.reservations.entity.StatutReservation.ANNULEE, com.hotel.reservations.entity.StatutReservation.TERMINEE) AND ((r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut))"
    )
})
public class Reservation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "L'identifiant du client est obligatoire")
    @Column(name = "client_id", nullable = false)
    private Long clientId;
    
    @NotNull(message = "L'identifiant de la chambre est obligatoire")
    @Column(name = "chambre_id", nullable = false)
    private Long chambreId;
    
    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;
    
    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;
    
    @NotNull
    @DecimalMin(value = "0.0", message = "Le montant doit être positif")
    @Digits(integer = 10, fraction = 2)
    @Column(name = "montant_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal;
    
    @Column(name = "prix_par_nuit", precision = 10, scale = 2)
    private BigDecimal prixParNuit;
    
    @Size(max = 500, message = "Les remarques ne peuvent pas dépasser 500 caractères")
    @Column(length = 500)
    private String remarques;
    
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @Version
    private Integer version;
    
    // Constructeurs
    public Reservation() {
    }
    
    public Reservation(Long clientId, Long chambreId, LocalDate dateDebut, LocalDate dateFin) {
        this.clientId = clientId;
        this.chambreId = chambreId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = StatutReservation.EN_ATTENTE;
    }
    
    // Callbacks JPA
    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }
    
    // Méthodes métier
    
    /**
     * Calcule le nombre de nuits de la réservation.
     */
    public long getNombreNuits() {
        if (dateDebut != null && dateFin != null) {
            return ChronoUnit.DAYS.between(dateDebut, dateFin);
        }
        return 0;
    }
    
    /**
     * Calcule le montant total basé sur le prix par nuit.
     */
    public void calculerMontantTotal() {
        if (prixParNuit != null && dateDebut != null && dateFin != null) {
            long nuits = getNombreNuits();
            this.montantTotal = prixParNuit.multiply(BigDecimal.valueOf(nuits));
        }
    }
    
    /**
     * Vérifie si la réservation peut être annulée.
     */
    public boolean peutEtreAnnulee() {
        return statut == StatutReservation.EN_ATTENTE || statut == StatutReservation.CONFIRMEE;
    }
    
    /**
     * Vérifie si la réservation peut être modifiée.
     */
    public boolean peutEtreModifiee() {
        return statut == StatutReservation.EN_ATTENTE || statut == StatutReservation.CONFIRMEE;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public Long getChambreId() {
        return chambreId;
    }
    
    public void setChambreId(Long chambreId) {
        this.chambreId = chambreId;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    public StatutReservation getStatut() {
        return statut;
    }
    
    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }
    
    public BigDecimal getMontantTotal() {
        return montantTotal;
    }
    
    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }
    
    public BigDecimal getPrixParNuit() {
        return prixParNuit;
    }
    
    public void setPrixParNuit(BigDecimal prixParNuit) {
        this.prixParNuit = prixParNuit;
    }
    
    public String getRemarques() {
        return remarques;
    }
    
    public void setRemarques(String remarques) {
        this.remarques = remarques;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public LocalDateTime getDateModification() {
        return dateModification;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", chambreId=" + chambreId +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statut=" + statut +
                ", montantTotal=" + montantTotal +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
