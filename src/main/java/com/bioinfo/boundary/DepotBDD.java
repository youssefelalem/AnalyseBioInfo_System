package com.bioinfo.boundary;

import java.sql.*;
import com.bioinfo.util.DBConnection;
import com.bioinfo.entity.Sequence;

public class DepotBDD {

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
    
    public void sauvegarderResultat(String nomPatient, String verdict) {
        String sqlPatient = "INSERT INTO patients (nom) VALUES (?)";
        String sqlRes = "INSERT INTO resultats (id_utilisateur, verdict, date_analyse) VALUES (?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection()) {
            int generatedId = -1;

            // 1. Insertion du patient et récupération de l'ID auto-généré
            // On ajoute Statement.RETURN_GENERATED_KEYS pour récupérer l'ID
            try (PreparedStatement pstmtP = conn.prepareStatement(sqlPatient, Statement.RETURN_GENERATED_KEYS)) {
                pstmtP.setString(1, nomPatient);
                pstmtP.executeUpdate();
                
                // Récupération de l'ID créé par l'AUTO_INCREMENT
                ResultSet rs = pstmtP.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }

            // 2. Insertion du résultat avec l'ID numérique obtenu
            if (generatedId != -1) {
                try (PreparedStatement pstmtR = conn.prepareStatement(sqlRes)) {
                    pstmtR.setInt(1, generatedId); // On utilise setInt car l'ID est un nombre
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