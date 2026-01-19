package com.hotel.reservations.client;

import com.hotel.reservations.config.AppConfig;
import com.hotel.reservations.dto.ClientDTO;
import com.hotel.reservations.exception.ClientNonValideException;
import com.hotel.reservations.exception.ServiceExterneException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.StringReader;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client REST pour communiquer avec le Service Gestion des Clients.
 */
@ApplicationScoped
public class ClientServiceClient {
    
    private static final Logger LOGGER = Logger.getLogger(ClientServiceClient.class.getName());
    
    @Inject
    private AppConfig config;
    
    /**
     * Récupère les informations d'un client.
     */
    public ClientDTO getClient(Long clientId) {
        if (config.isModeDeveloppement()) {
            LOGGER.info("[MODE DEV] Simulation récupération client ID: " + clientId);
            return creerClientMock(clientId);
        }
        
        Client client = null;
        try {
            client = ClientBuilder.newBuilder()
                    .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .build();
            
            String url = config.getClientServiceUrl() + "/" + clientId;
            LOGGER.info("Appel GET: " + url);
            
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                return parseClientFromJson(json);
            } else if (response.getStatus() == 404) {
                LOGGER.warning("Client non trouvé: " + clientId);
                throw new ClientNonValideException(clientId, "Client non trouvé");
            } else {
                LOGGER.warning("Erreur lors de la récupération du client: " + response.getStatus());
                throw new ServiceExterneException("Service Clients", 
                    "Erreur HTTP " + response.getStatus());
            }
        } catch (ClientNonValideException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur de communication avec le service Clients", e);
            // En cas d'erreur, retourner un mock pour ne pas bloquer
            return creerClientMock(clientId);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
    
    /**
     * Vérifie les antécédents d'un client (frais impayés, historique).
     */
    public boolean verifierAntecedents(Long clientId) {
        if (config.isModeDeveloppement()) {
            LOGGER.info("[MODE DEV] Simulation vérification antécédents client ID: " + clientId);
            return true; // Client valide en mode dev
        }
        
        Client client = null;
        try {
            client = ClientBuilder.newBuilder()
                    .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .build();
            
            String url = config.getClientServiceUrl() + "/" + clientId + "/antecedents";
            LOGGER.info("Appel GET: " + url);
            
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            
            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                try (JsonReader reader = Json.createReader(new StringReader(json))) {
                    JsonObject obj = reader.readObject();
                    boolean fraisImpayes = obj.getBoolean("fraisImpayes", false);
                    return !fraisImpayes; // Valide si pas de frais impayés
                }
            } else {
                LOGGER.warning("Erreur lors de la vérification des antécédents: " + response.getStatus());
                return true; // Par défaut, considérer comme valide
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur de communication avec le service Clients", e);
            return true; // Par défaut, considérer comme valide
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
    
    /**
     * Vérifie si un client existe.
     */
    public boolean clientExiste(Long clientId) {
        if (config.isModeDeveloppement()) {
            LOGGER.info("[MODE DEV] Simulation vérification existence client ID: " + clientId);
            return true;
        }
        
        Client client = null;
        try {
            client = ClientBuilder.newBuilder()
                    .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                    .build();
            
            String url = config.getClientServiceUrl() + "/" + clientId;
            LOGGER.info("Appel HEAD: " + url);
            
            Response response = client.target(url)
                    .request()
                    .head();
            
            return response.getStatus() == 200;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur de communication avec le service Clients", e);
            return true; // Par défaut, considérer comme existant
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
    
    /**
     * Crée un client mock pour le mode développement.
     */
    private ClientDTO creerClientMock(Long clientId) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientId);
        clientDTO.setNom("Client Test");
        clientDTO.setPrenom("Utilisateur");
        clientDTO.setEmail("client" + clientId + "@hotel.com");
        clientDTO.setTelephone("+33600000000");
        clientDTO.setFraisImpayes(false);
        clientDTO.setNombreSejours(5);
        return clientDTO;
    }
    
    /**
     * Parse un client depuis JSON.
     */
    private ClientDTO parseClientFromJson(String json) {
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            JsonObject obj = reader.readObject();
            ClientDTO clientDTO = new ClientDTO();
            clientDTO.setId(obj.getJsonNumber("id").longValue());
            clientDTO.setNom(obj.getString("nom", null));
            clientDTO.setPrenom(obj.getString("prenom", null));
            clientDTO.setEmail(obj.getString("email", null));
            clientDTO.setTelephone(obj.getString("telephone", null));
            clientDTO.setFraisImpayes(obj.getBoolean("fraisImpayes", false));
            clientDTO.setNombreSejours(obj.getInt("nombreSejours", 0));
            return clientDTO;
        }
    }
}
