package com.uemf.yalah.service;

import com.uemf.yalah.dao.BookingDAO;
import com.uemf.yalah.dao.ReviewDAO;
import com.uemf.yalah.dao.UserDAO;
import com.uemf.yalah.model.Booking;
import com.uemf.yalah.model.Review;
import com.uemf.yalah.model.User;
import com.uemf.yalah.model.enums.BookingStatus;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des avis
 */
public class ReviewService {

    private final ReviewDAO reviewDAO;
    private final BookingDAO bookingDAO;
    private final UserDAO userDAO;
    private final NotificationService notificationService;

    public ReviewService() {
        this.reviewDAO = new ReviewDAO();
        this.bookingDAO = new BookingDAO();
        this.userDAO = new UserDAO();
        this.notificationService = new NotificationService();
    }

    /**
     * Crée ou met à jour un avis pour un trajet
     */
    public Review createReview(Long bookingId, Long reviewerId, int rating, String comment) {
        // Récupérer la réservation
        Optional<Booking> bookingOpt = bookingDAO.findByIdWithDetails(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Réservation non trouvée");
        }

        Booking booking = bookingOpt.get();

        // Vérifier que le trajet est terminé
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalStateException("Vous ne pouvez noter qu'un trajet terminé");
        }

        // Vérifier que le reviewer est bien le passager ou le conducteur
        Long passengerId = booking.getPassenger().getId();
        Long driverId = booking.getRide().getDriver().getId();

        if (!reviewerId.equals(passengerId) && !reviewerId.equals(driverId)) {
            throw new IllegalArgumentException("Vous ne pouvez pas noter ce trajet");
        }

        // Déterminer qui est noté
        User reviewed = reviewerId.equals(passengerId)
                ? booking.getRide().getDriver() // Le passager note le conducteur
                : booking.getPassenger(); // Le conducteur note le passager

        User reviewer = reviewerId.equals(passengerId)
                ? booking.getPassenger()
                : booking.getRide().getDriver();

        // Vérifier si un avis existe déjà - si oui, le mettre à jour
        Optional<Review> existingReview = reviewDAO.findByRideAndReviewer(booking.getRide().getId(), reviewerId);

        Review review;
        boolean isUpdate = false;

        if (existingReview.isPresent()) {
            // Mise à jour
            review = existingReview.get();
            review.setRating(rating);
            review.setComment(comment);
            reviewDAO.update(review);
            isUpdate = true;
        } else {
            // Création
            review = new Review();
            review.setRide(booking.getRide());
            review.setReviewer(reviewer);
            review.setReviewed(reviewed);
            review.setRating(rating);
            review.setComment(comment);
            reviewDAO.save(review);
        }

        // Mettre à jour le rating de l'utilisateur noté
        updateUserRating(reviewed.getId());

        // Envoyer une notification seulement si nouvelle création
        if (!isUpdate) {
            notificationService.notifyReviewReceived(reviewed.getId(), reviewer.getFullName(), rating);
        }

        return review;
    }

    /**
     * Met à jour le rating moyen d'un utilisateur
     */
    private void updateUserRating(Long userId) {
        Double avgRating = reviewDAO.calculateAverageRating(userId);
        long count = reviewDAO.countByReviewed(userId);

        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRating(avgRating.floatValue());
            user.setRatingCount((int) count);
            userDAO.update(user);
        }
    }

    /**
     * Récupère les avis reçus par un utilisateur
     */
    public List<Review> getReviewsReceived(Long userId) {
        return reviewDAO.findByReviewed(userId);
    }

    /**
     * Récupère les avis donnés par un utilisateur
     */
    public List<Review> getReviewsGiven(Long userId) {
        return reviewDAO.findByReviewer(userId);
    }

    /**
     * Vérifie si l'utilisateur peut noter (ou modifier son avis sur) un trajet
     */
    public boolean canReview(Long bookingId, Long userId) {
        Optional<Booking> bookingOpt = bookingDAO.findByIdWithDetails(bookingId);
        if (bookingOpt.isEmpty())
            return false;

        Booking booking = bookingOpt.get();

        // Doit être terminé
        if (booking.getStatus() != BookingStatus.COMPLETED)
            return false;

        // Doit être participant
        Long passengerId = booking.getPassenger().getId();
        Long driverId = booking.getRide().getDriver().getId();
        if (!userId.equals(passengerId) && !userId.equals(driverId))
            return false;

        // Permet la création ET la modification d'un avis existant
        return true;
    }

    /**
     * Récupère la note moyenne d'un utilisateur
     */
    public Double getAverageRating(Long userId) {
        return reviewDAO.calculateAverageRating(userId);
    }
}
