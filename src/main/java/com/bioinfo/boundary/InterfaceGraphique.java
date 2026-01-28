package com.bioinfo.boundary;

import com.bioinfo.entity.ResultatAnalyse;
import com.bioinfo.entity.VerdictFinal;

/**
 * Interface graphique logique pour la couche Boundary.
 * Gère l'affichage et la saisie des données utilisateur.
 * Conforme au diagramme de classes BCE.
 */
public class InterfaceGraphique {

    /**
     * Notifie l'utilisateur d'une erreur.
     * 
     * @param message le message d'erreur à afficher
     */
    public void notifierErreur(String message) {
        System.err.println("[ERREUR IHM] " + message);
    }

    /**
     * Affiche le résultat de l'analyse à l'utilisateur.
     * 
     * @param resultat le résultat à afficher
     */
    public void afficherResultat(ResultatAnalyse resultat) {
        if (resultat == null) {
            notifierErreur("Aucun résultat à afficher");
            return;
        }

        System.out.println("=== RÉSULTAT DE L'ANALYSE ===");
        System.out.println("Date : " + resultat.getDateAnalyse());
        System.out.println("Verdict : " + formatVerdict(resultat.getVerdict()));
        System.out.println("Mutations détectées : " + resultat.getNbMutationsDetectees());
        System.out.println("=============================");
    }

    /**
     * Affiche le résultat final formaté.
     * 
     * @param resultat le résultat de l'analyse
     */
    public void afficherResultatFinal(ResultatAnalyse resultat) {
        afficherResultat(resultat);
    }

    /**
     * Saisie des données (méthode placeholder pour interface console).
     * Dans l'implémentation web, cette logique est dans le Servlet.
     */
    public void saisirDonnees() {
        System.out.println("[IHM] En attente de saisie des données...");
    }

    /**
     * Formate le verdict pour l'affichage.
     */
    private String formatVerdict(VerdictFinal verdict) {
        if (verdict == null)
            return "INDÉTERMINÉ";

        switch (verdict) {
            case SAIN_AA:
                return "SAIN (AA) - Aucune mutation détectée";
            case PORTEUR_AS:
                return "PORTEUR SAIN (AS) - Porteur hétérozygote";
            case MALADE_SS:
                return "MALADE (SS) - Drépanocytose confirmée";
            default:
                return verdict.name();
        }
    }
}
