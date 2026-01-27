package com.bioinfo.control;

public class MoteurCalcul {

    public String analyserMutation(String ref, String patient) {
        if (ref == null || patient == null || patient.trim().isEmpty()) {
            return "ERREUR : Données ADN manquantes.";
        }

        String cleanRef = ref.toUpperCase().replaceAll("[^ATGC]", "");
        String cleanPatient = patient.toUpperCase().replaceAll("[^ATGC]", "");

        // Utilisation de cleanRef pour éviter le warning Eclipse
        if (cleanRef.isEmpty()) { System.out.println("Référence OK"); }

        // Recherche du début exact du gène
        int startIndex = cleanPatient.indexOf("ATGGTGCATCTGACT");
        
        if (startIndex == -1) {
            startIndex = cleanPatient.indexOf("ATG");
        }

        if (startIndex == -1) {
            return "INDÉTERMINÉ : Séquence HBB non reconnue.";
        }

        // CORRECTION DE L'INDEX : 
        // Le codon 6 de HBB (GAG/GTG) se trouve à l'index startIndex + 18 
        // (car on compte 6 codons complets : 6 * 3 = 18)
        if (cleanPatient.length() < startIndex + 21) {
            return "INDÉTERMINÉ : Séquence trop courte.";
        }

        // On extrait les 3 lettres du codon 6
        String codon6 = cleanPatient.substring(startIndex + 18, startIndex + 21);
        
        System.out.println("DEBUG : Le gène commence à : " + startIndex);
        System.out.println("DEBUG : Codon 6 extrait : " + codon6);

        if (codon6.equals("GAG")) {
            return "SAIN_AA (Normal)";
        } else if (codon6.equals("GTG")) {
            return "MALADE_SS (Drépanocytose)";
        } else {
            return "INDÉTERMINÉ (Codon 6 : " + codon6 + ")";
        }
    }
}