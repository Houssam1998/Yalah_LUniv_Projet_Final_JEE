package com.uemf.yalah.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitaire pour le hachage et la vérification des mots de passe
 */
public class PasswordUtil {

    private static final int BCRYPT_ROUNDS = 12;

    private PasswordUtil() {
        // Constructeur privé
    }

    /**
     * Hache un mot de passe en clair avec BCrypt
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Vérifie si un mot de passe en clair correspond au hash
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    /**
     * Valide la force d'un mot de passe
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c))
                hasUpper = true;
            else if (Character.isLowerCase(c))
                hasLower = true;
            else if (Character.isDigit(c))
                hasDigit = true;
        }

        return hasUpper && hasLower && hasDigit;
    }
}
