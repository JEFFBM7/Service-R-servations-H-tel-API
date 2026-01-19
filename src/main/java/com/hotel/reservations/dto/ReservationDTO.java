package com.hotel.reservations.dto;

import com.hotel.reservations.entity.StatutReservation;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour la création et modification d'une réservation.
 */
public class ReservationDTO {
    
    private Long id;
    
    @NotNull(message = "L'identifiant du client est obligatoire")
    private Long clientId;
    
    @NotNull(message = "L'identifiant de la chambre est obligatoire")
    private Long chambreId;
    
    @NotNull(message = "La date de début est obligatoire")
    @FutureOrPresent(message = "La date de début doit être aujourd'hui ou dans le futur")
    private LocalDate dateDebut;
    
    @NotNull(message = "La date de fin est obligatoire")
    @Future(message = "La date de fin doit être dans le futur")
    private LocalDate dateFin;
    
    private StatutReservation statut;
    
    private BigDecimal montantTotal;
    
    private BigDecimal prixParNuit;
    
    @Size(max = 500, message = "Les remarques ne peuvent pas dépasser 500 caractères")
    private String remarques;
    
    private Long nombreNuits;
    
    // Informations enrichies (provenant des autres services)
    private String nomClient;
    private String numeroChambre;
    private String typeChambre;
    
    // Constructeurs
    public ReservationDTO() {
    }
    
    public ReservationDTO(Long clientId, Long chambreId, LocalDate dateDebut, LocalDate dateFin) {
        this.clientId = clientId;
        this.chambreId = chambreId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
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
    
    public Long getNombreNuits() {
        return nombreNuits;
    }
    
    public void setNombreNuits(Long nombreNuits) {
        this.nombreNuits = nombreNuits;
    }
    
    public String getNomClient() {
        return nomClient;
    }
    
    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }
    
    public String getNumeroChambre() {
        return numeroChambre;
    }
    
    public void setNumeroChambre(String numeroChambre) {
        this.numeroChambre = numeroChambre;
    }
    
    public String getTypeChambre() {
        return typeChambre;
    }
    
    public void setTypeChambre(String typeChambre) {
        this.typeChambre = typeChambre;
    }
}
