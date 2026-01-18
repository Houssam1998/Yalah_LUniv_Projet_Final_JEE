package com.uemf.yalah.model.enums;

/**
 * Roles disponibles pour les utilisateurs de l'application
 */
public enum UserRole {
    STUDENT("Étudiant"),
    PROFESSOR("Professeur"),
    STAFF("Personnel"),
    ADMIN("Administrateur");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
