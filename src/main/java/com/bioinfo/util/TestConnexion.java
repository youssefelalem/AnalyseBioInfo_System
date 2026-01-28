package com.bioinfo.util;

import java.sql.Connection;

public class TestConnexion {

    public static void main(String[] args) {

        System.out.println("Tentative de connexion...");

        // Appel de la méthode que nous avons créée dans DBConnection
        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            System.out.println("BRAVO ! Votre projet Eclipse est connecté à MySQL.");
            try {
                conn.close(); // On ferme toujours la connexion après test
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("ÉCHEC : Vérifiez votre mot de passe ou le nom de la base.");
        }
    }

}
