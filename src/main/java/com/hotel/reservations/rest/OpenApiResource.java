package com.hotel.reservations.rest;

import io.smallrye.openapi.api.OpenApiDocument;
import io.smallrye.openapi.runtime.OpenApiProcessor;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import io.smallrye.openapi.runtime.scanner.AnnotationScannerExtension;
import io.smallrye.openapi.runtime.scanner.FilteredIndexView;
import io.smallrye.openapi.runtime.scanner.OpenApiAnnotationScanner;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Endpoint pour exposer la documentation OpenAPI.
 */
@Path("/openapi")
public class OpenApiResource {

    private static final Logger LOGGER = Logger.getLogger(OpenApiResource.class.getName());

    @GET
    @Produces({MediaType.APPLICATION_JSON, "application/yaml"})
    public String getOpenApi() {
        return getOpenApiSpec();
    }

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String getOpenApiJson() {
        return getOpenApiSpec();
    }

    private String getOpenApiSpec() {
        return """
            {
              "openapi": "3.0.3",
              "info": {
                "title": "Service Réservations Hôtel API",
                "description": "API REST pour la gestion des réservations d'un hôtel",
                "version": "1.0.0",
                "contact": {
                  "name": "Équipe Développement Hôtel",
                  "email": "dev@hotel.com"
                }
              },
              "servers": [
                {
                  "url": "http://localhost:8080/service-reservations/api",
                  "description": "Serveur de développement"
                }
              ],
              "tags": [
                {"name": "Réservations", "description": "Opérations CRUD sur les réservations"},
                {"name": "Cycle de vie", "description": "Confirmation, check-in, check-out"},
                {"name": "Rapports", "description": "Génération de rapports"}
              ],
              "paths": {
                "/reservations": {
                  "get": {
                    "tags": ["Réservations"],
                    "summary": "Lister toutes les réservations",
                    "description": "Récupère la liste de toutes les réservations. Peut être filtrée par statut ou clientId.",
                    "parameters": [
                      {
                        "name": "statut",
                        "in": "query",
                        "description": "Filtrer par statut (EN_ATTENTE, CONFIRMEE, EN_COURS, TERMINEE, ANNULEE)",
                        "schema": {"type": "string", "enum": ["EN_ATTENTE", "CONFIRMEE", "EN_COURS", "TERMINEE", "ANNULEE"]}
                      },
                      {
                        "name": "clientId",
                        "in": "query",
                        "description": "Filtrer par ID client",
                        "schema": {"type": "integer"}
                      }
                    ],
                    "responses": {
                      "200": {
                        "description": "Liste des réservations",
                        "content": {
                          "application/json": {
                            "schema": {"type": "array", "items": {"$ref": "#/components/schemas/Reservation"}}
                          }
                        }
                      }
                    }
                  },
                  "post": {
                    "tags": ["Réservations"],
                    "summary": "Créer une réservation",
                    "description": "Crée une nouvelle réservation pour un client et une chambre",
                    "requestBody": {
                      "required": true,
                      "content": {
                        "application/json": {
                          "schema": {"$ref": "#/components/schemas/ReservationInput"},
                          "example": {
                            "clientId": 1,
                            "chambreId": 101,
                            "dateDebut": "2026-01-25",
                            "dateFin": "2026-01-28",
                            "remarques": "Chambre avec vue"
                          }
                        }
                      }
                    },
                    "responses": {
                      "201": {
                        "description": "Réservation créée",
                        "content": {"application/json": {"schema": {"$ref": "#/components/schemas/Reservation"}}}
                      },
                      "400": {"description": "Données invalides"},
                      "409": {"description": "Chambre non disponible"}
                    }
                  }
                },
                "/reservations/{id}": {
                  "get": {
                    "tags": ["Réservations"],
                    "summary": "Consulter une réservation",
                    "parameters": [{"name": "id", "in": "path", "required": true, "schema": {"type": "integer"}}],
                    "responses": {
                      "200": {"description": "Détails de la réservation", "content": {"application/json": {"schema": {"$ref": "#/components/schemas/Reservation"}}}},
                      "404": {"description": "Réservation non trouvée"}
                    }
                  },
                  "put": {
                    "tags": ["Réservations"],
                    "summary": "Modifier une réservation",
                    "parameters": [{"name": "id", "in": "path", "required": true, "schema": {"type": "integer"}}],
                    "requestBody": {"required": true, "content": {"application/json": {"schema": {"$ref": "#/components/schemas/ReservationInput"}}}},
                    "responses": {
                      "200": {"description": "Réservation modifiée"},
                      "400": {"description": "Modification impossible"},
                      "404": {"description": "Réservation non trouvée"}
                    }
                  },
                  "delete": {
                    "tags": ["Réservations"],
                    "summary": "Annuler une réservation",
                    "parameters": [{"name": "id", "in": "path", "required": true, "schema": {"type": "integer"}}],
                    "responses": {
                      "200": {"description": "Réservation annulée"},
                      "400": {"description": "Annulation impossible"},
                      "404": {"description": "Réservation non trouvée"}
                    }
                  }
                },
                "/reservations/{id}/confirmer": {
                  "post": {
                    "tags": ["Cycle de vie"],
                    "summary": "Confirmer une réservation",
                    "parameters": [{"name": "id", "in": "path", "required": true, "schema": {"type": "integer"}}],
                    "responses": {
                      "200": {"description": "Réservation confirmée"},
                      "400": {"description": "Confirmation impossible"},
                      "404": {"description": "Réservation non trouvée"}
                    }
                  }
                },
                "/reservations/{id}/checkin": {
                  "post": {
                    "tags": ["Cycle de vie"],
                    "summary": "Effectuer le check-in",
                    "parameters": [{"name": "id", "in": "path", "required": true, "schema": {"type": "integer"}}],
                    "responses": {
                      "200": {"description": "Check-in effectué"},
                      "400": {"description": "Check-in impossible"},
                      "404": {"description": "Réservation non trouvée"}
                    }
                  }
                },
                "/reservations/{id}/checkout": {
                  "post": {
                    "tags": ["Cycle de vie"],
                    "summary": "Effectuer le check-out",
                    "parameters": [{"name": "id", "in": "path", "required": true, "schema": {"type": "integer"}}],
                    "responses": {
                      "200": {"description": "Check-out effectué"},
                      "400": {"description": "Check-out impossible"},
                      "404": {"description": "Réservation non trouvée"}
                    }
                  }
                },
                "/reservations/rapport": {
                  "get": {
                    "tags": ["Rapports"],
                    "summary": "Générer le rapport des réservations",
                    "description": "Génère un rapport avec les occupations actuelles et les réservations à venir",
                    "responses": {
                      "200": {
                        "description": "Rapport généré",
                        "content": {"application/json": {"schema": {"$ref": "#/components/schemas/Rapport"}}}
                      }
                    }
                  }
                }
              },
              "components": {
                "schemas": {
                  "Reservation": {
                    "type": "object",
                    "properties": {
                      "id": {"type": "integer", "example": 1},
                      "clientId": {"type": "integer", "example": 1},
                      "chambreId": {"type": "integer", "example": 101},
                      "dateDebut": {"type": "string", "format": "date", "example": "2026-01-25"},
                      "dateFin": {"type": "string", "format": "date", "example": "2026-01-28"},
                      "statut": {"type": "string", "enum": ["EN_ATTENTE", "CONFIRMEE", "EN_COURS", "TERMINEE", "ANNULEE"]},
                      "montantTotal": {"type": "number", "example": 360.0},
                      "prixParNuit": {"type": "number", "example": 120.0},
                      "nombreNuits": {"type": "integer", "example": 3},
                      "remarques": {"type": "string"},
                      "nomClient": {"type": "string"},
                      "numeroChambre": {"type": "string"},
                      "typeChambre": {"type": "string"}
                    }
                  },
                  "ReservationInput": {
                    "type": "object",
                    "required": ["clientId", "chambreId", "dateDebut", "dateFin"],
                    "properties": {
                      "clientId": {"type": "integer", "description": "ID du client"},
                      "chambreId": {"type": "integer", "description": "ID de la chambre"},
                      "dateDebut": {"type": "string", "format": "date"},
                      "dateFin": {"type": "string", "format": "date"},
                      "remarques": {"type": "string", "maxLength": 500}
                    }
                  },
                  "Rapport": {
                    "type": "object",
                    "properties": {
                      "totalReservations": {"type": "integer"},
                      "occupationsActuelles": {"type": "integer"},
                      "reservationsAVenir": {"type": "integer"},
                      "reservationsAnnulees": {"type": "integer"},
                      "dateGeneration": {"type": "string"},
                      "listeOccupationsActuelles": {"type": "array", "items": {"$ref": "#/components/schemas/Reservation"}},
                      "listeReservationsAVenir": {"type": "array", "items": {"$ref": "#/components/schemas/Reservation"}}
                    }
                  }
                }
              }
            }
            """;
    }
}
