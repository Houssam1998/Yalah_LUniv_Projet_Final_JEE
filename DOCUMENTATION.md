# Documentation Technique - Yalah L'Univ

## 1. Contexte du Projet

Ce document présente les aspects techniques détaillés de la plateforme **Yalah L'Univ**. L'objectif était de développer une application web robuste respectant les standards **JEE** sans dépendre de frameworks lourds comme Spring Boot, afin de maîtriser les concepts fondamentaux (Servlet, JPA, JAX-RS).

## 2. Modélisation des Données (UML)

Le modèle de données s'articule autour des entités principales suivantes :

*   **User :** Classe mère (Stratégie `JOINED` ou `SINGLE_TABLE`) pour les Étudiants et Enseignants. Attributs : `email`, `password` (hashé BCrypt), `reputation`.
*   **Ride (Trajet) :** Entité centrale. Relations : `OneToMany` avec `Booking`, `ManyToOne` avec `Driver` et `Vehicle`.
*   **Booking (Réservation) :** Table de jointure enrichie avec états (`PENDING`, `ACCEPTED`, `REJECTED`).
*   **Message :** Système de messagerie persistant (PostgreSQL) et temps réel (WebSocket).

*(Voir les scripts SQL dans `docs/database/` pour le schéma physique)*

## 3. Choix d'Architecture

### 3.1. Approche "No-Framework" (Pure JEE)
Le choix de ne pas utiliser Spring a imposé une configuration manuelle rigoureuse :
*   **Gestion des Transactions :** Utilisation de `EntityTransaction` manuel dans les DAO (`connection.begin()`, `commit()`, `rollback()`).
*   **Gestion des Exceptions :** Blocs `try-catch-finally` systématiques pour assurer la fermeture des `EntityManager`.
*   **Injection de Dépendances :** Instanciation manuelle ou singleton simple pour les Services/DAO.

### 3.2. Persistance Polyglotte
*   **Pourquoi PostgreSQL ?** Pour garantir l'intégrité référentielle (ACID) des transactions financières (réservations) et des comptes utilisateurs.
*   **Pourquoi MongoDB ?** Pour stocker des logs volumineux et non structurés (ex: "Utilisateur X a cherché un trajet Fès -> Meknès à 12h") sans impacter les performances de la base relationnelle.

### 3.3. API REST & Frontend
L'application expose une API REST (`/api/v1/...`) consommée par certaines parties dynamiques du frontend (AJAX/Fetch), tandis que le rendu principal reste côté serveur (JSP) pour le SEO et la rapidité de premier chargement.

## 4. Sécurité

*   **Mots de passe :** Hashage avec BCrypt (bibliothèque `jbcrypt`).
*   **Sessions :** Gestion via `HttpSession`, filtre d'authentification (`AuthFilter`) interceptant les routes protégées.
*   **Injection SQL :** Prévention totale grâce à l'utilisation de JPQL et `CriteriaBuilder` (paramètres nommés).

## 5. WebSocket (Temps Réel)
Le chat utilise l'API standard `jakarta.websocket`.
*   **Endpoint :** `/ws/chat/{userId}`
*   Les messages sont persistés en base de données de manière asynchrone avant d'être diffusés aux clients connectés.

## 6. Structure du Projet

```
src/main/java
├── com.uemf.yalah.api        # Endpoints JAX-RS (REST)
├── com.uemf.yalah.controller # Servlets (MVC)
├── com.uemf.yalah.dao        # Data Access Object (JPA)
├── com.uemf.yalah.model      # Entités (Hibernate/JPA)
├── com.uemf.yalah.service    # Logique métier
├── com.uemf.yalah.socket     # WebSocket Endpoints
└── com.uemf.yalah.util       # Utilitaires (DB, Mongo, Password)

src/main/webapp
├── assets/                   # CSS, JS, Images
├── WEB-INF/views/            # JSP Templates (JSTL)
└── WEB-INF/web.xml           # Descripteur de déploiement
```

---
*Document généré automatiquement le 18 Janvier 2026*
