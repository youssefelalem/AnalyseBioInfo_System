package com.bioinfo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	// Paramètres de connexion (Adaptez le mot de passe !)
    private static final String URL = "jdbc:mysql://localhost:3306/bioinfo_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // <--- Mettez votre mot de passe ici

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Chargement du pilote JDBC (important pour les versions récentes)
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion à MySQL réussie !");
        } catch (ClassNotFoundException e) {
            System.err.println("Pilote MySQL non trouvé : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
        return conn;
    }
}
