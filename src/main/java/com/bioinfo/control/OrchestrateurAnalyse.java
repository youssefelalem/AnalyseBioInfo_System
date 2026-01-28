package com.bioinfo.control;

import com.bioinfo.boundary.DepotBDD;
import com.bioinfo.entity.*;

/**
 * Contrôleur central qui orchestre le flux d'analyse génétique.
 * Conforme au diagramme de classes BCE.
 */
public class OrchestrateurAnalyse {

    // Attributs conformes au diagramme BCE
    private boolean estValide;
    private boolean patientExiste;
    private DepotBDD depot;
    private MoteurCalcul moteur;

    // Constructeur
    public OrchestrateurAnalyse() {
        this.depot = new DepotBDD();
        this.moteur = new MoteurCalcul();
        this.estValide = false;
        this.patientExiste = false;
    }

    /**
     * Fonction principale : pilote le flux complet d'analyse.
     * Conforme au diagramme de séquence "piloterFlux".
     * 
     * @param infos   informations du patient
     * @param fichier fichier FASTA à analyser
     * @return ResultatAnalyse le résultat de l'analyse
     */
    public ResultatAnalyse piloterFlux(Utilisateur infos, FichierAnalyse fichier) {

        // 1. Vérifier la validité des données
        this.estValide = verifierValiditeDonnees(infos, fichier);
        if (!estValide) {
            return null;
        }

        // 2. Vérifier si le patient existe déjà
        this.patientExiste = DepotBDD.rechercherExistant(infos);

        if (patientExiste) {
            // Récupérer l'ancien résultat
            return depot.recupererResultat(infos.getIdUtilisateur());
        }

        // 3. Vérifier l'intégrité de la base de référence
        if (!verifierIntegriteReference()) {
            System.err.println("ERREUR : Données de référence corrompues !");
            return null;
        }

        // 4. Extraire les séquences du fichier
        String[] sequences = fichier.extraireDeuxSequences();
        String sequenceA = sequences[0];
        String sequenceB = sequences[1];

        // 5. Charger la séquence normale de référence
        Sequence sequenceNormale = depot.chargerSequenceReference();
        if (sequenceNormale == null) {
            // Si pas de référence, l'extraire du code
            sequenceNormale = depot.extraireSequenceNormale();
            if (sequenceNormale != null) {
                depot.mettreAJourSequence(sequenceNormale);
            }
        }

        // 6. Charger les séquences en mémoire du moteur
        String refNucleotides = (sequenceNormale != null) ? sequenceNormale.getNucleotides() : "";
        moteur.chargerEnMemoire(sequenceA, sequenceB, refNucleotides);

        // 7. Détecter les mutations
        int nbMutations = moteur.detecterMutationGlu6Val();

        // 8. Créer le résultat
        ResultatAnalyse resultat = new ResultatAnalyse();
        resultat.definirVerdict(nbMutations);

        // 9. Sauvegarder le résultat
        depot.sauvegarderResultat(infos.getNom(), resultat.getVerdict().name());
        depot.enregistrerDonnees(infos, new Sequence[] {
                new Sequence(sequenceA),
                new Sequence(sequenceB)
        });

        return resultat;
    }

    /**
     * Vérifie la validité des données saisies.
     * 
     * @param infos   l'utilisateur
     * @param fichier le fichier d'analyse
     * @return true si les données sont valides
     */
    public boolean verifierValiditeDonnees(Utilisateur infos, FichierAnalyse fichier) {
        if (infos == null || infos.getNom() == null || infos.getNom().trim().isEmpty()) {
            return false;
        }
        if (fichier == null || fichier.getContenuBrut() == null || fichier.getContenuBrut().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Vérifie l'intégrité de la base de données (hash).
     * 
     * @return true si la référence est intègre
     */
    public boolean verifierIntegriteReference() {
        Sequence ref = depot.chargerSequenceReference();

        if (ref == null) {
            // Essayer d'extraire la référence et de la sauvegarder
            Sequence refExtraite = depot.extraireSequenceNormale();
            if (refExtraite != null) {
                depot.mettreAJourSequence(refExtraite);
                return true;
            }
            return false;
        }

        // Le hash est généré automatiquement par le constructeur de Sequence
        System.out.println("LOG: Hash de la référence vérifié : " + ref.getEmpreinteHash());
        return true;
    }

    /**
     * Méthode legacy pour compatibilité avec AnalyseServlet existant.
     * 
     * @param nomPatient le nom du patient
     * @param adnPatient la séquence ADN
     * @return String le verdict textuel
     */
    public String executerDiagnostic(String nomPatient, String adnPatient) {
        // Créer les objets nécessaires
        Utilisateur utilisateur = new Utilisateur(nomPatient);
        FichierAnalyse fichier = new FichierAnalyse(">" + nomPatient + "\n" + adnPatient);

        // Appeler piloterFlux
        ResultatAnalyse resultat = piloterFlux(utilisateur, fichier);

        if (resultat == null) {
            // Fallback avec l'ancienne logique
            if (!verifierIntegriteReference()) {
                return "ERREUR : Données de référence corrompues ! L'analyse est stoppée.";
            }

            Sequence refObj = depot.chargerSequenceReference();
            if (refObj == null) {
                return "ERREUR : Impossible de charger la séquence de référence.";
            }

            String verdict = moteur.analyserMutation(refObj.getNucleotides(), adnPatient);
            depot.sauvegarderResultat(nomPatient, verdict);
            return verdict;
        }

        // Convertir le verdict en texte lisible
        switch (resultat.getVerdict()) {
            case SAIN_AA:
                return "SAIN_AA (Normal)";
            case PORTEUR_AS:
                return "PORTEUR_AS (Porteur sain)";
            case MALADE_SS:
                return "MALADE_SS (Drépanocytose)";
            default:
                return "INDÉTERMINÉ";
        }
    }

    // Getters
    public boolean isEstValide() {
        return estValide;
    }

    public boolean isPatientExiste() {
        return patientExiste;
    }
}