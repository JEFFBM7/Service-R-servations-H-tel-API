package com.hotel.reservations.dto;

import java.math.BigDecimal;

/**
 * DTO représentant les informations d'une chambre provenant du Service Chambres.
 */
public class ChambreDTO {
    
    private Long id;
    private String numero;
    private String type; // SIMPLE, DOUBLE, SUITE, etc.
    private BigDecimal prixParNuit;
    private String statut; // LIBRE, OCCUPEE, MAINTENANCE
    private boolean disponible;
    private String description;
    private int capacite;
    
    // Constructeurs
    public ChambreDTO() {
    }
    
    public ChambreDTO(Long id, String numero, String type, BigDecimal prixParNuit) {
        this.id = id;
        this.numero = numero;
        this.type = type;
        this.prixParNuit = prixParNuit;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public BigDecimal getPrixParNuit() {
        return prixParNuit;
    }
    
    public void setPrixParNuit(BigDecimal prixParNuit) {
        this.prixParNuit = prixParNuit;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public boolean isDisponible() {
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getCapacite() {
        return capacite;
    }
    
    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }
}
