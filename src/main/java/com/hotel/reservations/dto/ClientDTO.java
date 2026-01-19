package com.hotel.reservations.dto;

/**
 * DTO représentant les informations d'un client provenant du Service Clients.
 */
public class ClientDTO {
    
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private boolean fraisImpayes;
    private int nombreSejours;
    
    // Constructeurs
    public ClientDTO() {
    }
    
    public ClientDTO(Long id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public boolean isFraisImpayes() {
        return fraisImpayes;
    }
    
    public void setFraisImpayes(boolean fraisImpayes) {
        this.fraisImpayes = fraisImpayes;
    }
    
    public int getNombreSejours() {
        return nombreSejours;
    }
    
    public void setNombreSejours(int nombreSejours) {
        this.nombreSejours = nombreSejours;
    }
    
    public String getNomComplet() {
        return prenom + " " + nom;
    }
}
