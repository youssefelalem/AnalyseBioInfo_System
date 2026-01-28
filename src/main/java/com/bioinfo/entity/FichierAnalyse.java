package com.bioinfo.entity;

/**
 * Entité représentant un fichier d'analyse FASTA importé.
 * Conforme au diagramme de classes BCE.
 */
public class FichierAnalyse {
    private String contenuBrut; // Contenu complet du fichier FASTA importé

    // Constructeur
    public FichierAnalyse(String contenuBrut) {
        this.contenuBrut = contenuBrut;
    }

    /**
     * Extrait les données de l'utilisateur du fichier FASTA.
     * 
     * @return String contenant les métadonnées (header) du fichier
     */
    public String extraireDonneesUtilisateur() {
        if (contenuBrut == null || contenuBrut.isEmpty()) {
            return "";
        }
        // Extraire la première ligne (header FASTA commençant par >)
        String[] lignes = contenuBrut.split("\n");
        if (lignes.length > 0 && lignes[0].startsWith(">")) {
            return lignes[0].substring(1).trim();
        }
        return "";
    }

    /**
     * Extrait les deux séquences ADN pour la diploïdie.
     * 
     * @return String[] avec [0] = séquence allèle A, [1] = séquence allèle B
     */
    public String[] extraireDeuxSequences() {
        if (contenuBrut == null || contenuBrut.isEmpty()) {
            return new String[] { "", "" };
        }

        StringBuilder sequenceBuilder = new StringBuilder();
        String[] lignes = contenuBrut.split("\n");

        for (String ligne : lignes) {
            if (!ligne.startsWith(">")) {
                sequenceBuilder.append(ligne.trim().toUpperCase().replaceAll("[^ATGC]", ""));
            }
        }

        String sequenceComplete = sequenceBuilder.toString();
        int milieu = sequenceComplete.length() / 2;

        // Pour la diploïdie : on divise en deux allèles
        String sequenceA = sequenceComplete.substring(0, milieu);
        String sequenceB = sequenceComplete.substring(milieu);

        return new String[] { sequenceA, sequenceB };
    }

    /**
     * Extrait la séquence brute normalisée du fichier.
     * 
     * @return String la séquence ADN nettoyée
     */
    public String extraireSequenceNormalisee() {
        if (contenuBrut == null || contenuBrut.isEmpty()) {
            return "";
        }

        StringBuilder sequenceBuilder = new StringBuilder();
        String[] lignes = contenuBrut.split("\n");

        for (String ligne : lignes) {
            if (!ligne.startsWith(">")) {
                sequenceBuilder.append(ligne.trim().toUpperCase().replaceAll("[^ATGC]", ""));
            }
        }

        return sequenceBuilder.toString();
    }

    // Getters
    public String getContenuBrut() {
        return contenuBrut;
    }
}
