package com.uemf.yalah.model.enums;

/**
 * Statuts possibles pour une réservation
 */
public enum BookingStatus {
    PENDING("En attente"),
    CONFIRMED("Confirmée"),
    REJECTED("Refusée"),
    CANCELLED("Annulée"),
    COMPLETED("Terminée");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
