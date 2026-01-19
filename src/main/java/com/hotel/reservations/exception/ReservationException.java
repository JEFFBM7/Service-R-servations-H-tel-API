package com.hotel.reservations.exception;

/**
 * Exception levée lorsqu'une opération sur une réservation est invalide.
 */
public class ReservationException extends RuntimeException {
    
    private final String code;
    
    public ReservationException(String message) {
        super(message);
        this.code = "RESERVATION_ERROR";
    }
    
    public ReservationException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public ReservationException(String message, Throwable cause) {
        super(message, cause);
        this.code = "RESERVATION_ERROR";
    }
    
    public String getCode() {
        return code;
    }
}
