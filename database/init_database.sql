-- ====================================================
-- Script d'initialisation de la base de données
-- AnalyseBioInfo_System
-- ====================================================

-- 1. Création de l'environnement
CREATE DATABASE IF NOT EXISTS bioinfo_db;
USE bioinfo_db;

-- 2. Table des patients (Utilisateur)
CREATE TABLE IF NOT EXISTS patients (
    id_utilisateur INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    infosSaisies TEXT
);

-- 3. Table des séquences de référence
-- Hash modifié pour correspondre à Sequence.java (empreinteHash -> hash)
CREATE TABLE IF NOT EXISTS sequences (
    id_sequence INT AUTO_INCREMENT PRIMARY KEY,
    nucleotides TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'REFERENCE', 
    hash VARCHAR(255)
);

-- 4. Table des résultats (ResultatAnalyse)
CREATE TABLE IF NOT EXISTS resultats (
    id_analyse INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT,
    verdict VARCHAR(255), 
    date_analyse TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_utilisateur) REFERENCES patients(id_utilisateur)
);

-- 5. Insertion initiale (Optionnel, l'application peut l'auto-réparer)
-- La séquence correspond à celle hardcodée dans DepotBDD.java
INSERT INTO sequences (nucleotides, type, hash) 
VALUES (
    'ATGGTGCATCTGACTCCTGAGGAGAAGTCTGCCGTTACTGCCCTGTGGGGCAAGGTGAACGTGGATGAAGTTGGTGGTGAGGCC', 
    'REFERENCE', 
    'HASH_A_CALCULER_PAR_APP'
);
