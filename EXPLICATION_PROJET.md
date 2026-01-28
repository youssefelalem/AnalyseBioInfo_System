# Documentation du Projet : Système AnalyseBioInfo (v1.0)

## 1. Objectif du Projet
Ce système réalise une analyse bio-informatique de séquences ADN pour détecter la mutation **HBB** responsable de la **Drépanocytose** (Anémie Falciforme).
Il classifie le patient selon trois états génétiques :
- **SAIN_AA (Normal)** : Absence de mutation (Homozygote sauvage).
- **PORTEUR_AS (Hétérozygote)** : Une seule copie du gène muté (Porteur sain).
- **MALADE_SS (Homozygote muté)** : Deux copies du gène muté (Atteint de la maladie).

---

## 2. Architecture Logicielle (Modèle BCE)
Le projet respecte strictement le patron de conception **Boundary-Control-Entity (BCE)** pour assurer une séparation claire des responsabilités.

### A. Couche Entité (Entity Layer)
Représente les objets métier et les données persistantes.
- **`Utilisateur`** : Représente le patient (ID, Nom).
- **`Sequence`** : Stocke les nucléotides et l'empreinte de hachage (SHA-256) pour garantir l'intégrité.
- **`FichierAnalyse`** : Gère l'importation des fichiers FASTA et l'extraction des séquences brutes.
- **`ResultatAnalyse`** : Contient le verdict médical et la date de l'analyse.
- **`VerdictFinal`** : Énumération (Enum) des états possibles (SAIN, PORTEUR, MALADE).

### B. Couche Contrôle (Control Layer)
Contient la logique métier et les algorithmes.
- **`OrchestrateurAnalyse`** : Le chef d'orchestre. Il coordonne le flux entre l'IHM, la base de données et le moteur de calcul.
- **`MoteurCalcul`** : Le cœur scientifique.
    - Charge les séquences en mémoire.
    - Exécute l'algorithme d'alignement **Needleman-Wunsch** (simplifié).
    - Détecte spécifiquement la mutation ponctuelle au **codon 6** (Remplacement de `GAG` par `GTG`).

### C. Couche Frontière (Boundary Layer)
Gère les interactions avec le monde extérieur (Utilisateur, BDD).
- **`InterfaceGraphique`** : Classes logiques pour l'affichage (Simulation console/logique métier).
- **`AnalyseServlet`** : Point d'entrée Web (J2EE) qui reçoit les requêtes HTTP.
- **`DepotBDD`** : Gestion de la persistance (DAO) : sauvegarde et récupération des résultats depuis MySQL.

---

## 3. Méthodologie et Sources des Fichiers de Test
Pour garantir la validité scientifique de nos tests, nous n'avons pas utilisé de fichiers aléatoires. Nous avons suivi une méthodologie rigoureuse basée sur les banques de données génomiques officielles.

### Sources Officielles Utilisées :
1.  **Référence "Sain" (Wild Type)** :
    *   **Source** : NCBI (National Center for Biotechnology Information).
    *   **Accession** : [NM_000518.5](https://www.ncbi.nlm.nih.gov/nuccore/NM_000518.5)
    *   **Description** : Ceci est la séquence de référence officielle de l'ARNm du gène HBB humain. Elle contient le codon **GAG** (Acide Glutamique) en position 6, caractéristique d'un individu sain.

2.  **La Mutation (Pathogène)** :
    *   **Source** : dbSNP (Database of Single Nucleotide Polymorphisms).
    *   **Identifiant** : [rs334](https://www.ncbi.nlm.nih.gov/snp/rs334)
    *   **Description** : C'est la mutation ponctuelle officielle associée à la drépanocytose (**c.20A>T**). Elle transforme le codon `GAG` en `GTG` (Valine).

### Processus de Création des Fichiers de Test :
Puisque les banques de données ne fournissent pas de fichiers "patients" prêts à l'emploi (pour des raisons de confidentialité), nous avons reconstruit les génotypes bio-informatiquement :

1.  **`NCBI_Sain.fasta` (Homozygote Sain)** :
    *   Nous avons extrait la séquence brute de **NM_000518.5**.
    *   Nous l'avons dupliquée pour simuler la diploïdie (Paire de chromosomes 11).
    *   **Résultat** : Deux séquences identiques avec `GAG` au codon 6.

2.  **`NCBI_Malade.fasta` (Homozygote Muté)** :
    *   Nous avons pris la séquence de référence **NM_000518.5**.
    *   Nous avons appliqué in-silico la mutation **rs334** : remplacement précis de l'adénine (A) par une thymine (T) au niveau du 20ème nucléotide codant (Codon 6 : GAG → GTG).
    *   Nous l'avons dupliquée pour simuler les deux allèles atteints.

3.  **`NCBI_Porteur.fasta` (Hétérozygote)** :
    *   Nous avons combiné une séquence de référence (Saine) avec une séquence mutée (Malade).
    *   **Résultat** : Un fichier hybride représentant un porteur sain (Trait drépanocytaire).

---

## 4. Scénario d'Exécution (Workflow)
1.  **Saisie** : L'utilisateur envoie son nom et le fichier FASTA.
2.  **Vérification** : Le système vérifie si le patient existe déjà en base (Optimisation). Si oui, il retourne le résultat stocké.
3.  **Analyse** :
    - Extraction des deux séquences du fichier FASTA.
    - Comparaison avec la séquence de référence interne.
    - Détection de la présence du codon `GTG` à la position clé (+18 après le codon d'initiation).
4.  **Verdict** :
    - 0 mutation → **SAIN**
    - 1 mutation → **PORTEUR**
    - 2 mutations → **MALADE**
5.  **Stockage** : Enregistrement du patient et du résultat pour consultation future.
