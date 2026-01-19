package com.hotel.reservations.rest;

import com.hotel.reservations.dto.ErrorResponseDTO;
import com.hotel.reservations.exception.ReservationException;
import com.hotel.reservations.exception.ReservationNotFoundException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestionnaire global des exceptions pour l'API REST.
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {
    
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());
    
    @Override
    public Response toResponse(Exception exception) {
        LOGGER.log(Level.SEVERE, "Exception non gérée", exception);
        
        if (exception instanceof ReservationNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponseDTO(404, exception.getMessage()))
                    .build();
        }
        
        if (exception instanceof ReservationException) {
            ReservationException re = (ReservationException) exception;
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO(400, re.getMessage(), re.getCode()))
                    .build();
        }
        
        if (exception instanceof jakarta.validation.ConstraintViolationException) {
            jakarta.validation.ConstraintViolationException cve = 
                (jakarta.validation.ConstraintViolationException) exception;
            StringBuilder messages = new StringBuilder();
            cve.getConstraintViolations().forEach(v -> 
                messages.append(v.getPropertyPath()).append(": ").append(v.getMessage()).append("; ")
            );
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO(400, "Erreur de validation", messages.toString()))
                    .build();
        }
        
        // Erreur interne par défaut
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponseDTO(500, "Erreur interne du serveur", exception.getMessage()))
                .build();
    }
}
