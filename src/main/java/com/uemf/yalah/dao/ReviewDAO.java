package com.uemf.yalah.dao;

import com.uemf.yalah.model.Review;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * DAO pour les opérations sur les avis
 */
public class ReviewDAO extends GenericDAO<Review, Long> {

    public ReviewDAO() {
        super(Review.class);
    }

    /**
     * Trouve les avis reçus par un utilisateur
     */
    public List<Review> findByReviewed(Long userId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Review> query = em.createQuery(
                    "SELECT r FROM Review r " +
                            "JOIN FETCH r.reviewer " +
                            "JOIN FETCH r.ride " +
                            "WHERE r.reviewed.id = :userId " +
                            "ORDER BY r.createdAt DESC",
                    Review.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les avis donnés par un utilisateur
     */
    public List<Review> findByReviewer(Long userId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Review> query = em.createQuery(
                    "SELECT r FROM Review r " +
                            "JOIN FETCH r.reviewed " +
                            "JOIN FETCH r.ride " +
                            "WHERE r.reviewer.id = :userId " +
                            "ORDER BY r.createdAt DESC",
                    Review.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Vérifie si un avis existe déjà pour un trajet et un reviewer
     */
    public boolean existsByRideAndReviewer(Long rideId, Long reviewerId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM Review r " +
                            "WHERE r.ride.id = :rideId AND r.reviewer.id = :reviewerId",
                    Long.class);
            query.setParameter("rideId", rideId);
            query.setParameter("reviewerId", reviewerId);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    /**
     * Trouve un avis par trajet et reviewer
     */
    public Optional<Review> findByRideAndReviewer(Long rideId, Long reviewerId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Review> query = em.createQuery(
                    "SELECT r FROM Review r " +
                            "WHERE r.ride.id = :rideId AND r.reviewer.id = :reviewerId",
                    Review.class);
            query.setParameter("rideId", rideId);
            query.setParameter("reviewerId", reviewerId);
            return query.getResultList().stream().findFirst();
        } finally {
            em.close();
        }
    }

    /**
     * Calcule la note moyenne d'un utilisateur
     */
    public Double calculateAverageRating(Long userId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT AVG(r.rating) FROM Review r WHERE r.reviewed.id = :userId",
                    Double.class);
            query.setParameter("userId", userId);
            Double avg = query.getSingleResult();
            return avg != null ? avg : 0.0;
        } finally {
            em.close();
        }
    }

    /**
     * Compte les avis d'un utilisateur
     */
    public long countByReviewed(Long userId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM Review r WHERE r.reviewed.id = :userId",
                    Long.class);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
