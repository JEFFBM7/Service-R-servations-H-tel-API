package com.hotel.reservations.config;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration de l'application chargée depuis application.properties.
 */
@ApplicationScoped
public class AppConfig {
    
    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());
    
    private Properties properties = new Properties();
    
    private String chambreServiceUrl;
    private String clientServiceUrl;
    private int connectTimeout;
    private int readTimeout;
    private boolean modeDeveloppement;
    
    @PostConstruct
    public void init() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                properties.load(is);
                loadConfiguration();
                LOGGER.info("Configuration chargée avec succès");
            } else {
                LOGGER.warning("Fichier application.properties non trouvé, utilisation des valeurs par défaut");
                loadDefaults();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de la configuration", e);
            loadDefaults();
        }
    }
    
    private void loadConfiguration() {
        chambreServiceUrl = properties.getProperty("service.chambres.url", "http://localhost:8080/service-chambres/api/chambres");
        clientServiceUrl = properties.getProperty("service.clients.url", "http://localhost:8080/service-clients/api/clients");
        connectTimeout = Integer.parseInt(properties.getProperty("service.timeout.connect", "5000"));
        readTimeout = Integer.parseInt(properties.getProperty("service.timeout.read", "10000"));
        modeDeveloppement = Boolean.parseBoolean(properties.getProperty("mode.developpement", "true"));
        
        LOGGER.info("Mode développement: " + modeDeveloppement);
        LOGGER.info("URL Service Chambres: " + chambreServiceUrl);
        LOGGER.info("URL Service Clients: " + clientServiceUrl);
    }
    
    private void loadDefaults() {
        chambreServiceUrl = "http://localhost:8080/service-chambres/api/chambres";
        clientServiceUrl = "http://localhost:8080/service-clients/api/clients";
        connectTimeout = 5000;
        readTimeout = 10000;
        modeDeveloppement = true;
    }
    
    // Getters
    public String getChambreServiceUrl() {
        return chambreServiceUrl;
    }
    
    public String getClientServiceUrl() {
        return clientServiceUrl;
    }
    
    public int getConnectTimeout() {
        return connectTimeout;
    }
    
    public int getReadTimeout() {
        return readTimeout;
    }
    
    public boolean isModeDeveloppement() {
        return modeDeveloppement;
    }
}
