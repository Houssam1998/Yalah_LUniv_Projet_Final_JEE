package com.uemf.yalah.dao;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.uemf.yalah.util.MongoDBUtil;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * DAO pour les logs d'activité stockés dans MongoDB
 * Implémente la persistance polyglotte (PostgreSQL + MongoDB)
 */
public class ActivityLogDAO {

    private static final String COLLECTION_NAME = "activity_logs";
    private static final String SEARCH_LOGS_COLLECTION = "search_logs";

    /**
     * Log une activité générique
     */
    public void logActivity(String eventType, Long userId, String description, Document metadata) {
        try {
            MongoCollection<Document> collection = MongoDBUtil.getCollection(COLLECTION_NAME);

            Document log = new Document()
                    .append("timestamp", new Date())
                    .append("event_type", eventType)
                    .append("user_id", userId)
                    .append("description", description)
                    .append("metadata", metadata);

            collection.insertOne(log);
        } catch (Exception e) {
            System.err.println("[ActivityLogDAO] Erreur log: " + e.getMessage());
        }
    }

    /**
     * Log une recherche de trajet
     */
    public void logSearch(Long userId, String departure, String arrival,
            LocalDateTime date, int resultsCount) {
        try {
            MongoCollection<Document> collection = MongoDBUtil.getCollection(SEARCH_LOGS_COLLECTION);

            Document log = new Document()
                    .append("timestamp", new Date())
                    .append("user_id", userId)
                    .append("departure", departure)
                    .append("arrival", arrival)
                    .append("search_date",
                            date != null ? Date.from(date.atZone(ZoneId.systemDefault()).toInstant()) : null)
                    .append("results_count", resultsCount);

            collection.insertOne(log);
        } catch (Exception e) {
            System.err.println("[ActivityLogDAO] Erreur log recherche: " + e.getMessage());
        }
    }

    /**
     * Log une recherche de trajet (surcharge avec date String)
     */
    public void logSearch(Long userId, String departure, String arrival,
            String dateStr, int resultsCount) {
        LocalDateTime date = null;
        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                date = LocalDateTime.parse(dateStr + "T00:00:00");
            } catch (Exception ignored) {
            }
        }
        logSearch(userId, departure, arrival, date, resultsCount);
    }

    /**
     * Log une réservation
     */
    public void logBooking(Long userId, Long rideId, Long bookingId, String action) {
        Document metadata = new Document()
                .append("ride_id", rideId)
                .append("booking_id", bookingId)
                .append("action", action);

        logActivity("BOOKING_" + action.toUpperCase(), userId,
                "Réservation " + action.toLowerCase(), metadata);
    }

    /**
     * Log une connexion utilisateur
     */
    public void logLogin(Long userId, String ipAddress) {
        Document metadata = new Document()
                .append("ip_address", ipAddress);

        logActivity("USER_LOGIN", userId, "Connexion utilisateur", metadata);
    }

    /**
     * Récupère les logs récents
     */
    public List<Document> getRecentLogs(int limit) {
        List<Document> logs = new ArrayList<>();
        try {
            MongoCollection<Document> collection = MongoDBUtil.getCollection(COLLECTION_NAME);
            collection.find()
                    .sort(Sorts.descending("timestamp"))
                    .limit(limit)
                    .into(logs);
        } catch (Exception e) {
            System.err.println("[ActivityLogDAO] Erreur récupération: " + e.getMessage());
        }
        return logs;
    }

    /**
     * Agrégation: Top routes recherchées
     */
    public List<Document> getTopSearchedRoutes(int limit) {
        List<Document> results = new ArrayList<>();
        try {
            MongoCollection<Document> collection = MongoDBUtil.getCollection(SEARCH_LOGS_COLLECTION);

            AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(
                    Aggregates.group(
                            new Document("departure", "$departure").append("arrival", "$arrival"),
                            Accumulators.sum("count", 1)),
                    Aggregates.sort(Sorts.descending("count")),
                    Aggregates.limit(limit)));

            for (Document doc : aggregate) {
                results.add(doc);
            }
        } catch (Exception e) {
            System.err.println("[ActivityLogDAO] Erreur agrégation routes: " + e.getMessage());
        }
        return results;
    }

    /**
     * Agrégation: Événements par type
     */
    public List<Document> getEventsByType() {
        List<Document> results = new ArrayList<>();
        try {
            MongoCollection<Document> collection = MongoDBUtil.getCollection(COLLECTION_NAME);

            AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(
                    Aggregates.group("$event_type", Accumulators.sum("count", 1)),
                    Aggregates.sort(Sorts.descending("count"))));

            for (Document doc : aggregate) {
                results.add(doc);
            }
        } catch (Exception e) {
            System.err.println("[ActivityLogDAO] Erreur agrégation événements: " + e.getMessage());
        }
        return results;
    }

    /**
     * Compte les recherches aujourd'hui (global)
     */
    public long countSearchesToday() {
        try {
            MongoCollection<Document> collection = MongoDBUtil.getCollection(SEARCH_LOGS_COLLECTION);

            LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
            Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());

            return collection.countDocuments(Filters.gte("timestamp", startDate));
        } catch (Exception e) {
            System.err.println("[ActivityLogDAO] Erreur count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Compte les recherches d'un utilisateur aujourd'hui
     */
    public long countSearchesTodayByUser(Long userId) {
        try {
            MongoCollection<Document> collection = MongoDBUtil.getCollection(SEARCH_LOGS_COLLECTION);

            LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
            Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());

            return collection.countDocuments(Filters.and(
                    Filters.gte("timestamp", startDate),
                    Filters.eq("user_id", userId)));
        } catch (Exception e) {
            System.err.println("[ActivityLogDAO] Erreur count user: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Compte les événements par type pour aujourd'hui
     */
    public long countEventsTodayByType(String eventType) {
        try {
            MongoCollection<Document> collection = MongoDBUtil.getCollection(COLLECTION_NAME);

            LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
            Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());

            return collection.countDocuments(Filters.and(
                    Filters.gte("timestamp", startDate),
                    Filters.eq("event_type", eventType)));
        } catch (Exception e) {
            System.err.println("[ActivityLogDAO] Erreur count by type: " + e.getMessage());
            return 0;
        }
    }
}
