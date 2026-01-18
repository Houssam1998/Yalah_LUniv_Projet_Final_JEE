package com.uemf.yalah.dao;

import com.uemf.yalah.model.User;
import com.uemf.yalah.model.enums.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * DAO pour les opérations sur les utilisateurs
 */
public class UserDAO extends GenericDAO<User, Long> {

    public UserDAO() {
        super(User.class);
    }

    /**
     * Trouve un utilisateur par email
     */
    public Optional<User> findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve un utilisateur par ID avec ses véhicules chargés
     */
    public Optional<User> findByIdWithVehicles(Long userId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u LEFT JOIN FETCH u.vehicles WHERE u.id = :userId", User.class);
            query.setParameter("userId", userId);
            User user = query.getSingleResult();
            // Force initialize the collection before closing EntityManager
            if (user.getVehicles() != null) {
                user.getVehicles().size();
            }
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Vérifie si un email existe déjà
     */
    public boolean emailExists(String email) {
        return findByEmail(email).isPresent();
    }

    /**
     * Trouve les utilisateurs par rôle
     */
    public List<User> findByRole(UserRole role) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.role = :role ORDER BY u.lastName, u.firstName",
                    User.class);
            query.setParameter("role", role);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les utilisateurs vérifiés UEMF
     */
    public List<User> findVerifiedUsers() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.verified = true ORDER BY u.rating DESC",
                    User.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Recherche d'utilisateurs par nom
     */
    public List<User> searchByName(String searchTerm) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE LOWER(u.firstName) LIKE :term " +
                            "OR LOWER(u.lastName) LIKE :term ORDER BY u.lastName",
                    User.class);
            query.setParameter("term", "%" + searchTerm.toLowerCase() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les meilleurs conducteurs (par note)
     */
    public List<User> findTopDrivers(int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.ratingCount >= 5 " +
                            "ORDER BY u.rating DESC",
                    User.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Met à jour la note d'un utilisateur
     */
    public void updateRating(Long userId, float newRating, int newCount) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userId);
            if (user != null) {
                user.setRating(newRating);
                user.setRatingCount(newCount);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Active/Désactive un utilisateur
     */
    public void setActive(Long userId, boolean active) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userId);
            if (user != null) {
                user.setActive(active);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Compte tous les utilisateurs
     */
    public long countAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM User u",
                    Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
