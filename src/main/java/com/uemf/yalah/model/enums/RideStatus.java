package com.uemf.yalah.model.enums;

/**
 * Statuts possibles pour un trajet
 */
public enum RideStatus {
    SCHEDULED("Programmé"),
    IN_PROGRESS("En cours"),
    COMPLETED("Terminé"),
    CANCELLED("Annulé");

    private final String displayName;

    RideStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
