package com.uemf.yalah.dao;

import com.uemf.yalah.model.Message;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * DAO pour les opérations sur les messages
 */
public class MessageDAO extends GenericDAO<Message, Long> {

    public MessageDAO() {
        super(Message.class);
    }

    /**
     * Trouve les conversations d'un utilisateur (messages groupés par
     * interlocuteur)
     */
    public List<Message> findConversations(Long userId) {
        EntityManager em = getEntityManager();
        try {
            // Récupère le dernier message de chaque conversation avec eager loading
            TypedQuery<Message> query = em.createQuery(
                    "SELECT m FROM Message m " +
                            "JOIN FETCH m.sender " +
                            "JOIN FETCH m.receiver " +
                            "WHERE m.id IN (" +
                            "SELECT MAX(m2.id) FROM Message m2 " +
                            "WHERE m2.sender.id = :userId OR m2.receiver.id = :userId " +
                            "GROUP BY CASE WHEN m2.sender.id = :userId THEN m2.receiver.id ELSE m2.sender.id END" +
                            ") ORDER BY m.sentAt DESC",
                    Message.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les messages entre deux utilisateurs
     */
    public List<Message> findMessagesBetween(Long userId1, Long userId2) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Message> query = em.createQuery(
                    "SELECT m FROM Message m " +
                            "JOIN FETCH m.sender " +
                            "JOIN FETCH m.receiver " +
                            "WHERE (m.sender.id = :user1 AND m.receiver.id = :user2) " +
                            "OR (m.sender.id = :user2 AND m.receiver.id = :user1) " +
                            "ORDER BY m.sentAt ASC",
                    Message.class);
            query.setParameter("user1", userId1);
            query.setParameter("user2", userId2);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve les messages pour un trajet
     */
    public List<Message> findByRide(Long rideId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Message> query = em.createQuery(
                    "SELECT m FROM Message m WHERE m.ride.id = :rideId " +
                            "ORDER BY m.sentAt ASC",
                    Message.class);
            query.setParameter("rideId", rideId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Compte les messages non lus pour un utilisateur
     */
    public long countUnread(Long userId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(m) FROM Message m " +
                            "WHERE m.receiver.id = :userId AND m.isRead = false",
                    Long.class);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Marque tous les messages d'un expéditeur comme lus
     */
    public void markAsReadFrom(Long receiverId, Long senderId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery(
                    "UPDATE Message m SET m.isRead = true " +
                            "WHERE m.receiver.id = :receiverId AND m.sender.id = :senderId " +
                            "AND m.isRead = false")
                    .setParameter("receiverId", receiverId)
                    .setParameter("senderId", senderId)
                    .executeUpdate();
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
}
