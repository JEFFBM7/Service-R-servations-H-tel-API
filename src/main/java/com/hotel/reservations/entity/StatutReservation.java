package com.hotel.reservations.entity;

/**
 * Énumération des statuts possibles d'une réservation.
 */
public enum StatutReservation {
    
    /**
     * Réservation en attente de confirmation
     */
    EN_ATTENTE("En attente"),
    
    /**
     * Réservation confirmée
     */
    CONFIRMEE("Confirmée"),
    
    /**
     * Réservation annulée
     */
    ANNULEE("Annulée"),
    
    /**
     * Client a effectué le check-in
     */
    EN_COURS("En cours"),
    
    /**
     * Client a effectué le check-out
     */
    TERMINEE("Terminée");
    
    private final String libelle;
    
    StatutReservation(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
