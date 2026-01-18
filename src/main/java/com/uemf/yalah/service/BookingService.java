package com.uemf.yalah.service;

import com.uemf.yalah.dao.BookingDAO;
import com.uemf.yalah.dao.RideDAO;
import com.uemf.yalah.model.Booking;
import com.uemf.yalah.model.Ride;
import com.uemf.yalah.model.User;
import com.uemf.yalah.model.enums.BookingStatus;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des réservations
 */
public class BookingService {

    private final BookingDAO bookingDAO;
    private final RideDAO rideDAO;
    private final NotificationService notificationService;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.rideDAO = new RideDAO();
        this.notificationService = new NotificationService();
    }

    /**
     * Crée une nouvelle réservation
     */
    public Booking createBooking(User passenger, Ride ride, int seatsRequested,
            String message) throws Exception {

        // Vérifier que le passager n'est pas le conducteur
        if (ride.getDriver().getId().equals(passenger.getId())) {
            throw new Exception("Vous ne pouvez pas réserver sur votre propre trajet");
        }

        // Vérifier qu'il n'y a pas déjà une réservation
        if (bookingDAO.hasBooking(passenger.getId(), ride.getId())) {
            throw new Exception("Vous avez déjà une réservation pour ce trajet");
        }

        // Vérifier la disponibilité
        if (!ride.canBook(seatsRequested)) {
            throw new Exception("Pas assez de places disponibles");
        }

        // Créer la réservation
        Booking booking = new Booking(ride, passenger, seatsRequested);
        booking.setMessage(message);

        // Si réservation instantanée, confirmer directement
        if (ride.getInstantBooking()) {
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.confirm();
        }

        // Réduire les places disponibles
        ride.bookSeats(seatsRequested);
        rideDAO.update(ride);

        Booking savedBooking = bookingDAO.save(booking);

        // Envoyer notification au conducteur
        String rideInfo = ride.getDepartureLocation() + " → " + ride.getArrivalLocation();
        if (ride.getInstantBooking()) {
            // Notification de confirmation automatique au passager
            notificationService.notifyBookingConfirmed(
                    passenger.getId(),
                    ride.getDriver().getFullName(),
                    rideInfo,
                    savedBooking.getId());
            // Notification au conducteur qu'une réservation instantanée a été faite
            notificationService.notifyBookingRequest(
                    ride.getDriver().getId(),
                    passenger.getFullName(),
                    rideInfo + " (Réservation instantanée)",
                    savedBooking.getId());
        } else {
            // Notification de demande au conducteur
            notificationService.notifyBookingRequest(
                    ride.getDriver().getId(),
                    passenger.getFullName(),
                    rideInfo,
                    savedBooking.getId());
        }

        return savedBooking;
    }

    /**
     * Confirme une réservation (par le conducteur)
     */
    public Booking confirmBooking(Booking booking) {
        booking.confirm();
        return bookingDAO.update(booking);
    }

    /**
     * Rejette une réservation (par le conducteur)
     */
    public Booking rejectBooking(Booking booking) {
        booking.reject();
        return bookingDAO.update(booking);
    }

    /**
     * Annule une réservation (par le passager)
     */
    public Booking cancelBooking(Booking booking, String reason) {
        booking.cancel(reason);
        return bookingDAO.update(booking);
    }

    /**
     * Termine une réservation
     */
    public Booking completeBooking(Booking booking) {
        booking.complete();
        return bookingDAO.update(booking);
    }

    /**
     * Trouve les réservations d'un passager
     */
    public List<Booking> findByPassenger(Long passengerId) {
        return bookingDAO.findByPassenger(passengerId);
    }

    /**
     * Trouve les réservations pour un trajet
     */
    public List<Booking> findByRide(Long rideId) {
        return bookingDAO.findByRide(rideId);
    }

    /**
     * Trouve les réservations en attente pour un conducteur
     */
    public List<Booking> findPendingForDriver(Long driverId) {
        return bookingDAO.findPendingForDriver(driverId);
    }

    /**
     * Trouve les réservations confirmées d'un passager
     */
    public List<Booking> findConfirmedByPassenger(Long passengerId) {
        return bookingDAO.findConfirmedByPassenger(passengerId);
    }

    /**
     * Trouve une réservation par ID avec toutes les associations
     */
    public Optional<Booking> findById(Long bookingId) {
        return bookingDAO.findByIdWithDetails(bookingId);
    }

    /**
     * Compte les réservations en attente pour un conducteur
     */
    public long countPendingForDriver(Long driverId) {
        return bookingDAO.countPendingForDriver(driverId);
    }

    /**
     * Trouve les réservations confirmées pour un trajet
     */
    public List<Booking> findConfirmedByRide(Long rideId) {
        return bookingDAO.findConfirmedByRide(rideId);
    }
}
