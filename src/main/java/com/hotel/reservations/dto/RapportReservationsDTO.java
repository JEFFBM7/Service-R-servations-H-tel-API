package com.hotel.reservations.dto;

import java.util.List;

/**
 * DTO pour le rapport des réservations.
 */
public class RapportReservationsDTO {
    
    private int totalReservations;
    private int occupationsActuelles;
    private int reservationsAVenir;
    private int reservationsAnnulees;
    private List<ReservationDTO> listeOccupationsActuelles;
    private List<ReservationDTO> listeReservationsAVenir;
    private String dateGeneration;
    
    // Constructeurs
    public RapportReservationsDTO() {
    }
    
    // Getters et Setters
    public int getTotalReservations() {
        return totalReservations;
    }
    
    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }
    
    public int getOccupationsActuelles() {
        return occupationsActuelles;
    }
    
    public void setOccupationsActuelles(int occupationsActuelles) {
        this.occupationsActuelles = occupationsActuelles;
    }
    
    public int getReservationsAVenir() {
        return reservationsAVenir;
    }
    
    public void setReservationsAVenir(int reservationsAVenir) {
        this.reservationsAVenir = reservationsAVenir;
    }
    
    public int getReservationsAnnulees() {
        return reservationsAnnulees;
    }
    
    public void setReservationsAnnulees(int reservationsAnnulees) {
        this.reservationsAnnulees = reservationsAnnulees;
    }
    
    public List<ReservationDTO> getListeOccupationsActuelles() {
        return listeOccupationsActuelles;
    }
    
    public void setListeOccupationsActuelles(List<ReservationDTO> listeOccupationsActuelles) {
        this.listeOccupationsActuelles = listeOccupationsActuelles;
    }
    
    public List<ReservationDTO> getListeReservationsAVenir() {
        return listeReservationsAVenir;
    }
    
    public void setListeReservationsAVenir(List<ReservationDTO> listeReservationsAVenir) {
        this.listeReservationsAVenir = listeReservationsAVenir;
    }
    
    public String getDateGeneration() {
        return dateGeneration;
    }
    
    public void setDateGeneration(String dateGeneration) {
        this.dateGeneration = dateGeneration;
    }
}
