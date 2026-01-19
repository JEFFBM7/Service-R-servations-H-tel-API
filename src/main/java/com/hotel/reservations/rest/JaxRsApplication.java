package com.hotel.reservations.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configuration de l'application JAX-RS.
 * Définit le chemin de base de l'API REST.
 */
@ApplicationPath("/api")
public class JaxRsApplication extends Application {
    // Configuration automatique via CDI
    // Tous les endpoints seront accessibles sous /api/*
}
