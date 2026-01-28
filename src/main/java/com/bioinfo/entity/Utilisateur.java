package com.bioinfo.entity;

/**
 * Entité représentant un utilisateur/patient du système.
 * Renommé de Patient.java pour correspondre au diagramme BCE.
 */
public class Utilisateur {
    // Attributs privés (Encapsulation)
    private String idUtilisateur; // Identifiant unique (ex: CIN ou code patient)
    private String nom; // Nom de famille
    private String infosSaisies; // Données brutes du formulaire (Audit/Trace)

    // Constructeur : pour créer un objet Utilisateur avec toutes ses données
    public Utilisateur(String idUtilisateur, String nom, String infosSaisies) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.infosSaisies = infosSaisies;
    }

    // Constructeur simplifié avec seulement le nom
    public Utilisateur(String nom) {
        this.idUtilisateur = null;
        this.nom = nom;
        this.infosSaisies = "";
    }

    // Getters : pour permettre à l'Orchestrateur de lire les données
    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public String getInfosSaisies() {
        return infosSaisies;
    }

    // Setters : pour permettre de modifier les données si besoin
    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setInfosSaisies(String infosSaisies) {
        this.infosSaisies = infosSaisies;
    }
}
