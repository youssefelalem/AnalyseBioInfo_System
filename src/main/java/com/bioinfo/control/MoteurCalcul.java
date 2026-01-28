package com.bioinfo.control;

/**
 * Moteur de calcul pour l'analyse des mutations génétiques HBB.
 * Conforme au diagramme de classes BCE.
 */
public class MoteurCalcul {

    // Attributs conformes au diagramme BCE
    private String memoireSequenceUtilisateurA;
    private String memoireSequenceUtilisateurB;
    private String memoireSequenceNormale;
    private String sequenceAligneeA;
    private String sequenceAligneeB;
    private int nbMutationsDetectees;

    // Constructeur
    public MoteurCalcul() {
        this.memoireSequenceUtilisateurA = "";
        this.memoireSequenceUtilisateurB = "";
        this.memoireSequenceNormale = "";
        this.nbMutationsDetectees = 0;
    }

    /**
     * Charge les séquences en mémoire pour analyse.
     * 
     * @param seqA première séquence utilisateur (allèle A)
     * @param seqB deuxième séquence utilisateur (allèle B)
     * @param seqN séquence normale de référence
     */
    public void chargerEnMemoire(String seqA, String seqB, String seqN) {
        this.memoireSequenceUtilisateurA = normaliserSequence(seqA);
        this.memoireSequenceUtilisateurB = normaliserSequence(seqB);
        this.memoireSequenceNormale = normaliserSequence(seqN);
        System.out.println("LOG: Séquences chargées en mémoire");
    }

    /**
     * Algorithme Needleman-Wunsch simplifié pour l'alignement global.
     * Aligne les séquences utilisateur avec la référence.
     */
    public void NeedlemanWunsch() {
        // Implémentation simplifiée de Needleman-Wunsch
        // Pour une version complète, il faudrait la matrice de scoring

        if (memoireSequenceNormale.isEmpty()) {
            System.out.println("ERREUR: Séquence de référence non chargée");
            return;
        }

        // Alignement basique par recherche du motif HBB
        this.sequenceAligneeA = alignerSequence(memoireSequenceUtilisateurA, memoireSequenceNormale);
        this.sequenceAligneeB = alignerSequence(memoireSequenceUtilisateurB, memoireSequenceNormale);

        System.out.println("LOG: Alignement Needleman-Wunsch effectué");
    }

    /**
     * Alignement simplifié d'une séquence avec la référence.
     */
    private String alignerSequence(String seq, String ref) {
        if (seq == null || seq.isEmpty())
            return "";

        // Trouver le codon de démarrage ATG
        int startIndex = seq.indexOf("ATGGTGCATCTGACT");
        if (startIndex == -1) {
            startIndex = seq.indexOf("ATG");
        }

        if (startIndex != -1) {
            return seq.substring(startIndex);
        }
        return seq;
    }

    /**
     * Détecte la mutation Glu6Val (codon 6 du gène HBB).
     * GAG = Normal (Glutamate), GTG = Muté (Valine)
     * 
     * @return int nombre de mutations détectées (0, 1 ou 2)
     */
    public int detecterMutationGlu6Val() {
        // Exécuter l'alignement d'abord
        NeedlemanWunsch();

        int mutations = 0;

        // Vérifier mutation sur allèle A
        if (verifierMutationCodon6(memoireSequenceUtilisateurA)) {
            mutations++;
            System.out.println("LOG: Mutation Glu6Val détectée sur allèle A");
        }

        // Vérifier mutation sur allèle B
        if (verifierMutationCodon6(memoireSequenceUtilisateurB)) {
            mutations++;
            System.out.println("LOG: Mutation Glu6Val détectée sur allèle B");
        }

        this.nbMutationsDetectees = mutations;
        return mutations;
    }

    /**
     * Vérifie si le codon 6 présente la mutation Glu6Val.
     */
    private boolean verifierMutationCodon6(String sequence) {
        if (sequence == null || sequence.isEmpty())
            return false;

        String cleanSeq = normaliserSequence(sequence);

        // Recherche du début exact du gène HBB
        int startIndex = cleanSeq.indexOf("ATGGTGCATCTGACT");
        if (startIndex == -1) {
            startIndex = cleanSeq.indexOf("ATG");
        }

        if (startIndex == -1)
            return false;

        // Le codon 6 est à position startIndex + 18 (pour atteindre GAG/GTG)
        // Codon 1(ATG)..Codon 6(GAG) = 6*3 = 18 chars offset from start?
        // 0:ATG, 3:GTG, 6:CAT, 9:CTG, 12:ACT, 15:CCT, 18:GAG
        int codon6Start = startIndex + 18;

        if (cleanSeq.length() < codon6Start + 3)
            return false;

        String codon6 = cleanSeq.substring(codon6Start, codon6Start + 3);

        System.out.println("DEBUG: Codon 6 extrait = " + codon6);

        // GTG = Mutation (Valine au lieu de Glutamate)
        return codon6.equals("GTG");
    }

    /**
     * Méthode legacy pour compatibilité.
     * Analyse directe d'une séquence patient.
     */
    public String analyserMutation(String ref, String patient) {
        if (ref == null || patient == null || patient.trim().isEmpty()) {
            return "ERREUR : Données ADN manquantes.";
        }

        String cleanPatient = normaliserSequence(patient);

        // Recherche du début exact du gène
        int startIndex = cleanPatient.indexOf("ATGGTGCATCTGACT");

        if (startIndex == -1) {
            startIndex = cleanPatient.indexOf("ATG");
        }

        if (startIndex == -1) {
            return "INDÉTERMINÉ : Séquence HBB non reconnue.";
        }

        // Le codon 6 de HBB (GAG/GTG) se trouve après 5 codons complets
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

    /**
     * Normalise une séquence ADN.
     */
    private String normaliserSequence(String seq) {
        if (seq == null)
            return "";
        return seq.toUpperCase().replaceAll("[^ATGC]", "");
    }

    // Getters
    public int getNbMutationsDetectees() {
        return nbMutationsDetectees;
    }

    public String getMemoireSequenceUtilisateurA() {
        return memoireSequenceUtilisateurA;
    }

    public String getMemoireSequenceUtilisateurB() {
        return memoireSequenceUtilisateurB;
    }

    public String getMemoireSequenceNormale() {
        return memoireSequenceNormale;
    }

    public String getSequenceAligneeA() {
        return sequenceAligneeA;
    }

    public String getSequenceAligneeB() {
        return sequenceAligneeB;
    }
}