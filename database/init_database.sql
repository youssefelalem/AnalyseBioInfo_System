-- ====================================================
-- Script d'initialisation de la base de données
-- AnalyseBioInfo_System
-- ====================================================

-- 1. Création de l'environnement
CREATE DATABASE IF NOT EXISTS bioinfo_db;
USE bioinfo_db;

-- 2. Table des patients
CREATE TABLE IF NOT EXISTS patients (
    id_utilisateur INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    infosSaisies TEXT
);

-- 3. Table des séquences de référence
CREATE TABLE IF NOT EXISTS sequences (
    id_sequence INT AUTO_INCREMENT PRIMARY KEY,
    nucleotides TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'NORMALE',
    empreinteHash VARCHAR(255)
);

-- 4. Table des résultats 
CREATE TABLE IF NOT EXISTS resultats (
    id_analyse INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT,
    verdict VARCHAR(255), 
    date_analyse TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_utilisateur) REFERENCES patients(id_utilisateur)
);

-- 5. Insertion d'une séquence de référence
INSERT INTO sequences (nucleotides, type, empreinteHash) 
VALUES ('ATGGTGCACCTGACTCCTGAGGAGAAGTCTGCCGTTACTGCCCTGTGGGGCAAGGTGAACGTGGATGAAGTTGGTGGT', 'NORMALE', '5107212a9ee48bd3e2281ef481b7a7d4b715a86460fba2a099b8ddb2ca14cf9d');
