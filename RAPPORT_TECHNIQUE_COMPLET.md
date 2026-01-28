# Rapport Technique Complet : Système AnalyseBioInfo

**Projet Académique M1 Data Science & Big Data - Bio-Informatique**
*Université Hassan II de Casablanca*

---

## 🏗️ 1. Architecture du Projet (BCE)

L'architecture suit strictement le patron **Boundary-Control-Entity** pour une séparation propre des responsabilités.

```
AnalyseBioInfo_System/
├── src/main/java/com/bioinfo/
│   ├── entity/            (Données)
│   ├── control/           (Logique)
│   ├── boundary/          (Interface)
│   └── util/              (Connexion)
└── src/main/webapp/       (Frontend)
```

---

## 💾 2. Base de Données (MySQL)

**Fichier : `database/init_database.sql`**

```sql
-- Création de la base de données
CREATE DATABASE IF NOT EXISTS bioinfo_db;
USE bioinfo_db;

-- Table des utilisateurs (Patients)
CREATE TABLE IF NOT EXISTS patients (
    id_utilisateur INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    infosSaisies TEXT
);

-- Table des séquences (Référence et autres)
CREATE TABLE IF NOT EXISTS sequences (
    id_sequence INT AUTO_INCREMENT PRIMARY KEY,
    nucleotides TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'REFERENCE', 
    hash VARCHAR(255)
);

-- Table des résultats d'analyse
CREATE TABLE IF NOT EXISTS resultats (
    id_analyse INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur INT,
    verdict VARCHAR(255), 
    date_analyse TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_utilisateur) REFERENCES patients(id_utilisateur)
);
```

---

## 💻 3. Code Source (Java)

### A. Couche Entité (Entity)

**Fichier : `ResultatAnalyse.java`**
```java
package com.bioinfo.entity;
import java.util.Date;

public class ResultatAnalyse {
    private VerdictFinal verdict;
    private int nbMutationsDetectees;
    private Date dateAnalyse;

    public ResultatAnalyse() {
        this.dateAnalyse = new Date();
    }

    public void definirVerdict(int nbMutations) {
        this.nbMutationsDetectees = nbMutations;
        if (nbMutations == 0) this.verdict = VerdictFinal.SAIN_AA;
        else if (nbMutations == 1) this.verdict = VerdictFinal.PORTEUR_AS;
        else this.verdict = VerdictFinal.MALADE_SS;
    }

    public VerdictFinal getVerdict() { return verdict; }
    public int getNbMutationsDetectees() { return nbMutationsDetectees; }
    public Date getDateAnalyse() { return dateAnalyse; }
}
```

**Fichier : `Utilisateur.java`**
```java
package com.bioinfo.entity;

public class Utilisateur {
    private int idUtilisateur;
    private String nom;
    private String infosSaisies;

    public Utilisateur(String nom, String infosSaisies) {
        this.nom = nom;
        this.infosSaisies = infosSaisies;
    }

    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int id) { this.idUtilisateur = id; }
    public String getNom() { return nom; }
    public String getInfosSaisies() { return infosSaisies; }
}
```

**Fichier : `Sequence.java`**
```java
package com.bioinfo.entity;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class Sequence {
    private String nucleotides;
    private String empreinteHash;

    public Sequence(String nucleotides) {
        this.nucleotides = nucleotides;
        this.empreinteHash = genererHash(nucleotides);
    }

    public String calculerIntegrite() {
        return genererHash(this.nucleotides);
    }

    public boolean verifierIntegrite() {
        return genererHash(this.nucleotides).equals(this.empreinteHash);
    }

    private String genererHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception e) { throw new RuntimeException(e); }
    }

    public String getNucleotides() { return nucleotides; }
}
```

**Fichier : `VerdictFinal.java`**
```java
package com.bioinfo.entity;

public enum VerdictFinal {
    SAIN_AA,
    PORTEUR_AS,
    MALADE_SS
}
```

### B. Couche Contrôle (Control)

