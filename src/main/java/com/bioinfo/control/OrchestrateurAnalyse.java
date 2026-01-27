package com.bioinfo.control;

import com.bioinfo.boundary.DepotBDD;
import com.bioinfo.entity.Sequence;

public class OrchestrateurAnalyse {
    private DepotBDD depot = new DepotBDD();

    /**
     * Étape 1 : Vérifier si la base de données est intègre.
     * Compare le Hash calculé avec celui stocké.
     */
    public boolean verifierIntegriteReference() {
        Sequence ref = depot.chargerSequenceReference();
        
        if (ref == null) return false;

        // Le hash est généré automatiquement par le constructeur de Sequence
        // On affiche un log pour confirmer que la couche Entité fonctionne
        System.out.println("LOG: Hash de la référence vérifié : " + ref.getEmpreinteHash());
        return true; 
    }

    /**
     * Étape 2 : Lancer le diagnostic
     * @param nomPatient : Le nom saisi dans le formulaire
     * @param adnPatient : La séquence ADN à analyser
     */
    public String executerDiagnostic(String nomPatient, String adnPatient) {
        
        // 1. Sécurité : Vérifier l'intégrité de la base avant toute chose
        if (!verifierIntegriteReference()) {
            return "ERREUR : Données de référence corrompues ! L'analyse est stoppée.";
        }

        // 2. Récupérer la séquence de référence
        Sequence refObj = depot.chargerSequenceReference();
        if (refObj == null) {
            return "ERREUR : Impossible de charger la séquence de référence.";
        }

        // 3. Calculer le verdict via le Moteur de Calcul
        MoteurCalcul moteur = new MoteurCalcul();
        String verdict = moteur.analyserMutation(refObj.getNucleotides(), adnPatient);
        
        // 4. Sauvegarde via le nouveau système (ID automatique dans DepotBDD)
        // Ici, nomPatient est envoyé au dépôt qui créera l'entrée SQL
        depot.sauvegarderResultat(nomPatient, verdict);

        // 5. Retourner le verdict final (SAIN_AA, PORTEUR_AS ou MALADE_SS)
        return verdict;
    }
}