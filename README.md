# 🧬 AnalyseBioInfo System

<div align="center">

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-007396?style=for-the-badge&logo=eclipse&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/Tomcat-10.1-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)

**Système d'Analyse Bio-Informatique pour la Détection de la Mutation Glu6Val (HBB)**

*Approche Computationnelle et Diagnostic Automatisé de la Drépanocytose*

[Contexte](#-contexte-scientifique) • [Architecture](#-architecture) • [Installation](#-installation) • [Utilisation](#-utilisation)

</div>

---

## 📋 Résumé

Ce projet s'inscrit dans le cadre de l'application des technologies **Big Data** à la **bio-informatique**. Il vise à développer une chaîne de traitement automatisée pour l'analyse du gène de l'**Hémoglobine Bêta (HBB)**, spécifiquement pour la détection de la mutation **Glu6Val** responsable de l'**anémie falciforme** (drépanocytose).

En combinant l'alignement de séquences et l'analyse structurale, ce système fournit un outil d'aide au diagnostic rapide et précis.

### Objectifs du Projet
- 📥 **Parsing de fichiers FASTA** - Lecture et extraction des séquences ADN
- 🔬 **Alignement de séquences** - Implémentation de l'algorithme de Needleman-Wunsch
- 🧬 **Détection de mutation** - Identification de la substitution Glu6Val (codon 6)
- 💾 **Persistance des résultats** - Sauvegarde en base de données MySQL

### Résultats de Diagnostic

| Génotype | Verdict | Description |
|----------|---------|-------------|
| **AA** | `SAIN_AA` | Patient sain - Codon 6 normal (GAG) |
| **AS** | `PORTEUR_AS` | Porteur sain du trait drépanocytaire |
| **SS** | `MALADE_SS` | Patient atteint de drépanocytose - Codon 6 muté (GTG) |

---

## 🔬 Contexte Scientifique

### L'Anémie Falciforme (Drépanocytose)

L'anémie falciforme est une **maladie génétique** transmise selon un mode autosomique récessif. Elle se caractérise par la production d'une forme anormale d'hémoglobine, appelée **hémoglobine S (HbS)**, en lieu et place de l'hémoglobine A (HbA).

### La Mutation Glu6Val (E6V)

La cause moléculaire est une **mutation ponctuelle** sur le **codon 6** du gène **HBB** :

```
Séquence Normale (HbA) : ... Val-His-Leu-Thr-Pro-Glu-Glu-Lys ...
                                               ↑
Séquence Mutée (HbS)  : ... Val-His-Leu-Thr-Pro-Val-Glu-Lys ...
                                               ↑
                                    Position 6 : Glu → Val
```

| Caractéristique | HbA (Normal) | HbS (Muté) |
|-----------------|--------------|------------|
| Acide aminé position 6 | Acide glutamique (Glu) | Valine (Val) |
| Propriété | Hydrophile | Hydrophobe |
| Solubilité | Bonne | Faible sous hypoxie |
| Forme globules rouges | Disques biconcaves | Faucille (Sickle) |

### Algorithme Utilisé

Le système utilise l'**algorithme de Needleman-Wunsch** pour l'alignement global des séquences, permettant de localiser avec précision toute substitution sur le gène HBB.

---

## 🏗️ Architecture

Le projet suit une **architecture en 3 couches (3-Tier)** basée sur le modèle **BCE (Boundary-Control-Entity)** :

```
┌─────────────────────────────────────────────────────────────┐
│              COUCHE PRÉSENTATION (Boundary)                  │
│            index.html + script.js + style.css               │
│     • Formulaire de saisie patient                          │
│     • Upload fichier FASTA                                  │
│     • Affichage du verdict                                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│               COUCHE MÉTIER (Control)                        │
│                                                              │
│  ┌─────────────────────┐    ┌──────────────────────────┐   │
│  │   AnalyseServlet    │───▶│  OrchestrateurAnalyse    │   │
│  │   (Point d'entrée)  │    │  (Contrôle du flux)      │   │
│  └─────────────────────┘    └──────────────────────────┘   │
│                                        │                    │
│                              ┌─────────▼─────────┐         │
│                              │   MoteurCalcul    │         │
│                              │ (Analyse mutation)│         │
│                              └───────────────────┘         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│               COUCHE DONNÉES (Entity + Boundary)             │
│                                                              │
│  ┌────────────┐  ┌────────────┐  ┌───────────────────────┐ │
│  │  Patient   │  │  Sequence  │  │       DepotBDD        │ │
│  │  (Entity)  │  │  (Entity)  │  │   (Accès base MySQL)  │ │
│  └────────────┘  └────────────┘  └───────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Structure du Projet

```
AnalyseBioInfo_System/
├── 📁 src/main/
│   ├── 📁 java/com/bioinfo/
│   │   ├── 📁 boundary/          # Interface & Persistance
│   │   │   ├── AnalyseServlet.java    # Servlet HTTP
│   │   │   └── DepotBDD.java          # Accès MySQL
│   │   ├── 📁 control/           # Logique métier
│   │   │   ├── OrchestrateurAnalyse.java
│   │   │   └── MoteurCalcul.java      # Détection mutation
│   │   ├── 📁 entity/            # Modèles de données
│   │   │   ├── Patient.java
│   │   │   └── Sequence.java          # + Hash SHA-256
│   │   └── 📁 util/
│   │       └── DBConnection.java      # Configuration MySQL
│   └── 📁 webapp/
│       ├── 📄 index.html
│       ├── 📄 script.js
│       ├── 📄 style.css
│       └── 📁 WEB-INF/
│           ├── 📄 web.xml
│           └── 📁 lib/
│               └── mysql-connector-j-9.6.0.jar
├── 📁 database/
│   └── 📄 init_database.sql      # Script création BDD
├── 📁 rapport/
│   └── 📄 rapportbioinfvr3.pdf   # Rapport complet du projet
├── 📄 build.ps1                  # Script de build PowerShell
└── 📄 README.md
```

### Sécurité et Intégrité des Données

Le système intègre un **protocole d'auto-réparation** fondé sur la vérification d'intégrité :
- Utilisation de **SHA-256** pour valider la conformité de la séquence de référence
- Vérification avant chaque traitement que l'étalon de mesure n'a subi aucune altération

---

## 🚀 Installation

### Prérequis

| Logiciel | Version | Téléchargement |
|----------|---------|----------------|
| Java JDK | 21+ | [Eclipse Adoptium](https://adoptium.net/) |
| Apache Tomcat | 10.1+ | [Apache Tomcat](https://tomcat.apache.org/) |
| MySQL (XAMPP) | 8.0+ | [XAMPP](https://www.apachefriends.org/) |

### Étapes d'Installation

#### 1️⃣ Configuration de la Base de Données

1. Démarrer **MySQL** via XAMPP Control Panel
2. Ouvrir **phpMyAdmin** : http://localhost/phpmyadmin
3. Exécuter le script : `database/init_database.sql`

```sql
CREATE DATABASE IF NOT EXISTS bioinfo_db;
USE bioinfo_db;

-- Tables: patients, sequences, resultats
-- Voir fichier database/init_database.sql pour le script complet
```

#### 2️⃣ Configuration de la Connexion

Modifier `src/main/java/com/bioinfo/util/DBConnection.java` si nécessaire :

```java
private static final String URL = "jdbc:mysql://localhost:3306/bioinfo_db";
private static final String USER = "root";
private static final String PASSWORD = ""; // Votre mot de passe MySQL
```

#### 3️⃣ Compilation et Déploiement

```powershell
cd AnalyseBioInfo_System
.\build.ps1
```

#### 4️⃣ Démarrage du Serveur

```powershell
cd C:\apache-tomcat-10.1.50\bin
.\catalina.bat run
```

#### 5️⃣ Accès à l'Application

🌐 **URL** : http://localhost:8080/AnalyseBioInfo_System/

---

## 💻 Utilisation

### Interface Web

1. **Saisir le nom du patient** dans le formulaire
2. **Charger un fichier FASTA** contenant la séquence ADN à analyser
3. **Cliquer sur "Lancer l'analyse"**
4. **Consulter le verdict** affiché (SAIN_AA, PORTEUR_AS, ou MALADE_SS)

### Format FASTA Supporté

```fasta
>Patient_ID_123 | Échantillon HBB
ATGGTGCACCTGACTCCTGAGGAGAAGTCTGCCGTTACTGCCCTGTGGGGCAAGGTGAAC
GTGGATGAAGTTGGTGGTGAGGCCCTGGGCAGGCTGCTGGTGGTCTACCCTTGGACCCAG
```

---

## 🧪 Tests

### Séquence Normale (SAIN_AA)
```
ATGGTGCACCTGACTCCTGAGGAGAAGTCTGCCGTTACTGCC
```
> **Codon 6 : GAG** → Acide glutamique → Patient sain ✅

### Séquence Mutée (MALADE_SS)
```
ATGGTGCACCTGACTCCTGTGGAGAAGTCTGCCGTTACTGCC
```
> **Codon 6 : GTG** → Valine → Drépanocytose détectée ⚠️

---

## 🔌 API REST

### Endpoint d'Analyse

```http
POST /AnalyseBioInfo_System/analyser
Content-Type: application/x-www-form-urlencoded

Paramètres:
  nom  : String (Nom du patient)
  adn  : String (Séquence ADN)

Réponse: text/plain
  "Résultat pour {nom} : SAIN_AA (Normal)"
  "Résultat pour {nom} : MALADE_SS (Drépanocytose)"
```

---

## 📊 Base de Données

### Schéma Relationnel

```
┌───────────────────┐         ┌───────────────────┐
│     patients      │         │     resultats     │
├───────────────────┤         ├───────────────────┤
│ id_utilisateur PK │◄────────│ id_utilisateur FK │
│ nom               │         │ id_analyse PK     │
│ infosSaisies      │         │ verdict           │
└───────────────────┘         │ date_analyse      │
                              └───────────────────┘
                                      
┌───────────────────┐
│    sequences      │
├───────────────────┤
│ id_sequence PK    │
│ nucleotides       │
│ type              │
│ empreinteHash     │  ← SHA-256 pour intégrité
└───────────────────┘
```

---

## 🛠️ Technologies Utilisées

| Catégorie | Technologie |
|-----------|-------------|
| **Backend** | Java 21, Jakarta Servlet API 6.0 |
| **Frontend** | HTML5, CSS3, JavaScript (Vanilla) |
| **Base de données** | MySQL 8.0 |
| **Serveur** | Apache Tomcat 10.1 |
| **Sécurité** | SHA-256 (intégrité des données) |
| **Build** | PowerShell Script |
| **Versioning** | Git / GitHub |

---

## 📚 Références Scientifiques

1. **Pauling, L., et al. (1949)**. *Sickle cell anemia, a molecular disease.* Science.
2. **Ingram, V. M. (1956)**. *A specific chemical difference between the globins of normal human and sickle-cell anemia hemoglobin.* Nature.
3. **Rees, D. C., et al. (2010)**. *Sickle-cell disease.* The Lancet.

---

## 👥 Équipe du Projet

<div align="center">

**Université Hassan II de Casablanca**  
**Faculté des Sciences Ben M'Sick (FSBM)**  
**Master Data Science & Big Data (M1DSBD)**  
**Module : Bio-Informatique**  
**Année Universitaire : 2025-2026**

</div>

### Encadrement

| | |
|---|---|
| **Encadrante Académique** | Prof. **Ichrak BENAMRI** - Professeure à la FSBM |

### Équipe de Développement

| Membre | Rôle | Responsabilités |
|--------|------|-----------------|
| **Youssef El Alem** | 👨‍💼 Chef de Projet | Coordination et supervision |
| **Mohamed Taha Kachmar** | 🏗️ Architecte Logiciel | Conception et architecture 3-Tier |
| **Siham Salhi** | 🔬 Analyste Biologique | Expertise bio-informatique |
| **Mouslim Jad** | 💻 Développeur | Implémentation technique |
| **Essadiki Ibtissam** | ✅ Assurance Qualité | Validation et documentation |

---

## 📄 Licence

Ce projet est réalisé dans un cadre académique et éducatif.

---

<div align="center">

**🧬 AnalyseBioInfo System**  
*Détection Génétique Automatisée de la Mutation HBB*

</div>
