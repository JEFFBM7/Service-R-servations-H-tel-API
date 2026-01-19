package com.hotel.reservations.client;

import com.hotel.reservations.config.AppConfig;
import com.hotel.reservations.dto.ChambreDTO;
import com.hotel.reservations.exception.ServiceExterneException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client REST pour communiquer avec le Service Gestion des Chambres.
 */
@ApplicationScoped
public class ChambreServiceClient {
    
    private static final Logger LOGGER = Logger.getLogger(ChambreServiceClient.class.getName());
    
    @Inject
    private AppConfig config;
    
    /**
     * Récupère les informations d'une chambre.
     */
    public ChambreDTO getChambre(Long chambreId) {
        if (config.isModeDeveloppement()) {
            LOGGER.info("[MODE DEV] Simulation récupération chambre ID: " + chambreId);
            return creerChambreMock(chambreId);
        }
        
        Client client = null;
        try {
            client = ClientBuilder.newBuilder()
                    .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .build();
            
            String url = config.getChambreServiceUrl() + "/" + chambreId;
            LOGGER.info("Appel GET: " + url);
            
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return parseChambreFromJson(json);
            } else if (response.getStatus() == 404) {
                LOGGER.warning("Chambre non trouvée: " + chambreId);
                return null;
            } else {
                LOGGER.warning("Erreur lors de la récupération de la chambre: " + response.getStatus());
                throw new ServiceExterneException("Service Chambres", 
                    "Erreur HTTP " + response.getStatus());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur de communication avec le service Chambres", e);
            // En cas d'erreur, retourner un mock pour ne pas bloquer
            return creerChambreMock(chambreId);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
    
    /**
     * Vérifie la disponibilité d'une chambre pour des dates données.
     */
    public boolean verifierDisponibilite(Long chambreId, LocalDate dateDebut, LocalDate dateFin) {
        if (config.isModeDeveloppement()) {
            LOGGER.info("[MODE DEV] Simulation vérification disponibilité chambre ID: " + chambreId);
            return true; // Toujours disponible en mode dev
        }
        
        Client client = null;
        try {
            client = ClientBuilder.newBuilder()
                    .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .build();
            
            String url = config.getChambreServiceUrl() + "/" + chambreId + "/disponibilite";
            LOGGER.info("Appel GET: " + url);
            
            Response response = client.target(url)
                    .queryParam("dateDebut", dateDebut.toString())
                    .queryParam("dateFin", dateFin.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                try (JsonReader reader = Json.createReader(new StringReader(json))) {
                    JsonObject obj = reader.readObject();
                    return obj.getBoolean("disponible", true);
                }
            } else {
                LOGGER.warning("Erreur lors de la vérification disponibilité: " + response.getStatus());
                return true; // Par défaut, considérer disponible
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur de communication avec le service Chambres", e);
            return true; // Par défaut, considérer disponible
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
    
    /**
     * Met à jour le statut d'une chambre.
     */
    public void mettreAJourStatutChambre(Long chambreId, String nouveauStatut) {
        if (config.isModeDeveloppement()) {
            LOGGER.info("[MODE DEV] Simulation mise à jour statut chambre ID: " + chambreId + " -> " + nouveauStatut);
            return;
        }
        
        Client client = null;
        try {
            client = ClientBuilder.newBuilder()
                    .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .build();
            
            String url = config.getChambreServiceUrl() + "/" + chambreId + "/statut";
            LOGGER.info("Appel PUT: " + url + " avec statut: " + nouveauStatut);
            
            JsonObject payload = Json.createObjectBuilder()
                    .add("statut", nouveauStatut)
                    .build();
            
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(payload.toString()));
            
            if (response.getStatus() >= 200 && response.getStatus() < 300) {
                LOGGER.info("Statut chambre mis à jour avec succès");
            } else {
                LOGGER.warning("Erreur lors de la mise à jour du statut: " + response.getStatus());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur de communication avec le service Chambres", e);
            // Ne pas lever d'exception pour ne pas bloquer la réservation
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
    
    /**
     * Crée une chambre mock pour le mode développement.
     */
    private ChambreDTO creerChambreMock(Long chambreId) {
        ChambreDTO chambre = new ChambreDTO();
        chambre.setId(chambreId);
        chambre.setNumero("CH-" + chambreId);
        chambre.setType("DOUBLE");
        chambre.setPrixParNuit(BigDecimal.valueOf(120.00));
        chambre.setStatut("LIBRE");
        chambre.setDisponible(true);
        chambre.setCapacite(2);
        return chambre;
    }
    
    /**
     * Parse une chambre depuis JSON.
     */
    private ChambreDTO parseChambreFromJson(String json) {
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            JsonObject obj = reader.readObject();
            ChambreDTO chambre = new ChambreDTO();
            chambre.setId(obj.getJsonNumber("id").longValue());
            chambre.setNumero(obj.getString("numero", null));
            chambre.setType(obj.getString("type", null));
            if (obj.containsKey("prixParNuit") && !obj.isNull("prixParNuit")) {
                chambre.setPrixParNuit(obj.getJsonNumber("prixParNuit").bigDecimalValue());
            }
            chambre.setStatut(obj.getString("statut", null));
            chambre.setDisponible(obj.getBoolean("disponible", true));
            return chambre;
        }
    }
}
