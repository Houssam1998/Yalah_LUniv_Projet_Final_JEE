package com.uemf.yalah.service;

import com.uemf.yalah.dao.MessageDAO;
import com.uemf.yalah.model.Message;
import com.uemf.yalah.model.Ride;
import com.uemf.yalah.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des messages
 */
public class MessageService {

    private final MessageDAO messageDAO;

    public MessageService() {
        this.messageDAO = new MessageDAO();
    }

    /**
     * Envoie un message
     */
    public Message sendMessage(User sender, User receiver, String content) {
        Message message = new Message(sender, receiver, content);
        return messageDAO.save(message);
    }

    /**
     * Envoie un message lié à un trajet
     */
    public Message sendMessage(User sender, User receiver, Ride ride, String content) {
        Message message = new Message(ride, sender, receiver, content);
        return messageDAO.save(message);
    }

    /**
     * Récupère les conversations d'un utilisateur
     */
    public List<Message> getConversations(Long userId) {
        return messageDAO.findConversations(userId);
    }

    /**
     * Récupère les messages entre deux utilisateurs
     */
    public List<Message> getMessagesBetween(Long userId1, Long userId2) {
        return messageDAO.findMessagesBetween(userId1, userId2);
    }

    /**
     * Récupère les messages d'un trajet
     */
    public List<Message> getMessagesByRide(Long rideId) {
        return messageDAO.findByRide(rideId);
    }

    /**
     * Compte les messages non lus
     */
    public long countUnread(Long userId) {
        return messageDAO.countUnread(userId);
    }

    /**
     * Marque les messages comme lus
     */
    public void markAsRead(Long receiverId, Long senderId) {
        messageDAO.markAsReadFrom(receiverId, senderId);
    }

    /**
     * Trouve un message par ID
     */
    public Optional<Message> findById(Long messageId) {
        return messageDAO.findById(messageId);
    }
}
