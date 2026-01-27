package com.bioinfo.boundary;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.bioinfo.control.OrchestrateurAnalyse;

@WebServlet("/analyser")
public class AnalyseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Récupérer les données envoyées par le formulaire HTML
        String nomPatient = request.getParameter("nom");
        String adnPatient = request.getParameter("adn");

        // 2. Appeler l'Orchestrateur pour traiter les données
        OrchestrateurAnalyse orchestrateur = new OrchestrateurAnalyse();
        String resultat = orchestrateur.executerDiagnostic(nomPatient, adnPatient);

        // 3. Envoyer la réponse au format texte simple (ou JSON)
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.print("Résultat pour " + nomPatient + " : " + resultat);
    }
}
