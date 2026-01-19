package com.hotel.reservations.exception;

/**
 * Exception levée lorsqu'une réservation n'est pas trouvée.
 */
public class ReservationNotFoundException extends RuntimeException {
    
    private final Long reservationId;
    
    public ReservationNotFoundException(Long reservationId) {
        super("Réservation non trouvée avec l'ID: " + reservationId);
        this.reservationId = reservationId;
    }
    
    public ReservationNotFoundException(String message) {
        super(message);
        this.reservationId = null;
    }
    
    public Long getReservationId() {
        return reservationId;
    }
}
