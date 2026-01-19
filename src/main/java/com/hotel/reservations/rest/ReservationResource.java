package com.hotel.reservations.rest;

import com.hotel.reservations.dto.ErrorResponseDTO;
import com.hotel.reservations.dto.RapportReservationsDTO;
import com.hotel.reservations.dto.ReservationDTO;
import com.hotel.reservations.entity.StatutReservation;
import com.hotel.reservations.exception.ChambreNonDisponibleException;
import com.hotel.reservations.exception.ClientNonValideException;
import com.hotel.reservations.exception.ReservationException;
import com.hotel.reservations.exception.ReservationNotFoundException;
import com.hotel.reservations.service.ReservationService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * API REST pour la gestion des réservations.
 */
@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {
    
    private static final Logger LOGGER = Logger.getLogger(ReservationResource.class.getName());
    
    @Inject
    private ReservationService reservationService;
    
    @Context
    private UriInfo uriInfo;
    
    /**
     * Crée une nouvelle réservation.
     * POST /api/reservations
     */
    @POST
    public Response creerReservation(@Valid @NotNull ReservationDTO dto) {
        LOGGER.info("POST /reservations - Création d'une réservation");
        try {
            ReservationDTO created = reservationService.creerReservation(dto);
            URI location = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(created.getId()))
                    .build();
            return Response.created(location).entity(created).build();
        } catch (ChambreNonDisponibleException e) {
            LOGGER.log(Level.WARNING, "Chambre non disponible", e);
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponseDTO(409, e.getMessage(), e.getCode()))
                    .build();
        } catch (ClientNonValideException e) {
            LOGGER.log(Level.WARNING, "Client non valide", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO(400, e.getMessage(), e.getCode()))
                    .build();
        } catch (ReservationException e) {
            LOGGER.log(Level.WARNING, "Erreur de réservation", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO(400, e.getMessage(), e.getCode()))
                    .build();
        }
    }
    
    /**
     * Récupère une réservation par son ID.
     * GET /api/reservations/{id}
     */
    @GET
    @Path("/{id}")
    public Response getReservation(@PathParam("id") Long id) {
        LOGGER.info("GET /reservations/" + id);
        try {
            ReservationDTO reservation = reservationService.getReservation(id);
            return Response.ok(reservation).build();
        } catch (ReservationNotFoundException e) {
            LOGGER.log(Level.WARNING, "Réservation non trouvée: " + id, e);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponseDTO(404, e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Liste toutes les réservations.
     * GET /api/reservations
     * GET /api/reservations?statut=CONFIRMEE
     * GET /api/reservations?clientId=1
     */
    @GET
    public Response listerReservations(
            @QueryParam("statut") String statut,
            @QueryParam("clientId") Long clientId) {
        LOGGER.info("GET /reservations - statut=" + statut + ", clientId=" + clientId);
        
        List<ReservationDTO> reservations;
        
        if (clientId != null) {
            reservations = reservationService.listerReservationsParClient(clientId);
        } else if (statut != null && !statut.isEmpty()) {
            try {
                StatutReservation statutEnum = StatutReservation.valueOf(statut.toUpperCase());
                reservations = reservationService.listerReservationsParStatut(statutEnum);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponseDTO(400, "Statut invalide: " + statut))
                        .build();
            }
        } else {
            reservations = reservationService.listerReservations();
        }
        
        return Response.ok(reservations).build();
    }
    
    /**
     * Modifie une réservation existante.
     * PUT /api/reservations/{id}
     */
    @PUT
    @Path("/{id}")
    public Response modifierReservation(@PathParam("id") Long id, @Valid ReservationDTO dto) {
        LOGGER.info("PUT /reservations/" + id);
        try {
            ReservationDTO updated = reservationService.modifierReservation(id, dto);
            return Response.ok(updated).build();
        } catch (ReservationNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponseDTO(404, e.getMessage()))
                    .build();
        } catch (ChambreNonDisponibleException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponseDTO(409, e.getMessage(), e.getCode()))
                    .build();
        } catch (ReservationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO(400, e.getMessage(), e.getCode()))
                    .build();
        }
    }
    
    /**
     * Annule une réservation.
     * DELETE /api/reservations/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response annulerReservation(@PathParam("id") Long id) {
        LOGGER.info("DELETE /reservations/" + id);
        try {
            ReservationDTO cancelled = reservationService.annulerReservation(id);
            return Response.ok(cancelled).build();
        } catch (ReservationNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponseDTO(404, e.getMessage()))
                    .build();
        } catch (ReservationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO(400, e.getMessage(), e.getCode()))
                    .build();
        }
    }
    
    /**
     * Confirme une réservation.
     * POST /api/reservations/{id}/confirmer
     */
    @POST
    @Path("/{id}/confirmer")
    public Response confirmerReservation(@PathParam("id") Long id) {
        LOGGER.info("POST /reservations/" + id + "/confirmer");
        try {
            ReservationDTO confirmed = reservationService.confirmerReservation(id);
            return Response.ok(confirmed).build();
        } catch (ReservationNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponseDTO(404, e.getMessage()))
                    .build();
        } catch (ReservationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO(400, e.getMessage(), e.getCode()))
                    .build();
        }
    }
    
    /**
     * Effectue le check-in.
     * POST /api/reservations/{id}/checkin
     */
    @POST
    @Path("/{id}/checkin")
    public Response checkIn(@PathParam("id") Long id) {
        LOGGER.info("POST /reservations/" + id + "/checkin");
        try {
            ReservationDTO result = reservationService.checkIn(id);
            return Response.ok(result).build();
        } catch (ReservationNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponseDTO(404, e.getMessage()))
                    .build();
        } catch (ReservationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO(400, e.getMessage(), e.getCode()))
                    .build();
        }
    }
    
    /**
     * Effectue le check-out.
     * POST /api/reservations/{id}/checkout
     */
    @POST
    @Path("/{id}/checkout")
    public Response checkOut(@PathParam("id") Long id) {
        LOGGER.info("POST /reservations/" + id + "/checkout");
        try {
            ReservationDTO result = reservationService.checkOut(id);
            return Response.ok(result).build();
        } catch (ReservationNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponseDTO(404, e.getMessage()))
                    .build();
        } catch (ReservationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO(400, e.getMessage(), e.getCode()))
                    .build();
        }
    }
    
    /**
     * Génère le rapport des réservations.
     * GET /api/reservations/rapport
     */
    @GET
    @Path("/rapport")
    public Response genererRapport() {
        LOGGER.info("GET /reservations/rapport");
        RapportReservationsDTO rapport = reservationService.genererRapport();
        return Response.ok(rapport).build();
    }
}
