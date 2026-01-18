package com.uemf.yalah.dao;

import com.uemf.yalah.model.Booking;
import com.uemf.yalah.model.enums.BookingStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO pour les opérations sur les réservations
 */
public class BookingDAO extends GenericDAO<Booking, Long> {

    public BookingDAO() {
        super(Booking.class);
    }

    /**
     * Trouve les réservations d'un passager avec toutes les associations
     * nécessaires
     */
    public List<Booking> findByPassenger(Long passengerId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Booking> query = em.createQuery(
                    "SELECT DISTINCT b FROM Booking b " +
                            "JOIN FETCH b.ride r " +
                            "JOIN FETCH r.driver " +
                            "JOIN FETCH r.vehicle " +
                            "WHERE b.passenger.id = :passengerId " +
                            "ORDER BY r.departureTime DESC",
                    Booking.class);
            query.setParameter("passengerId", passengerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les réservations pour un trajet
     */
    public List<Booking> findByRide(Long rideId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Booking> query = em.createQuery(
                    "SELECT b FROM Booking b JOIN FETCH b.passenger " +
                            "WHERE b.ride.id = :rideId " +
                            "ORDER BY b.bookedAt DESC",
                    Booking.class);
            query.setParameter("rideId", rideId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les réservations en attente pour un conducteur
     */
    public List<Booking> findPendingForDriver(Long driverId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Booking> query = em.createQuery(
                    "SELECT b FROM Booking b JOIN FETCH b.passenger JOIN FETCH b.ride r " +
                            "WHERE r.driver.id = :driverId AND b.status = :status " +
                            "ORDER BY b.bookedAt ASC",
                    Booking.class);
            query.setParameter("driverId", driverId);
            query.setParameter("status", BookingStatus.PENDING);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les réservations confirmées d'un passager avec toutes les associations
     */
    public List<Booking> findConfirmedByPassenger(Long passengerId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Booking> query = em.createQuery(
                    "SELECT DISTINCT b FROM Booking b " +
                            "JOIN FETCH b.ride r " +
                            "JOIN FETCH r.driver " +
                            "JOIN FETCH r.vehicle " +
                            "WHERE b.passenger.id = :passengerId " +
                            "AND b.status = :status " +
                            "ORDER BY r.departureTime ASC",
                    Booking.class);
            query.setParameter("passengerId", passengerId);
            query.setParameter("status", BookingStatus.CONFIRMED);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Vérifie si un utilisateur a déjà réservé pour un trajet
     */
    public boolean hasBooking(Long passengerId, Long rideId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(b) FROM Booking b WHERE b.passenger.id = :passengerId " +
                            "AND b.ride.id = :rideId " +
                            "AND b.status NOT IN (:cancelled, :rejected)",
                    Long.class);
            query.setParameter("passengerId", passengerId);
            query.setParameter("rideId", rideId);
            query.setParameter("cancelled", BookingStatus.CANCELLED);
            query.setParameter("rejected", BookingStatus.REJECTED);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    /**
     * Met à jour le statut d'une réservation
     */
    public void updateStatus(Long bookingId, BookingStatus status) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Booking booking = em.find(Booking.class, bookingId);
            if (booking != null) {
                booking.setStatus(status);
                if (status == BookingStatus.CONFIRMED) {
                    booking.confirm();
                }
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
     * Compte les réservations en attente pour un conducteur
     */
    public long countPendingForDriver(Long driverId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(b) FROM Booking b " +
                            "WHERE b.ride.driver.id = :driverId " +
                            "AND b.status = :status",
                    Long.class);
            query.setParameter("driverId", driverId);
            query.setParameter("status", BookingStatus.PENDING);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve une réservation par ID avec toutes les associations chargées
     */
    public java.util.Optional<Booking> findByIdWithDetails(Long id) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Booking> query = em.createQuery(
                    "SELECT b FROM Booking b " +
                            "JOIN FETCH b.ride r " +
                            "JOIN FETCH r.driver " +
                            "JOIN FETCH r.vehicle " +
                            "JOIN FETCH b.passenger " +
                            "WHERE b.id = :id",
                    Booking.class);
            query.setParameter("id", id);
            return query.getResultList().stream().findFirst();
        } finally {
            em.close();
        }
    }

    /**
     * Compte les réservations d'un passager
     */
    public long countByPassenger(Long passengerId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(b) FROM Booking b WHERE b.passenger.id = :passengerId",
                    Long.class);
            query.setParameter("passengerId", passengerId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Compte toutes les réservations
     */
    public long countAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(b) FROM Booking b",
                    Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les réservations confirmées pour un trajet
     */
    public List<Booking> findConfirmedByRide(Long rideId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Booking> query = em.createQuery(
                    "SELECT b FROM Booking b JOIN FETCH b.passenger " +
                            "WHERE b.ride.id = :rideId AND b.status = :status",
                    Booking.class);
            query.setParameter("rideId", rideId);
            query.setParameter("status", BookingStatus.CONFIRMED);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
