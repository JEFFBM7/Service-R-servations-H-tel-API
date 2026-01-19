package com.hotel.reservations.exception;

/**
 * Exception levée lorsqu'un client n'est pas valide pour une réservation.
 */
public class ClientNonValideException extends ReservationException {
    
    private final Long clientId;
    
    public ClientNonValideException(Long clientId) {
        super("CLIENT_NON_VALIDE", "Le client avec l'ID " + clientId + " n'est pas valide pour effectuer une réservation");
        this.clientId = clientId;
    }
    
    public ClientNonValideException(Long clientId, String message) {
        super("CLIENT_NON_VALIDE", message);
        this.clientId = clientId;
    }
    
    public Long getClientId() {
        return clientId;
    }
}
