package com.bioinfo.entity;

public class Patient {
	// Attributs privés (Encapsulation)
    private String idUtilisateur; // Identifiant unique (ex: CIN ou code patient)
    private String nom;           // Nom de famille
    private String infosSaisies;  // Données brutes du formulaire (Audit/Trace)

    // Constructeur : pour créer un objet Patient avec toutes ses données
    public Patient(String idUtilisateur, String nom, String infosSaisies) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.infosSaisies = infosSaisies;
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
