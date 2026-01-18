package com.uemf.yalah.dao;

import com.uemf.yalah.model.Ride;
import com.uemf.yalah.model.enums.RideStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO pour les opérations sur les trajets
 */
public class RideDAO extends GenericDAO<Ride, Long> {

    public RideDAO() {
        super(Ride.class);
    }

    /**
     * Trouve un trajet par ID avec ses relations chargées (driver, vehicle)
     */
    public java.util.Optional<Ride> findByIdWithRelations(Long rideId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Ride> query = em.createQuery(
                    "SELECT r FROM Ride r " +
                            "LEFT JOIN FETCH r.driver " +
                            "LEFT JOIN FETCH r.vehicle " +
                            "WHERE r.id = :rideId",
                    Ride.class);
            query.setParameter("rideId", rideId);
            return java.util.Optional.of(query.getSingleResult());
        } catch (jakarta.persistence.NoResultException e) {
            return java.util.Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve tous les trajets avec leurs relations chargées (pour API)
     */
    public List<Ride> findAllWithRelations() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Ride> query = em.createQuery(
                    "SELECT DISTINCT r FROM Ride r " +
                            "LEFT JOIN FETCH r.driver " +
                            "LEFT JOIN FETCH r.vehicle " +
                            "ORDER BY r.departureTime DESC",
                    Ride.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Recherche de trajets disponibles
     */
    public List<Ride> searchRides(String departure, String arrival, LocalDateTime date,
            int minSeats, Double maxPrice) {
        EntityManager em = getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT r FROM Ride r " +
                            "LEFT JOIN FETCH r.driver " +
                            "LEFT JOIN FETCH r.vehicle " +
                            "WHERE r.status = :status " +
                            "AND r.availableSeats >= :minSeats " +
                            "AND r.departureTime > :now ");

            if (departure != null && !departure.isEmpty()) {
                jpql.append("AND LOWER(r.departureLocation) LIKE :departure ");
            }
            if (arrival != null && !arrival.isEmpty()) {
                jpql.append("AND LOWER(r.arrivalLocation) LIKE :arrival ");
            }
            if (date != null) {
                jpql.append("AND DATE(r.departureTime) = DATE(:date) ");
            }
            if (maxPrice != null) {
                jpql.append("AND r.pricePerSeat <= :maxPrice ");
            }
            jpql.append("ORDER BY r.departureTime ASC");

            TypedQuery<Ride> query = em.createQuery(jpql.toString(), Ride.class);
            query.setParameter("status", RideStatus.SCHEDULED);
            query.setParameter("minSeats", minSeats);
            query.setParameter("now", LocalDateTime.now());

            if (departure != null && !departure.isEmpty()) {
                query.setParameter("departure", "%" + departure.toLowerCase() + "%");
            }
            if (arrival != null && !arrival.isEmpty()) {
                query.setParameter("arrival", "%" + arrival.toLowerCase() + "%");
            }
            if (date != null) {
                query.setParameter("date", date);
            }
            if (maxPrice != null) {
                query.setParameter("maxPrice", maxPrice);
            }

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Recherche tous les trajets disponibles pour filtrage par proximité
     * Retourne tous les trajets avec coordonnées pour calcul de distance côté
     * application
     */
    public List<Ride> findAvailableRidesWithCoordinates(LocalDateTime date, int minSeats, Double maxPrice) {
        EntityManager em = getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT r FROM Ride r " +
                            "LEFT JOIN FETCH r.driver " +
                            "LEFT JOIN FETCH r.vehicle " +
                            "WHERE r.status = :status " +
                            "AND r.availableSeats >= :minSeats " +
                            "AND r.departureTime > :now " +
                            "AND r.departureLat IS NOT NULL " +
                            "AND r.arrivalLat IS NOT NULL ");

            if (date != null) {
                jpql.append("AND DATE(r.departureTime) = DATE(:date) ");
            }
            if (maxPrice != null) {
                jpql.append("AND r.pricePerSeat <= :maxPrice ");
            }
            jpql.append("ORDER BY r.departureTime ASC");

            TypedQuery<Ride> query = em.createQuery(jpql.toString(), Ride.class);
            query.setParameter("status", RideStatus.SCHEDULED);
            query.setParameter("minSeats", minSeats);
            query.setParameter("now", LocalDateTime.now());

            if (date != null) {
                query.setParameter("date", date);
            }
            if (maxPrice != null) {
                query.setParameter("maxPrice", maxPrice);
            }

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les trajets d'un conducteur
     */
    public List<Ride> findByDriver(Long driverId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Ride> query = em.createQuery(
                    "SELECT DISTINCT r FROM Ride r " +
                            "LEFT JOIN FETCH r.driver " +
                            "LEFT JOIN FETCH r.vehicle " +
                            "WHERE r.driver.id = :driverId " +
                            "ORDER BY r.departureTime DESC",
                    Ride.class);
            query.setParameter("driverId", driverId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les trajets à venir d'un conducteur
     */
    public List<Ride> findUpcomingByDriver(Long driverId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Ride> query = em.createQuery(
                    "SELECT r FROM Ride r WHERE r.driver.id = :driverId " +
                            "AND r.departureTime > :now AND r.status = :status " +
                            "ORDER BY r.departureTime ASC",
                    Ride.class);
            query.setParameter("driverId", driverId);
            query.setParameter("now", LocalDateTime.now());
            query.setParameter("status", RideStatus.SCHEDULED);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les trajets récents (pour la page d'accueil)
     */
    public List<Ride> findRecentRides(int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Ride> query = em.createQuery(
                    "SELECT DISTINCT r FROM Ride r " +
                            "LEFT JOIN FETCH r.driver " +
                            "LEFT JOIN FETCH r.vehicle " +
                            "WHERE r.status = :status " +
                            "AND r.departureTime > :now AND r.availableSeats > 0 " +
                            "ORDER BY r.createdAt DESC",
                    Ride.class);
            query.setParameter("status", RideStatus.SCHEDULED);
            query.setParameter("now", LocalDateTime.now());
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les trajets populaires (campus UEMF)
     */
    public List<Ride> findRidesToCampus(int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Ride> query = em.createQuery(
                    "SELECT DISTINCT r FROM Ride r " +
                            "LEFT JOIN FETCH r.driver " +
                            "LEFT JOIN FETCH r.vehicle " +
                            "WHERE r.status = :status " +
                            "AND r.departureTime > :now " +
                            "AND (LOWER(r.arrivalLocation) LIKE '%uemf%' " +
                            "OR LOWER(r.arrivalLocation) LIKE '%euromed%' " +
                            "OR LOWER(r.departureLocation) LIKE '%uemf%' " +
                            "OR LOWER(r.departureLocation) LIKE '%euromed%') " +
                            "ORDER BY r.departureTime ASC",
                    Ride.class);
            query.setParameter("status", RideStatus.SCHEDULED);
            query.setParameter("now", LocalDateTime.now());
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Met à jour le statut d'un trajet
     */
    public void updateStatus(Long rideId, RideStatus status) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Ride ride = em.find(Ride.class, rideId);
            if (ride != null) {
                ride.setStatus(status);
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
     * Compte les trajets actifs
     */
    public long countActiveRides() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM Ride r WHERE r.status = :status " +
                            "AND r.departureTime > :now",
                    Long.class);
            query.setParameter("status", RideStatus.SCHEDULED);
            query.setParameter("now", LocalDateTime.now());
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Compte les trajets d'un conducteur
     */
    public long countByDriver(Long driverId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM Ride r WHERE r.driver.id = :driverId",
                    Long.class);
            query.setParameter("driverId", driverId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Compte les trajets actifs d'un conducteur
     */
    public long countActiveByDriver(Long driverId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM Ride r WHERE r.driver.id = :driverId " +
                            "AND r.status = :status AND r.departureTime > :now",
                    Long.class);
            query.setParameter("driverId", driverId);
            query.setParameter("status", RideStatus.SCHEDULED);
            query.setParameter("now", LocalDateTime.now());
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Compte tous les trajets
     */
    public long countAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM Ride r",
                    Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les trajets dont l'heure d'arrivée est passée mais qui ne sont pas
     * encore terminés
     * (pour envoyer des notifications de rappel)
     */
    public List<Ride> findRidesNeedingCompletion(LocalDateTime now) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Ride> query = em.createQuery(
                    "SELECT r FROM Ride r JOIN FETCH r.driver " +
                            "WHERE r.status = :scheduled " +
                            "AND (r.estimatedArrivalTime < :now OR " +
                            "     (r.estimatedArrivalTime IS NULL AND r.departureTime < :pastTime))",
                    Ride.class);
            query.setParameter("scheduled", RideStatus.SCHEDULED);
            query.setParameter("now", now);
            query.setParameter("pastTime", now.minusHours(2)); // 2h après le départ si pas d'heure d'arrivée
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les trajets nécessitant une notification de trajet terminé
     * Vérifie que completionNotified = false pour éviter les doublons
     */
    public List<Ride> findRidesNeedingCompletionNotification(LocalDateTime now) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Ride> query = em.createQuery(
                    "SELECT r FROM Ride r JOIN FETCH r.driver " +
                            "WHERE r.status = :scheduled " +
                            "AND (r.completionNotified = false OR r.completionNotified IS NULL) " +
                            "AND (r.estimatedArrivalTime < :now OR " +
                            "     (r.estimatedArrivalTime IS NULL AND r.departureTime < :pastTime))",
                    Ride.class);
            query.setParameter("scheduled", RideStatus.SCHEDULED);
            query.setParameter("now", now);
            query.setParameter("pastTime", now.minusHours(2)); // 2h après le départ si pas d'heure d'arrivée
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
