# Yalah L'Univ - Plateforme de Covoiturage Universitaire UEMF

**Version :** 1.0-SNAPSHOT  
**Auteurs :** Équipe Projet JEE (UEMF)  
**Date :** Janvier 2026

![Logo UEMF](docs/assets/uemf_logo.webp)

## 📖 Introduction

**Yalah L'Univ** est une solution web de covoiturage dédiée à la communauté de l'Université Euro-Méditerranéenne de Fès (UEMF). Conçue pour répondre aux défis de mobilité des étudiants et du personnel, la plateforme favorise le partage de trajets domicile-université, réduisant ainsi l'empreinte carbone et renforçant les liens sociaux au sein du campus.

Ce projet s'inscrit dans le cadre du module **Intégration JEE & Développement Avancé**, mettant en œuvre une architecture logicielle robuste basée sur les standards industriels Java Enterprise.

## 🏗 Architecture & Technologies

L'application repose sur une architecture **MVC (Modèle-Vue-Contrôleur)** classique enrichie par des services RESTful et une persistance polyglotte.

### Backend (Java EE / Jakarta EE 10)
*   **Conteneur de Servlets :** Apache Tomcat 10.1 (via Jakarta Servlet 6.0)
*   **ORM (Object-Relational Mapping) :** Hibernate 6.4 (JPA 3.0)
*   **API REST :** Jersey (JAX-RS 3.1)
*   **Injection de Dépendances :** HK2 (via Jersey)
*   **Build Tool :** Maven

### Frontend
*   **Rendu Serveur :** JSP (JavaServer Pages) & JSTL
*   **Design :** CSS natif (Variables CSS, Flexbox/Grid) pour une identité visuelle unique.
*   **Interactivité :** JavaScript Vanilla (ES6+) & WebSocket (Chat temps réel).

### Base de Données (Persistance Polyglotte)
1.  **PostgreSQL 15+ :** Données relationnelles structurées (Utilisateurs, Trajets, Réservations).
2.  **MongoDB :** Données non structurées et volumineuses (Logs d'activité, Analytics, Historique de recherche).

### Services Externes
*   **Leaflet.js & OpenStreetMap :** Cartographie interactive.
*   **LocationIQ API :** Autocomplétion d'adresses et géocodage.
*   **GraphHopper :** Calcul d'itinéraires et distances.

## ✨ Fonctionnalités Clés

*   **Authentification & Profils :** Inscription sécurisée, gestion de profil (étudiant/professeur), préférences (fumeur, musique).
*   **Gestion des Trajets :** Proposition de trajets (conducteur) avec points de départ/arrivée dynamiques sur carte.
*   **Recherche & Réservation :** Moteur de recherche multicritères, réservation de places, gestion des statuts (en attente, confirmé).
*   **Tableau de Bord :** Statistiques personnelles, historique des réservations, gains (simulés).
*   **Communication :** Messagerie instantanée WebSocket intégrée pour coordonner les trajets.
*   **Système d'Avis :** Évaluation des conducteurs et passagers pour maintenir la confiance.

## 🚀 Installation & Configuration

### Prérequis
*   Java JDK 21+
*   Maven 3.8+
*   PostgreSQL (Port 5432)
*   MongoDB (Port 27017)
*   Tomcat 10.1+

### Étapes de Déploiement

1.  **Cloner le dépôt :**
    ```bash
    git clone https://github.com/Houssam1998/Yalah_LUniv_Projet_Final_JEE.git
    cd Yalah_LUniv_Projet_Final_JEE
    ```

2.  **Configuration Base de Données :**
    *   Renommez `src/main/resources/META-INF/persistence.xml.example` en `persistence.xml`.
    *   Modifiez les identifiants JDBC :
        ```xml
        <property name="jakarta.persistence.jdbc.password" value="VOTRE_MOT_DE_PASSE"/>
        ```

3.  **Initialisation Données :**
    *   Le script Hibernate `hbm2ddl` créera automatiquement le schéma.
    *   (Optionnel) Importez les données de test via `docs/database/fix_data.sql`.

4.  **Compilation & Exécution :**
    ```bash
    mvn clean package
    ```
    *   Déployez le fichier `target/yalah.war` dans le dossier `webapps` de Tomcat.
    *   Accédez à `http://localhost:8080/yalah`

## 📚 Documentation

Pour une analyse détaillée (Conception UML, Diagrammes de Séquence, Choix Techniques), veuillez consulter le dossier [docs/](docs/).

---
*Projet universitaire - © 2026 UEMF*
