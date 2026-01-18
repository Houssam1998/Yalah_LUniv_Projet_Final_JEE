package com.uemf.yalah.dao;

import com.uemf.yalah.model.Notification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO pour les opérations sur les notifications
 */
public class NotificationDAO extends GenericDAO<Notification, Long> {

    public NotificationDAO() {
        super(Notification.class);
    }

    /**
     * Trouve toutes les notifications d'un utilisateur (non lues en premier)
     */
    public List<Notification> findByUser(Long userId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Notification> query = em.createQuery(
                    "SELECT n FROM Notification n " +
                            "WHERE n.user.id = :userId " +
                            "ORDER BY n.isRead ASC, n.createdAt DESC",
                    Notification.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les notifications non lues d'un utilisateur
     */
    public List<Notification> findUnreadByUser(Long userId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Notification> query = em.createQuery(
                    "SELECT n FROM Notification n " +
                            "WHERE n.user.id = :userId AND n.isRead = false " +
                            "ORDER BY n.createdAt DESC",
                    Notification.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Compte les notifications non lues d'un utilisateur
     */
    public long countUnreadByUser(Long userId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(n) FROM Notification n " +
                            "WHERE n.user.id = :userId AND n.isRead = false",
                    Long.class);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Marque une notification comme lue
     */
    public void markAsRead(Long notificationId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Notification notification = em.find(Notification.class, notificationId);
            if (notification != null) {
                notification.setIsRead(true);
                notification.setReadAt(java.time.LocalDateTime.now());
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /**
     * Marque toutes les notifications d'un utilisateur comme lues
     */
    public void markAllAsRead(Long userId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery(
                    "UPDATE Notification n SET n.isRead = true, n.readAt = :now " +
                            "WHERE n.user.id = :userId AND n.isRead = false")
                    .setParameter("now", java.time.LocalDateTime.now())
                    .setParameter("userId", userId)
                    .executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les notifications récentes d'un utilisateur (limitées)
     */
    public List<Notification> findRecentByUser(Long userId, int limit) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Notification> query = em.createQuery(
                    "SELECT n FROM Notification n " +
                            "WHERE n.user.id = :userId " +
                            "ORDER BY n.createdAt DESC",
                    Notification.class);
            query.setParameter("userId", userId);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
