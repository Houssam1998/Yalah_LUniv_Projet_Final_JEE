package com.uemf.yalah.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Utilitaire de connexion MongoDB pour la persistance polyglotte
 * 
 * MongoDB est utilisé pour:
 * - Logs d'activité (recherches, réservations, connexions)
 * - Analytics en temps réel
 * - Données statistiques agrégées
 */
public class MongoDBUtil {

    // Timeout de 2 secondes pour éviter le blocage du Dashboard si Mongo est éteint
    private static final String CONNECTION_STRING = "mongodb://localhost:27017/?connectTimeoutMS=2000&serverSelectionTimeoutMS=2000";
    private static final String DATABASE_NAME = "yalah_analytics";

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static boolean isConnected = false;

    /**
     * Initialise la connexion MongoDB
     */
    public static synchronized void init() {
        if (mongoClient == null) {
            try {
                mongoClient = MongoClients.create(CONNECTION_STRING);
                database = mongoClient.getDatabase(DATABASE_NAME);
                // Tenter un ping pour vérifier la connexion réelle avant de créer les index
                // Cela permet de fail-fast si la base n'est pas là, au lieu de bloquer sur
                // createIndex
                database.runCommand(new Document("ping", 1));

                isConnected = true;
                System.out.println("[MongoDB] Connecté à la base: " + DATABASE_NAME);

                // Créer les index (seulement si connecté)
                createIndexes();
            } catch (Exception e) {
                System.err.println("[MongoDB] Erreur de connexion: " + e.getMessage());
                isConnected = false;
            }
        }
    }

    /**
     * Crée les index pour optimiser les requêtes
     */
    private static void createIndexes() {
        try {
            // Index sur activity_logs
            MongoCollection<Document> activityLogs = database.getCollection("activity_logs");
            activityLogs.createIndex(new Document("timestamp", -1));
            activityLogs.createIndex(new Document("event_type", 1));
            activityLogs.createIndex(new Document("user_id", 1));

            // Index sur search_logs
            MongoCollection<Document> searchLogs = database.getCollection("search_logs");
            searchLogs.createIndex(new Document("timestamp", -1));
            searchLogs.createIndex(new Document("departure", 1));
            searchLogs.createIndex(new Document("arrival", 1));

            System.out.println("[MongoDB] Index créés avec succès");
        } catch (Exception e) {
            System.err.println("[MongoDB] Erreur création index: " + e.getMessage());
        }
    }

    /**
     * Retourne la base de données MongoDB
     */
    public static MongoDatabase getDatabase() {
        if (database == null) {
            init();
        }
        return database;
    }

    /**
     * Retourne une collection spécifique
     */
    public static MongoCollection<Document> getCollection(String collectionName) {
        return getDatabase().getCollection(collectionName);
    }

    /**
     * Vérifie si MongoDB est connecté
     */
    public static boolean isConnected() {
        return isConnected;
    }

    /**
     * Ferme la connexion MongoDB
     */
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            isConnected = false;
            System.out.println("[MongoDB] Connexion fermée");
        }
    }
}
