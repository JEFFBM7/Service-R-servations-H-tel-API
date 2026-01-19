package com.hotel.reservations.exception;

/**
 * Exception levée lorsqu'un service externe n'est pas disponible.
 */
public class ServiceExterneException extends ReservationException {
    
    private final String serviceName;
    
    public ServiceExterneException(String serviceName) {
        super("SERVICE_EXTERNE_INDISPONIBLE", "Le service " + serviceName + " n'est pas disponible");
        this.serviceName = serviceName;
    }
    
    public ServiceExterneException(String serviceName, String message) {
        super("SERVICE_EXTERNE_INDISPONIBLE", message);
        this.serviceName = serviceName;
    }
    
    public ServiceExterneException(String serviceName, String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
    }
    
    public String getServiceName() {
        return serviceName;
    }
}
