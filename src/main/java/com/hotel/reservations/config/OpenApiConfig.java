package com.hotel.reservations.config;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Configuration OpenAPI/Swagger pour le Service Réservations.
 */
@OpenAPIDefinition(
    info = @Info(
        title = "Service Réservations Hôtel API",
        version = "1.0.0",
        description = "API REST pour la gestion des réservations d'un hôtel. " +
                      "Ce service permet de créer, modifier, annuler des réservations " +
                      "et de gérer le cycle de vie complet (confirmation, check-in, check-out).",
        contact = @Contact(
            name = "Équipe Développement Hôtel",
            email = "dev@hotel.com"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080/service-reservations", description = "Serveur de développement"),
        @Server(url = "http://localhost:8080/service-reservations", description = "Serveur de production")
    },
    tags = {
        @Tag(name = "Réservations", description = "Opérations CRUD sur les réservations"),
        @Tag(name = "Cycle de vie", description = "Gestion du cycle de vie des réservations (confirmation, check-in, check-out)"),
        @Tag(name = "Rapports", description = "Génération de rapports")
    }
)
public class OpenApiConfig {
}
