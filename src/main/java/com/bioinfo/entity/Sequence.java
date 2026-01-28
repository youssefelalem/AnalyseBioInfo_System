package com.bioinfo.entity;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class Sequence {
    private String nucleotides;
    private String empreinteHash;

    // Constructeur utilisé lors de l'extraction ou du chargement depuis la BDD
    public Sequence(String nucleotides) {
        this.nucleotides = nucleotides;
        this.empreinteHash = genererHash(nucleotides);
    }

    /**
     * Calcule le hash SHA-256 de la séquence.
     * Utilisé pour la fonction verifierIntegrite() de l'Orchestrateur.
     */
    public String genererHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            // Conversion du tableau de bytes en format hexadécimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du calcul du Hash", e);
        }
    }

    /**
     * Calcule et vérifie l'intégrité de la séquence.
     * Conforme au diagramme BCE.
     * 
     * @return String le hash d'intégrité actuel
     */
    public String calculerIntegrite() {
        return genererHash(this.nucleotides);
    }

    /**
     * Vérifie si la séquence est intègre (hash correspondant).
     * 
     * @return boolean true si intègre
     */
    public boolean verifierIntegrite() {
        String hashActuel = genererHash(this.nucleotides);
        return hashActuel.equals(this.empreinteHash);
    }

    // Getters
    public String getNucleotides() {
        return nucleotides;
    }

    public String getEmpreinteHash() {
        return empreinteHash;
    }
}