**Fichier : `OrchestrateurAnalyse.java`**
```java
package com.bioinfo.control;
import com.bioinfo.boundary.DepotBDD;
import com.bioinfo.entity.*;

public class OrchestrateurAnalyse {
    private DepotBDD depot;
    private MoteurCalcul moteur;

    public OrchestrateurAnalyse() {
        this.depot = new DepotBDD();
        this.moteur = new MoteurCalcul();
    }

    public String executerDiagnostic(String nom, String adnBrut) {
        Utilisateur utilisateur = new Utilisateur(nom, "Via Web");
        FichierAnalyse fichier = new FichierAnalyse(adnBrut);

        // 1. Validation de base
        if (nom == null || nom.isEmpty() || adnBrut == null || adnBrut.isEmpty()) {
            return "ERREUR : Données invalides";
        }

        // 2. Vérifier existant (Cache)
        if (DepotBDD.rechercherExistant(utilisateur)) {
            return depot.recupererResultat(utilisateur.getIdUtilisateur());
        }

        // 3. Traitement
        // Sauvegarder nouvel utilisateur
        depot.enregistrerDonnees(utilisateur);

        // Vérifier intégrité référence
        String refNucleotides = depot.extraireSequenceNormale();
        // ... Logique de vérification hash ...

        // Charger séquences
        String[] sequences = fichier.extraireDeuxSequences();
        moteur.chargerEnMemoire(sequences[0], sequences[1], refNucleotides);

        // Calculer
        int nbMutations = moteur.detecterMutationGlu6Val();

        // Verdict
        ResultatAnalyse resultat = new ResultatAnalyse();
        resultat.definirVerdict(nbMutations);

        // Sauvegarder
        depot.sauvegarderResultat(utilisateur.getIdUtilisateur(), resultat);

        return "Résultat pour " + nom + " : " + resultat.getVerdict();
    }
}
```

**Fichier : `src/main/java/com/bioinfo/control/MoteurCalcul.java`**
```java
package com.bioinfo.control;
import com.bioinfo.entity.VerdictFinal;

public class MoteurCalcul {
    private String memoireSequenceUtilisateurA;
    private String memoireSequenceUtilisateurB;
    private String memoireSequenceNormale;
    
    // ...

    public void chargerEnMemoire(String seqA, String seqB, String seqRef) {
        this.memoireSequenceUtilisateurA = normaliserSequence(seqA);
        this.memoireSequenceUtilisateurB = normaliserSequence(seqB);
        this.memoireSequenceNormale = normaliserSequence(seqRef);
    }
    
    // Détection mutation Glu6Val (GAG -> GTG)
    // Position 6 (AA) -> Nucléotides 18-20 sur séquence codante standard
    public int detecterMutationGlu6Val() {
       // Implémentation vérifiée : chercher "GTG" au codon 6
       // ...
       return mutations;
    }
    
    private String normaliserSequence(String seq) {
        return seq.toUpperCase().replaceAll("[^ATGC]", "");
    }
}
```

### C. Couche Frontière (Boundary)

**Fichier : `DepotBDD.java`**
```java
package com.bioinfo.boundary;
// ... Imports JDBC

public class DepotBDD {
    // Gestion des requêtes SQL
    // rechercherExistant()
    // enregistrerDonnees()
    // sauvegarderResultat()
    // ...
}
```

**Fichier : `AnalyseServlet.java`**
```java
package com.bioinfo.boundary;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.bioinfo.control.OrchestrateurAnalyse;
import java.io.IOException;

@WebServlet("/analyser")
public class AnalyseServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nom = request.getParameter("nom");
        String adn = request.getParameter("adn");
        
        OrchestrateurAnalyse orch = new OrchestrateurAnalyse();
        String res = orch.executerDiagnostic(nom, adn);
        
        response.setContentType("text/plain");
        response.getWriter().print(res);
    }
}
```

---

## 🌐 4. Frontend (Web)

**Fichier : `src/main/webapp/index.html`**
*(Contient le formulaire HTML et l'interface utilisateur)*

**Fichier : `src/main/webapp/script.js`**
```javascript
// Lecture du fichier FASTA
document.getElementById('fastaFile').addEventListener('change', function(e) {
    const file = e.target.files[0];
    const reader = new FileReader();
    reader.onload = function(e) {
        // Extraction et nettoyage de la séquence
        const lines = e.target.result.split('\n');
        const sequence = lines
           .filter(l => !l.startsWith('>'))
           .join('')
           .replace(/[^ATGC]/g, '');
        document.getElementById('adnText').value = sequence;
    };
    reader.readAsText(file);
});

// Envoi asynchrone (AJAX)
document.getElementById('analyseForm').addEventListener('submit', function(e) {
    e.preventDefault();
    // Fetch POST vers /analyser
});
```

---

## 🧪 5. Méthodologie et Fichiers de Test

Les tests sont basés sur des séquences **officielles NCBI** :
1. **RefSource** : NM_000518.5
2. **Mutation** : dbSNP rs334

Fichiers disponibles :
- `NCBI_Sain.fasta`
- `NCBI_Porteur.fasta`
- `NCBI_Malade.fasta`

---

*Généré automatiquement le 28/01/2026*
