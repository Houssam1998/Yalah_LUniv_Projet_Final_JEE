package com.uemf.yalah.controller;

import com.uemf.yalah.model.Ride;
import com.uemf.yalah.model.User;
import com.uemf.yalah.service.RideService;
import com.uemf.yalah.service.BookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur pour la page d'accueil
 */
@WebServlet(urlPatterns = { "/home" })
public class HomeController extends HttpServlet {

    private RideService rideService;
    private BookingService bookingService;

    @Override
    public void init() throws ServletException {
        rideService = new RideService();
        bookingService = new BookingService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Trajets récents
        List<Ride> recentRides = rideService.findRecentRides(6);
        request.setAttribute("recentRides", recentRides);

        // Trajets vers/depuis le campus
        List<Ride> campusRides = rideService.findCampusRides(4);
        request.setAttribute("campusRides", campusRides);

        // Statistiques globales
        request.setAttribute("activeRidesCount", rideService.countActiveRides());

        // Si l'utilisateur est connecté, ajouter ses infos
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User currentUser = (User) session.getAttribute("user");

            // Trajets à venir du conducteur
            request.setAttribute("upcomingRides", rideService.findUpcomingByDriver(currentUser.getId()));

            // Réservations en attente
            request.setAttribute("pendingBookingsCount",
                    bookingService.countPendingForDriver(currentUser.getId()));

            // Prochains trajets réservés
            request.setAttribute("confirmedBookings",
                    bookingService.findConfirmedByPassenger(currentUser.getId()));
        }

        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}
