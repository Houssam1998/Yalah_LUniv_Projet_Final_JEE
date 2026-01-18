package com.uemf.yalah.service;

import com.uemf.yalah.dao.UserDAO;
import com.uemf.yalah.model.User;
import com.uemf.yalah.model.enums.UserRole;
import com.uemf.yalah.util.PasswordUtil;

import java.util.Optional;

/**
 * Service pour l'authentification et la gestion des utilisateurs
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Inscription d'un nouvel utilisateur
     */
    public User register(String email, String password, String firstName,
            String lastName, UserRole role) throws Exception {

        // Vérifier si l'email existe déjà
        if (userDAO.emailExists(email)) {
            throw new Exception("Cet email est déjà utilisé");
        }

        // Valider le mot de passe
        if (!PasswordUtil.isStrongPassword(password)) {
            throw new Exception("Le mot de passe doit contenir au moins 8 caractères, " +
                    "une majuscule, une minuscule et un chiffre");
        }

        // Créer l'utilisateur
        User user = new User();
        user.setEmail(email.toLowerCase().trim());
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setRole(role);

        // Vérification automatique pour les emails UEMF
        if (user.isUemfEmail()) {
            user.setVerified(true);
        }

        return userDAO.save(user);
    }

    /**
     * Connexion d'un utilisateur
     */
    public Optional<User> login(String email, String password) {
        Optional<User> userOpt = userDAO.findByEmail(email.toLowerCase().trim());

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        // Vérifier le mot de passe
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            return Optional.empty();
        }

        // Vérifier que le compte est actif
        if (!user.getActive()) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    /**
     * Met à jour le profil d'un utilisateur
     */
    public User updateProfile(User user, String firstName, String lastName,
            String phone, String bio) {
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setPhone(phone);
        user.setBio(bio);
        return userDAO.update(user);
    }

    /**
     * Met à jour les préférences d'un utilisateur
     */
    public User updatePreferences(User user, Boolean prefMusic,
            Boolean prefSmoking, Boolean prefTalking) {
        user.setPrefMusic(prefMusic);
        user.setPrefSmoking(prefSmoking);
        user.setPrefTalking(prefTalking);
        return userDAO.update(user);
    }

    /**
     * Change le mot de passe d'un utilisateur
     */
    public boolean changePassword(User user, String oldPassword, String newPassword) {
        // Vérifier l'ancien mot de passe
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            return false;
        }

        // Valider le nouveau mot de passe
        if (!PasswordUtil.isStrongPassword(newPassword)) {
            return false;
        }

        user.setPassword(PasswordUtil.hashPassword(newPassword));
        userDAO.update(user);
        return true;
    }

    /**
     * Trouve un utilisateur par ID
     */
    public Optional<User> findById(Long userId) {
        return userDAO.findById(userId);
    }

    /**
     * Trouve un utilisateur par email
     */
    public Optional<User> findByEmail(String email) {
        return userDAO.findByEmail(email.toLowerCase().trim());
    }
}
