package com.hotel.reservations.rest;

import com.hotel.reservations.config.AppConfig;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Proxy REST pour contourner les restrictions CORS
 * lors de l'appel au Service Gestion des Chambres.
 */
@Path("/chambres")
@Produces(MediaType.APPLICATION_JSON)
public class ChambresProxyResource {
    
    private static final Logger LOGGER = Logger.getLogger(ChambresProxyResource.class.getName());
    
    @Inject
    private AppConfig config;
    
    /**
     * Récupère toutes les chambres depuis le service externe.
     * GET /api/chambres
     */
    @GET
    public Response getAllChambres() {
        LOGGER.info("GET /chambres - Proxy vers service externe");
        
        Client client = null;
        try {
            client = ClientBuilder.newBuilder()
                    .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .build();
            
            String url = config.getChambreServiceUrl();
            LOGGER.info("Appel proxy vers: " + url);
            
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            String json = response.readEntity(String.class);
            return Response.ok(json).build();
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur proxy chambres", e);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Service des chambres indisponible\"}")
                    .build();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
    
    /**
     * Récupère une chambre spécifique depuis le service externe.
     * GET /api/chambres/{id}
     */
    @GET
    @Path("/{id}")
    public Response getChambre(@PathParam("id") Long id) {
        LOGGER.info("GET /chambres/" + id + " - Proxy vers service externe");
        
        Client client = null;
        try {
            client = ClientBuilder.newBuilder()
                    .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .build();
            
            String url = config.getChambreServiceUrl() + "/" + id;
            LOGGER.info("Appel proxy vers: " + url);
            
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 404) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Chambre non trouvée\"}")
                        .build();
            }
            
            String json = response.readEntity(String.class);
            return Response.ok(json).build();
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur proxy chambre " + id, e);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Service des chambres indisponible\"}")
                    .build();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
