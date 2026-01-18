package com.uemf.yalah.controller;

import com.uemf.yalah.model.Booking;
import com.uemf.yalah.model.Ride;
import com.uemf.yalah.model.User;
import com.uemf.yalah.service.BookingService;
import com.uemf.yalah.service.NotificationService;
import com.uemf.yalah.service.RideService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des réservations
 */
@WebServlet(urlPatterns = { "/bookings/*" })
public class BookingController extends HttpServlet {

    private BookingService bookingService;
    private RideService rideService;

    @Override
    public void init() throws ServletException {
        bookingService = new BookingService();
        rideService = new RideService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        switch (pathInfo) {
            case "/":
            case "/my-bookings":
                showMyBookings(request, response);
                break;
            default:
                // Vérifier si c'est un ID de réservation: /bookings/123
                if (pathInfo.matches("/\\d+")) {
                    String bookingId = pathInfo.substring(1);
                    showBookingDetails(request, response, Long.parseLong(bookingId));
                } else {
                    response.sendRedirect(request.getContextPath() + "/bookings/my-bookings");
                }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        switch (pathInfo) {
            case "/create":
                processCreate(request, response);
                break;
            case "/confirm":
                processConfirm(request, response);
                break;
            case "/reject":
                processReject(request, response);
                break;
            case "/cancel":
                processCancel(request, response);
                break;
            case "/complete":
                processComplete(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/bookings");
        }
    }

    private void showMyBookings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        List<Booking> bookings = bookingService.findByPassenger(currentUser.getId());
        request.setAttribute("bookings", bookings);

        // Réservations confirmées pour le dashboard
        List<Booking> confirmed = bookingService.findConfirmedByPassenger(currentUser.getId());
        request.setAttribute("confirmedBookings", confirmed);

        // Heure actuelle pour afficher le bouton Valider seulement pour les trajets
        // passés
        request.setAttribute("currentTime", java.time.LocalDateTime.now());

        request.getRequestDispatcher("/WEB-INF/views/bookings/my-bookings.jsp").forward(request, response);
    }

    private void showBookingDetails(HttpServletRequest request, HttpServletResponse response, Long bookingId)
            throws ServletException, IOException {

        Optional<Booking> bookingOpt = bookingService.findById(bookingId);

        if (bookingOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Réservation non trouvée");
            return;
        }

        Booking booking = bookingOpt.get();
        request.setAttribute("booking", booking);

        request.getRequestDispatcher("/WEB-INF/views/bookings/details.jsp").forward(request, response);
    }

    private void processCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        String rideIdStr = request.getParameter("rideId");
        String seatsStr = request.getParameter("seats");
        String message = request.getParameter("message");

        try {
            Long rideId = Long.parseLong(rideIdStr);
            int seats = seatsStr != null ? Integer.parseInt(seatsStr) : 1;

            // Use findByIdWithRelations to eagerly load driver and vehicle
            Optional<Ride> rideOpt = rideService.findByIdWithRelations(rideId);
            if (rideOpt.isEmpty()) {
                throw new Exception("Trajet non trouvé");
            }

            Booking booking = bookingService.createBooking(
                    currentUser, rideOpt.get(), seats, message);

            response.sendRedirect(request.getContextPath() + "/bookings/" + booking.getId() + "?created=true");

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/rides/" + rideIdStr + "?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    private void processConfirm(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        User currentUser = (User) request.getSession().getAttribute("user");
        String bookingIdStr = request.getParameter("bookingId");
        NotificationService notificationService = new NotificationService();

        try {
            Long bookingId = Long.parseLong(bookingIdStr);
            Optional<Booking> bookingOpt = bookingService.findById(bookingId);

            if (bookingOpt.isPresent()) {
                Booking booking = bookingOpt.get();
                // Vérifier que l'utilisateur est bien le conducteur
                if (booking.getRide().getDriver().getId().equals(currentUser.getId())) {
                    bookingService.confirmBooking(booking);

                    // Envoyer notification au passager
                    String rideInfo = booking.getRide().getDepartureLocation() + " → "
                            + booking.getRide().getArrivalLocation();
                    notificationService.notifyBookingConfirmed(
                            booking.getPassenger().getId(),
                            currentUser.getFullName(),
                            rideInfo,
                            bookingId);
                }
            }

            response.sendRedirect(request.getContextPath() + "/rides/my-rides?confirmed=true");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/rides/my-rides?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    private void processReject(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        User currentUser = (User) request.getSession().getAttribute("user");
        String bookingIdStr = request.getParameter("bookingId");
        NotificationService notificationService = new NotificationService();

        try {
            Long bookingId = Long.parseLong(bookingIdStr);
            Optional<Booking> bookingOpt = bookingService.findById(bookingId);

            if (bookingOpt.isPresent()) {
                Booking booking = bookingOpt.get();
                // Vérifier que l'utilisateur est bien le conducteur
                if (booking.getRide().getDriver().getId().equals(currentUser.getId())) {
                    bookingService.rejectBooking(booking);

                    // Envoyer notification au passager
                    String rideInfo = booking.getRide().getDepartureLocation() + " → "
                            + booking.getRide().getArrivalLocation();
                    notificationService.notifyBookingRejected(
                            booking.getPassenger().getId(),
                            currentUser.getFullName(),
                            rideInfo);
                }
            }

            response.sendRedirect(request.getContextPath() + "/rides/my-rides?rejected=true");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/rides/my-rides?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    private void processCancel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        User currentUser = (User) request.getSession().getAttribute("user");
        String bookingIdStr = request.getParameter("bookingId");
        String reason = request.getParameter("reason");
        NotificationService notificationService = new NotificationService();

        try {
            Long bookingId = Long.parseLong(bookingIdStr);
            Optional<Booking> bookingOpt = bookingService.findById(bookingId);

            if (bookingOpt.isPresent()) {
                Booking booking = bookingOpt.get();
                // Vérifier que l'utilisateur est bien le passager
                if (booking.getPassenger().getId().equals(currentUser.getId())) {
                    // Notifier le conducteur de l'annulation
                    String rideInfo = booking.getRide().getDepartureLocation() + " → "
                            + booking.getRide().getArrivalLocation();
                    notificationService.notifyBookingCancelled(
                            booking.getRide().getDriver().getId(),
                            currentUser.getFullName(),
                            rideInfo,
                            booking.getRide().getId(),
                            false // cancelled by passenger, not driver
                    );

                    bookingService.cancelBooking(booking, reason);
                }
            }

            response.sendRedirect(request.getContextPath() + "/bookings/my-bookings?cancelled=true");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/bookings/my-bookings?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    /**
     * Le passager confirme que le trajet est terminé
     */
    private void processComplete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        User currentUser = (User) request.getSession().getAttribute("user");
        String bookingIdStr = request.getParameter("bookingId");

        try {
            Long bookingId = Long.parseLong(bookingIdStr);
            Optional<Booking> bookingOpt = bookingService.findById(bookingId);

            if (bookingOpt.isPresent()) {
                Booking booking = bookingOpt.get();
                // Vérifier que l'utilisateur est bien le passager
                if (booking.getPassenger().getId().equals(currentUser.getId())) {
                    bookingService.completeBooking(booking);
                    // Pas de notification ici - juste marquer comme complété
                }
            }

            response.sendRedirect(request.getContextPath() + "/bookings/my-bookings?completed=true");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/bookings/my-bookings?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }
}
