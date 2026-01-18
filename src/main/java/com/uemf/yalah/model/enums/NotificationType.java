package com.uemf.yalah.model.enums;

/**
 * Types de notifications dans l'application
 */
public enum NotificationType {

    // Réservations
    BOOKING_REQUEST("Demande de réservation", "booking"),
    BOOKING_CONFIRMED("Réservation confirmée", "booking"),
    BOOKING_REJECTED("Réservation refusée", "booking"),
    BOOKING_CANCELLED("Réservation annulée", "booking"),

    // Messages
    NEW_MESSAGE("Nouveau message", "message"),

    // Trajets
    RIDE_REMINDER("Rappel de trajet", "ride"),
    RIDE_CANCELLED("Trajet annulé", "ride"),

    // Avis
    REVIEW_RECEIVED("Avis reçu", "review"),
    REVIEW_REQUEST("Demande d'avis", "review"),

    // Système
    ACCOUNT_VERIFIED("Compte vérifié", "system"),
    WELCOME("Bienvenue", "system");

    private final String label;
    private final String category;

    NotificationType(String label, String category) {
        this.label = label;
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public String getCategory() {
        return category;
    }

    /**
     * Retourne l'icône emoji associée au type
     */
    public String getIcon() {
        return switch (this) {
            case BOOKING_REQUEST -> "📨";
            case BOOKING_CONFIRMED -> "✅";
            case BOOKING_REJECTED -> "❌";
            case BOOKING_CANCELLED -> "🚫";
            case NEW_MESSAGE -> "💬";
            case RIDE_REMINDER -> "⏰";
            case RIDE_CANCELLED -> "🚗❌";
            case REVIEW_RECEIVED -> "⭐";
            case REVIEW_REQUEST -> "📝";
            case ACCOUNT_VERIFIED -> "✓";
            case WELCOME -> "👋";
        };
    }
}
