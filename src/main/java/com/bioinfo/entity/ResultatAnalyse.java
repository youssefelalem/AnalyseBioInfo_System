package com.bioinfo.entity;

import java.util.Date;

/**
 * Entité représentant le résultat d'une analyse génétique.
 * Conforme au diagramme de classes BCE.
 */
public class ResultatAnalyse {
    private VerdictFinal verdict;
    private Date dateAnalyse;
    private int nbMutationsDetectees;

    // Constructeur par défaut
    public ResultatAnalyse() {
        this.dateAnalyse = new Date();
        this.nbMutationsDetectees = 0;
        this.verdict = null;
    }

    // Constructeur avec verdict
    public ResultatAnalyse(VerdictFinal verdict) {
        this.verdict = verdict;
        this.dateAnalyse = new Date();
        this.nbMutationsDetectees = determinerNbMutations(verdict);
    }

    /**
     * Définit le verdict basé sur le nombre de mutations détectées.
     * Logique décisionnelle basée sur le nombre de mutations.
     * 
     * @param nbMutations nombre de mutations Glu6Val détectées
     */
    public void definirVerdict(int nbMutations) {
        this.nbMutationsDetectees = nbMutations;

        switch (nbMutations) {
            case 0:
                this.verdict = VerdictFinal.SAIN_AA;
                break;
            case 1:
                this.verdict = VerdictFinal.PORTEUR_AS;
                break;
            case 2:
            default:
                this.verdict = VerdictFinal.MALADE_SS;
                break;
        }
    }

    /**
     * Détermine le nombre de mutations basé sur le verdict.
     */
    private int determinerNbMutations(VerdictFinal v) {
        if (v == null)
            return 0;
        switch (v) {
            case SAIN_AA:
                return 0;
            case PORTEUR_AS:
                return 1;
            case MALADE_SS:
                return 2;
            default:
                return 0;
        }
    }

    // Getters
    public VerdictFinal getVerdict() {
        return verdict;
    }

    public Date getDateAnalyse() {
        return dateAnalyse;
    }

    public int getNbMutationsDetectees() {
        return nbMutationsDetectees;
    }

    // Setters
    public void setVerdict(VerdictFinal verdict) {
        this.verdict = verdict;
    }

    public void setDateAnalyse(Date dateAnalyse) {
        this.dateAnalyse = dateAnalyse;
    }

    public void setNbMutationsDetectees(int nbMutationsDetectees) {
        this.nbMutationsDetectees = nbMutationsDetectees;
    }

    @Override
    public String toString() {
        return "ResultatAnalyse{" +
                "verdict=" + verdict +
                ", dateAnalyse=" + dateAnalyse +
                ", nbMutationsDetectees=" + nbMutationsDetectees +
                '}';
    }
}
