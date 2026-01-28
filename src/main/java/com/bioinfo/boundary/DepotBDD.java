package com.bioinfo.boundary;

import java.sql.*;
import com.bioinfo.util.DBConnection;
import com.bioinfo.entity.*;

/**
 * Couche Boundary pour l'accès à la base de données.
 * Conforme au diagramme de classes BCE.
 */
public class DepotBDD {

    // Séquence HBB de référence (hardcodée pour auto-réparation)
    private static final String HBB_REFERENCE = "ATGGTGCATCTGACTCCTGAGGAGAAGTCTGCCGTTACTGCCCTGTGGGGCAAGGTGAACGTGGATGAAGTTGGTGGTGAGGCC";

    /**
     * Recherche si un patient existe déjà dans la base.
     * 
     * @param infos l'utilisateur à rechercher
     * @return true si existe, false sinon
     */
    public static boolean rechercherExistant(Utilisateur infos) {
        if (infos == null)
            return false;

        String query = "SELECT COUNT(*) FROM patients WHERE nom = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, infos.getNom());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur recherche patient : " + e.getMessage());
        }
        return false;
    }

    /**
     * Récupère le résultat d'analyse précédent d'un patient.
     * 
     * @param id identifiant du patient
     * @return ResultatAnalyse ou null
     */
    public ResultatAnalyse recupererResultat(String id) {
        String query = "SELECT verdict, date_analyse FROM resultats WHERE id_utilisateur = ? ORDER BY date_analyse DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String verdictStr = rs.getString("verdict");
                Date dateAnalyse = rs.getDate("date_analyse");

                ResultatAnalyse resultat = new ResultatAnalyse();
                resultat.setDateAnalyse(dateAnalyse);

                // Convertir le verdict String en enum
                try {
                    VerdictFinal verdict = VerdictFinal.valueOf(verdictStr.split(" ")[0].toUpperCase());
                    resultat.setVerdict(verdict);
                } catch (IllegalArgumentException e) {
                    // Fallback si le format ne correspond pas
                    if (verdictStr.contains("SAIN")) {
                        resultat.setVerdict(VerdictFinal.SAIN_AA);
                    } else if (verdictStr.contains("PORTEUR")) {
                        resultat.setVerdict(VerdictFinal.PORTEUR_AS);
                    } else if (verdictStr.contains("MALADE")) {
                        resultat.setVerdict(VerdictFinal.MALADE_SS);
                    }
                }

                return resultat;
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération résultat : " + e.getMessage());
        }
        return null;
    }

    /**
     * Vérifie si une séquence normale existe en base.
     * 
     * @return true si existe et est intègre
     */
    public boolean existeSequenceNormale() {
        String query = "SELECT COUNT(*) FROM sequences WHERE type = 'REFERENCE'";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur vérification séquence : " + e.getMessage());
        }
        return false;
    }

    /**
     * Enregistre les données utilisateur et séquences.
     * 
     * @param utilisateur l'utilisateur
     * @param sequences   les séquences à sauvegarder
     */
    public void enregistrerDonnees(Utilisateur utilisateur, Sequence[] sequences) {
        // Sauvegarde utilisateur
        String sqlUser = "INSERT INTO patients (nom) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sqlUser)) {

            pstmt.setString(1, utilisateur.getNom());
            pstmt.executeUpdate();
            System.out.println("LOG: Utilisateur enregistré : " + utilisateur.getNom());

        } catch (SQLException e) {
            System.err.println("Erreur enregistrement utilisateur : " + e.getMessage());
        }
    }

    /**
     * Extrait la séquence normale de référence (hardcodée pour auto-réparation).
     * 
     * @return Sequence de référence HBB
     */
    public Sequence extraireSequenceNormale() {
        return new Sequence(HBB_REFERENCE);
    }

    /**
     * Met à jour ou insère la séquence normale en base.
     * 
     * @param sequence la séquence à sauvegarder
     */
    public void mettreAJourSequence(Sequence sequence) {
        String sql = "INSERT INTO sequences (nucleotides, type, hash) VALUES (?, 'REFERENCE', ?) " +
                "ON DUPLICATE KEY UPDATE nucleotides = ?, hash = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sequence.getNucleotides());
            pstmt.setString(2, sequence.getEmpreinteHash());
            pstmt.setString(3, sequence.getNucleotides());
            pstmt.setString(4, sequence.getEmpreinteHash());
            pstmt.executeUpdate();
            System.out.println("LOG: Séquence de référence mise à jour");

        } catch (SQLException e) {
            System.err.println("Erreur mise à jour séquence : " + e.getMessage());
        }
    }

    /**
     * Charge la séquence de référence depuis la base.
     * 
     * @return Sequence ou null
     */
    public Sequence chargerSequenceReference() {
        String query = "SELECT nucleotides FROM sequences LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return new Sequence(rs.getString("nucleotides"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur chargement séquence : " + e.getMessage());
        }
        return null;
    }

    /**
     * Sauvegarde le résultat d'analyse.
     * 
     * @param nomPatient nom du patient
     * @param verdict    résultat de l'analyse
     */
    public void sauvegarderResultat(String nomPatient, String verdict) {
        String sqlPatient = "INSERT INTO patients (nom) VALUES (?)";
        String sqlRes = "INSERT INTO resultats (id_utilisateur, verdict, date_analyse) VALUES (?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection()) {
            int generatedId = -1;

            // 1. Insertion du patient et récupération de l'ID auto-généré
            try (PreparedStatement pstmtP = conn.prepareStatement(sqlPatient, Statement.RETURN_GENERATED_KEYS)) {
                pstmtP.setString(1, nomPatient);
                pstmtP.executeUpdate();

                ResultSet rs = pstmtP.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }

            // 2. Insertion du résultat avec l'ID numérique obtenu
            if (generatedId != -1) {
                try (PreparedStatement pstmtR = conn.prepareStatement(sqlRes)) {
                    pstmtR.setInt(1, generatedId);
                    pstmtR.setString(2, verdict);
                    pstmtR.executeUpdate();
                    System.out.println("Succès : Patient #" + generatedId + " et résultat enregistrés !");
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la sauvegarde : " + e.getMessage());
        }
    }
}