package com.uemf.yalah.service;

import com.uemf.yalah.dao.NotificationDAO;
import com.uemf.yalah.dao.UserDAO;
import com.uemf.yalah.model.Notification;
import com.uemf.yalah.model.User;
import com.uemf.yalah.model.enums.NotificationType;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des notifications
 */
public class NotificationService {

    private final NotificationDAO notificationDAO;
    private final UserDAO userDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * Crée une notification pour un utilisateur
     */
    public Notification createNotification(Long userId, NotificationType type,
            String title, String message, String relatedUrl) {

        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé: " + userId);
        }

        Notification notification = new Notification();
        notification.setUser(userOpt.get());
        notification.setType(type.name()); // Convert enum to String
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRelatedUrl(relatedUrl);
        notification.setIsRead(false);

        return notificationDAO.save(notification);
    }

    /**
     * Crée une notification de demande de réservation
     */
    public void notifyBookingRequest(Long driverId, String passengerName,
            String rideInfo, Long bookingId) {
        createNotification(
                driverId,
                NotificationType.BOOKING_REQUEST,
                "Nouvelle demande de réservation",
                passengerName + " souhaite réserver une place pour " + rideInfo,
                "/rides/my-rides"); // Redirect to my-rides where driver can manage bookings
    }

    /**
     * Crée une notification de confirmation de réservation
     */
    public void notifyBookingConfirmed(Long passengerId, String driverName,
            String rideInfo, Long bookingId) {
        createNotification(
                passengerId,
                NotificationType.BOOKING_CONFIRMED,
                "Réservation confirmée !",
                driverName + " a accepté votre réservation pour " + rideInfo,
                "/bookings/details/" + bookingId);
    }

    /**
     * Crée une notification de refus de réservation
     */
    public void notifyBookingRejected(Long passengerId, String driverName,
            String rideInfo) {
        createNotification(
                passengerId,
                NotificationType.BOOKING_REJECTED,
                "Réservation refusée",
                driverName + " n'a pas pu accepter votre réservation pour " + rideInfo,
                "/rides/search");
    }

    /**
     * Crée une notification d'annulation de réservation
     */
    public void notifyBookingCancelled(Long userId, String otherUserName,
            String rideInfo, Long rideId, boolean isCancelledByDriver) {
        createNotification(
                userId,
                NotificationType.BOOKING_CANCELLED,
                "Réservation annulée",
                isCancelledByDriver
                        ? "Le conducteur a annulé le trajet " + rideInfo
                        : otherUserName + " a annulé sa réservation pour " + rideInfo,
                rideId != null ? "/rides/" + rideId : "/rides/my-rides");
    }

    /**
     * Crée une notification de trajet terminé (pour rappeler de noter)
     */
    public void notifyRideCompleted(Long userId, String rideInfo, Long rideId, boolean isDriver) {
        createNotification(
                userId,
                NotificationType.RIDE_REMINDER,
                "Trajet terminé !",
                isDriver
                        ? "Votre trajet " + rideInfo + " est terminé. Notez vos passagers !"
                        : "Votre trajet " + rideInfo + " est terminé. N'oubliez pas de noter le conducteur !",
                isDriver ? "/rides/my-rides" : "/bookings/my-bookings");
    }

    /**
     * Crée une notification de nouveau message
     */
    public void notifyNewMessage(Long receiverId, String senderName, Long conversationId) {
        createNotification(
                receiverId,
                NotificationType.NEW_MESSAGE,
                "Nouveau message",
                "Vous avez reçu un message de " + senderName,
                "/messages/" + conversationId);
    }

    /**
     * Crée une notification de rappel de trajet
     */
    public void notifyRideReminder(Long userId, String rideInfo, Long rideId, boolean isDriver) {
        createNotification(
                userId,
                NotificationType.RIDE_REMINDER,
                "Rappel: trajet demain",
                isDriver
                        ? "Rappel: vous avez un trajet prévu demain - " + rideInfo
                        : "Rappel: vous êtes inscrit au trajet de demain - " + rideInfo,
                "/rides/details/" + rideId);
    }

    /**
     * Crée une notification d'avis reçu
     */
    public void notifyReviewReceived(Long userId, String reviewerName, int rating) {
        createNotification(
                userId,
                NotificationType.REVIEW_RECEIVED,
                "Nouvel avis reçu",
                reviewerName + " vous a attribué " + rating + " étoiles",
                "/profile");
    }

    /**
     * Récupère toutes les notifications d'un utilisateur
     */
    public List<Notification> getNotifications(Long userId) {
        return notificationDAO.findByUser(userId);
    }

    /**
     * Récupère les notifications non lues
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationDAO.findUnreadByUser(userId);
    }

    /**
     * Compte les notifications non lues
     */
    public long countUnread(Long userId) {
        return notificationDAO.countUnreadByUser(userId);
    }

    /**
     * Récupère les notifications récentes (pour le header)
     */
    public List<Notification> getRecentNotifications(Long userId, int limit) {
        return notificationDAO.findRecentByUser(userId, limit);
    }

    /**
     * Marque une notification comme lue
     */
    public void markAsRead(Long notificationId) {
        notificationDAO.markAsRead(notificationId);
    }

    /**
     * Marque toutes les notifications comme lues
     */
    public void markAllAsRead(Long userId) {
        notificationDAO.markAllAsRead(userId);
    }
}
