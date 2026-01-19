package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.ReservationDTO;
import com.hotel.reservations.entity.Reservation;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper pour convertir entre Reservation et ReservationDTO.
 */
@ApplicationScoped
public class ReservationMapper {
    
    /**
     * Convertit une entité Reservation en DTO.
     */
    public ReservationDTO toDTO(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setClientId(reservation.getClientId());
        dto.setChambreId(reservation.getChambreId());
        dto.setDateDebut(reservation.getDateDebut());
        dto.setDateFin(reservation.getDateFin());
        dto.setStatut(reservation.getStatut());
        dto.setMontantTotal(reservation.getMontantTotal());
        dto.setPrixParNuit(reservation.getPrixParNuit());
        dto.setRemarques(reservation.getRemarques());
        dto.setNombreNuits(reservation.getNombreNuits());
        
        return dto;
    }
    
    /**
     * Convertit un DTO en entité Reservation.
     */
    public Reservation toEntity(ReservationDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Reservation reservation = new Reservation();
        reservation.setClientId(dto.getClientId());
        reservation.setChambreId(dto.getChambreId());
        reservation.setDateDebut(dto.getDateDebut());
        reservation.setDateFin(dto.getDateFin());
        reservation.setRemarques(dto.getRemarques());
        
        if (dto.getStatut() != null) {
            reservation.setStatut(dto.getStatut());
        }
        if (dto.getPrixParNuit() != null) {
            reservation.setPrixParNuit(dto.getPrixParNuit());
        }
        if (dto.getMontantTotal() != null) {
            reservation.setMontantTotal(dto.getMontantTotal());
        }
        
        return reservation;
    }
    
    /**
     * Met à jour une entité existante avec les données du DTO.
     */
    public void updateEntity(Reservation reservation, ReservationDTO dto) {
        if (reservation == null || dto == null) {
            return;
        }
        
        if (dto.getClientId() != null) {
            reservation.setClientId(dto.getClientId());
        }
        if (dto.getChambreId() != null) {
            reservation.setChambreId(dto.getChambreId());
        }
        if (dto.getDateDebut() != null) {
            reservation.setDateDebut(dto.getDateDebut());
        }
        if (dto.getDateFin() != null) {
            reservation.setDateFin(dto.getDateFin());
        }
        if (dto.getStatut() != null) {
            reservation.setStatut(dto.getStatut());
        }
        if (dto.getRemarques() != null) {
            reservation.setRemarques(dto.getRemarques());
        }
        if (dto.getPrixParNuit() != null) {
            reservation.setPrixParNuit(dto.getPrixParNuit());
            reservation.calculerMontantTotal();
        }
    }
}
