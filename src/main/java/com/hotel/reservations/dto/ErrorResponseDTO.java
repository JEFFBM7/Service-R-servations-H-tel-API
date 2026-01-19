package com.hotel.reservations.dto;

import java.time.LocalDateTime;

/**
 * DTO pour les réponses d'erreur de l'API.
 */
public class ErrorResponseDTO {
    
    private int status;
    private String message;
    private String details;
    private String timestamp;
    private String path;
    
    // Constructeurs
    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now().toString();
    }
    
    public ErrorResponseDTO(int status, String message) {
        this();
        this.status = status;
        this.message = message;
    }
    
    public ErrorResponseDTO(int status, String message, String details) {
        this(status, message);
        this.details = details;
    }
    
    // Getters et Setters
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
}
