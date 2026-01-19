package com.hotel.reservations.exception;

/**
 * Exception levée lorsqu'une chambre n'est pas disponible.
 */
public class ChambreNonDisponibleException extends ReservationException {
    
    private final Long chambreId;
    
    public ChambreNonDisponibleException(Long chambreId) {
        super("CHAMBRE_NON_DISPONIBLE", "La chambre avec l'ID " + chambreId + " n'est pas disponible pour les dates demandées");
        this.chambreId = chambreId;
    }
    
    public ChambreNonDisponibleException(Long chambreId, String message) {
        super("CHAMBRE_NON_DISPONIBLE", message);
        this.chambreId = chambreId;
    }
    
    public Long getChambreId() {
        return chambreId;
    }
}
