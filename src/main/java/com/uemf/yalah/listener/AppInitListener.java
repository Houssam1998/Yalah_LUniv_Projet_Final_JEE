package com.uemf.yalah.listener;

import com.uemf.yalah.util.DatabaseSeeder;
import com.uemf.yalah.util.JPAUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Listener pour initialiser l'application au démarrage.
 * - Initialise JPA (EntityManagerFactory)
 * - Charge les données de test si la base est vide
 */
@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("🚀 Démarrage de Yalah L'Univ...");

        try {
            // Initialiser JPA
            JPAUtil.getEntityManager().close();
            System.out.println("✅ Connexion à la base de données établie");

            // Charger les données de test si la base est vide
            DatabaseSeeder.seedDatabase();

            System.out.println("🎉 Yalah L'Univ prêt!");

        } catch (Exception e) {
            System.err.println("❌ Erreur d'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("👋 Arrêt de Yalah L'Univ...");
        JPAUtil.shutdown();
        System.out.println("✅ Application arrêtée proprement");
    }
}
