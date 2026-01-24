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
 * lors de l'appel au Service Gestion des Clients.
 */
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
public class ClientsProxyResource {
    
    private static final Logger LOGGER = Logger.getLogger(ClientsProxyResource.class.getName());
    
    @Inject
    private AppConfig config;
    
    /**
     * Récupère tous les clients depuis le service externe.
     * GET /api/clients
     */
    @GET
    public Response getAllClients() {
        LOGGER.info("GET /clients - Proxy vers service externe");
        
        Client client = null;
        try {
            client = ClientBuilder.newBuilder()
                    .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .build();
            
            String url = config.getClientServiceUrl();
            LOGGER.info("Appel proxy vers: " + url);
            
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            String json = response.readEntity(String.class);
            return Response.ok(json).build();
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur proxy clients", e);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Service des clients indisponible\"}")
                    .build();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
    
    /**
     * Récupère un client spécifique depuis le service externe.
     * GET /api/clients/{id}
     */
    @GET
    @Path("/{id}")
    public Response getClient(@PathParam("id") Long id) {
        LOGGER.info("GET /clients/" + id + " - Proxy vers service externe");
        
        Client client = null;
        try {
            client = ClientBuilder.newBuilder()
                    .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .build();
            
            String url = config.getClientServiceUrl() + "/" + id;
            LOGGER.info("Appel proxy vers: " + url);
            
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 404) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Client non trouvé\"}")
                        .build();
            }
            
            String json = response.readEntity(String.class);
            return Response.ok(json).build();
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur proxy client " + id, e);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Service des clients indisponible\"}")
                    .build();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
    
    /**
     * Recherche des clients par nom ou téléphone.
     * GET /api/clients/search?q=...
     */
    @GET
    @Path("/search")
    public Response searchClients(@QueryParam("q") String query) {
        LOGGER.info("GET /clients/search?q=" + query + " - Proxy vers service externe");
        
        Client client = null;
        try {
            client = ClientBuilder.newBuilder()
                    .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .build();
            
            String url = config.getClientServiceUrl();
            LOGGER.info("Appel proxy vers: " + url);
            
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            String json = response.readEntity(String.class);
            return Response.ok(json).build();
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur proxy recherche clients", e);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Service des clients indisponible\"}")
                    .build();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
