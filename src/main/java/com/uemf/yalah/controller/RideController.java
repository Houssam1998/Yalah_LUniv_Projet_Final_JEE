package com.uemf.yalah.controller;

import com.uemf.yalah.dao.UserDAO;
import com.uemf.yalah.dto.RideSearchResult;
import com.uemf.yalah.model.Booking;
import com.uemf.yalah.model.Ride;
import com.uemf.yalah.model.User;
import com.uemf.yalah.model.Vehicle;
import com.uemf.yalah.dao.ActivityLogDAO;
import com.uemf.yalah.service.RideService;
import com.uemf.yalah.service.BookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des trajets
 */
@WebServlet(urlPatterns = { "/rides/*" })
public class RideController extends HttpServlet {

    private RideService rideService;
    private BookingService bookingService;
    private UserDAO userDAO;
    private com.uemf.yalah.service.NotificationService notificationService;

    @Override
    public void init() throws ServletException {
        rideService = new RideService();
        bookingService = new BookingService();
        userDAO = new UserDAO();
        notificationService = new com.uemf.yalah.service.NotificationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        System.out.println("[RideController] doGet called - pathInfo: '" + pathInfo + "'");

        if (pathInfo == null)
            pathInfo = "/";

        System.out.println("[RideController] Routing pathInfo: '" + pathInfo + "'");

        switch (pathInfo) {
            case "/":
            case "/search":
                System.out.println("[RideController] -> showSearchPage");
                showSearchPage(request, response);
                break;
            case "/create":
                System.out.println("[RideController] -> showCreatePage");
                showCreatePage(request, response);
                break;
            case "/my-rides":
                System.out.println("[RideController] -> showMyRides");
                showMyRides(request, response);
                break;
            default:
                // Vérifier si c'est un ID de trajet: /rides/123
                if (pathInfo.matches("/\\d+")) {
                    String rideId = pathInfo.substring(1);
                    System.out.println("[RideController] -> showRideDetails for ID: " + rideId);
                    showRideDetails(request, response, Long.parseLong(rideId));
                } else if (pathInfo.matches("/edit/\\d+")) {
                    String rideId = pathInfo.split("/")[2];
                    System.out.println("[RideController] -> showEditPage for ID: " + rideId);
                    showEditPage(request, response, Long.parseLong(rideId));
                } else {
                    System.out.println("[RideController] -> redirect to /rides/search (unknown path)");
                    response.sendRedirect(request.getContextPath() + "/rides/search");
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
            case "/update":
                processUpdate(request, response);
                break;
            case "/search":
                processSearch(request, response);
                break;
            case "/cancel":
                processCancel(request, response);
                break;
            case "/complete":
                processComplete(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/rides");
        }
    }

    private void showSearchPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Trajets récents pour affichage par défaut
        List<Ride> recentRides = rideService.findRecentRides(10);
        request.setAttribute("rides", recentRides);

        // Trajets populaires vers le campus
        List<Ride> campusRides = rideService.findCampusRides(5);
        request.setAttribute("campusRides", campusRides);

        request.getRequestDispatcher("/WEB-INF/views/rides/search.jsp").forward(request, response);
    }

    private void processSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String departure = request.getParameter("departure");
        String arrival = request.getParameter("arrival");
        String dateStr = request.getParameter("date");
        String seatsStr = request.getParameter("seats");
        String maxPriceStr = request.getParameter("maxPrice");

        // Coordinates for proximity search
        String depLatStr = request.getParameter("departureLat");
        String depLngStr = request.getParameter("departureLng");
        String arrLatStr = request.getParameter("arrivalLat");
        String arrLngStr = request.getParameter("arrivalLng");

        LocalDateTime date = null;
        if (dateStr != null && !dateStr.isEmpty()) {
            date = LocalDateTime.parse(dateStr + "T00:00:00");
        }

        int seats = 1;
        if (seatsStr != null && !seatsStr.isEmpty()) {
            seats = Integer.parseInt(seatsStr);
        }

        Double maxPrice = null;
        if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
            maxPrice = Double.parseDouble(maxPriceStr);
        }

        // Parse coordinates if provided
        Double userDepLat = parseDouble(depLatStr);
        Double userDepLng = parseDouble(depLngStr);
        Double userArrLat = parseDouble(arrLatStr);
        Double userArrLng = parseDouble(arrLngStr);

        List<RideSearchResult> searchResults;

        // If coordinates are provided, use proximity search
        if ((userDepLat != null && userDepLng != null) || (userArrLat != null && userArrLng != null)) {
            List<Ride> allRides = rideService.findAvailableRidesWithCoordinates(date, seats, maxPrice);
            searchResults = new ArrayList<>();

            for (Ride ride : allRides) {
                Double depProximity = null;
                Double arrProximity = null;

                // Calculate departure proximity
                if (userDepLat != null && userDepLng != null && ride.getDepartureLat() != null) {
                    depProximity = RideSearchResult.calculateDistance(
                            userDepLat, userDepLng,
                            ride.getDepartureLat(), ride.getDepartureLng());
                }

                // Calculate arrival proximity
                if (userArrLat != null && userArrLng != null && ride.getArrivalLat() != null) {
                    arrProximity = RideSearchResult.calculateDistance(
                            userArrLat, userArrLng,
                            ride.getArrivalLat(), ride.getArrivalLng());
                }

                // Filter: only include rides within reasonable distance (e.g., 20km for each
                // point)
                boolean isRelevant = true;
                if (depProximity != null && depProximity > 20)
                    isRelevant = false;
                if (arrProximity != null && arrProximity > 20)
                    isRelevant = false;

                if (isRelevant) {
                    searchResults.add(new RideSearchResult(ride, depProximity, arrProximity));
                }
            }

            // Sort by relevance (closest matches first)
            searchResults.sort(Comparator.comparingDouble(RideSearchResult::getRelevanceScore));
        } else {
            // Fall back to text-based search
            List<Ride> rides = rideService.searchRides(departure, arrival, date, seats, maxPrice);
            searchResults = new ArrayList<>();
            for (Ride ride : rides) {
                searchResults.add(new RideSearchResult(ride));
            }
        }

        request.setAttribute("searchResults", searchResults);
        request.setAttribute("departure", departure);
        request.setAttribute("arrival", arrival);
        request.setAttribute("date", dateStr);
        request.setAttribute("seats", seats);
        request.setAttribute("maxPrice", maxPriceStr);
        request.setAttribute("searchPerformed", true);
        request.setAttribute("hasProximity", userDepLat != null || userArrLat != null);

        // Log search to MongoDB for analytics
        try {
            User currentUser = (User) request.getSession().getAttribute("user");
            Long userId = currentUser != null ? currentUser.getId() : null;
            new ActivityLogDAO().logSearch(userId, departure, arrival, dateStr, searchResults.size());
        } catch (Exception e) {
            System.err.println("Failed to log search: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/rides/search.jsp").forward(request, response);
    }

    private Double parseDouble(String value) {
        if (value == null || value.isEmpty())
            return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void showCreatePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        // Query vehicles directly to avoid lazy initialization issues
        List<com.uemf.yalah.model.Vehicle> vehicles = rideService.findVehiclesByOwner(currentUser.getId());

        request.setAttribute("vehicles", vehicles);
        request.getRequestDispatcher("/WEB-INF/views/rides/create.jsp").forward(request, response);
    }

    private void processCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        String departureLocation = request.getParameter("departureLocation");
        String arrivalLocation = request.getParameter("arrivalLocation");
        String departureDateStr = request.getParameter("departureDate");
        String departureTimeStr = request.getParameter("departureTime");
        String seatsStr = request.getParameter("availableSeats");
        String priceStr = request.getParameter("pricePerSeat");
        String vehicleIdStr = request.getParameter("vehicleId");

        try {
            LocalDateTime departureTime = LocalDateTime.parse(
                    departureDateStr + "T" + departureTimeStr,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

            int seats = Integer.parseInt(seatsStr);
            BigDecimal price = new BigDecimal(priceStr);
            Long vehicleId = Long.parseLong(vehicleIdStr);

            // Récupérer le véhicule directement via le service
            Optional<com.uemf.yalah.model.Vehicle> vehicleOpt = rideService.findVehicleById(vehicleId);
            if (vehicleOpt.isEmpty()) {
                throw new Exception("Véhicule non trouvé");
            }
            Vehicle vehicle = vehicleOpt.get();

            // Vérifier que le véhicule appartient bien à l'utilisateur
            if (!vehicle.getOwner().getId().equals(currentUser.getId())) {
                throw new Exception("Ce véhicule ne vous appartient pas");
            }

            Ride ride = rideService.createRide(
                    currentUser, vehicle, departureLocation, arrivalLocation,
                    departureTime, seats, price);

            // Récupérer les options du trajet
            String instantBookingParam = request.getParameter("instantBooking");
            String allowsBaggageParam = request.getParameter("allowsBaggage");
            String allowsPetsParam = request.getParameter("allowsPets");
            String allowsDetoursParam = request.getParameter("allowsDetours");
            String description = request.getParameter("description");

            // Checkbox non coché = paramètre null; coché = "on"
            ride.setInstantBooking(instantBookingParam != null);
            ride.setAllowsBaggage(allowsBaggageParam != null);
            ride.setAllowsPets(allowsPetsParam != null);
            ride.setAllowsDetours(allowsDetoursParam != null);
            if (description != null && !description.isEmpty()) {
                ride.setDescription(description);
            }

            // Récupérer les coordonnées GPS si fournies
            String depLat = request.getParameter("departureLat");
            String depLng = request.getParameter("departureLng");
            String arrLat = request.getParameter("arrivalLat");
            String arrLng = request.getParameter("arrivalLng");

            if (depLat != null && !depLat.isEmpty()) {
                rideService.setCoordinates(ride,
                        Double.parseDouble(depLat), Double.parseDouble(depLng),
                        Double.parseDouble(arrLat), Double.parseDouble(arrLng));
            } else {
                // Save the ride options even if no coordinates
                rideService.updateRide(ride);
            }

            response.sendRedirect(request.getContextPath() + "/rides/" + ride.getId() + "?created=true");

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            showCreatePage(request, response);
        }
    }

    private void showRideDetails(HttpServletRequest request, HttpServletResponse response, Long rideId)
            throws ServletException, IOException {

        Optional<Ride> rideOpt = rideService.findByIdWithRelations(rideId);

        if (rideOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Trajet non trouvé");
            return;
        }

        Ride ride = rideOpt.get();
        request.setAttribute("ride", ride);

        // Récupérer les réservations de ce trajet
        request.setAttribute("bookings", bookingService.findByRide(rideId));

        request.getRequestDispatcher("/WEB-INF/views/rides/details.jsp").forward(request, response);
    }

    private void showMyRides(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        List<Ride> rides = rideService.findByDriver(currentUser.getId());
        request.setAttribute("rides", rides);

        // Réservations à confirmer
        request.setAttribute("pendingBookings", bookingService.findPendingForDriver(currentUser.getId()));

        // Heure actuelle pour afficher le bouton Valider seulement pour les trajets
        // passés
        request.setAttribute("currentTime", LocalDateTime.now());

        request.getRequestDispatcher("/WEB-INF/views/rides/my-rides.jsp").forward(request, response);
    }

    private void processCancel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        User currentUser = (User) request.getSession().getAttribute("user");
        String rideIdStr = request.getParameter("rideId");

        try {
            Long rideId = Long.parseLong(rideIdStr);
            Optional<Ride> rideOpt = rideService.findById(rideId);

            if (rideOpt.isPresent()) {
                Ride ride = rideOpt.get();
                // Vérifier que l'utilisateur est bien le conducteur
                if (ride.getDriver().getId().equals(currentUser.getId())) {
                    rideService.cancelRide(ride);
                }
            }

            response.sendRedirect(request.getContextPath() + "/rides/my-rides?cancelled=true");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/rides/my-rides?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    /**
     * Marque un trajet comme terminé et complète toutes les réservations confirmées
     * SEULEMENT si l'heure de départ est passée
     */
    private void processComplete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        User currentUser = (User) request.getSession().getAttribute("user");
        String rideIdStr = request.getParameter("rideId");
        System.out.println("[RideController] processComplete called for rideId: " + rideIdStr);

        try {
            Long rideId = Long.parseLong(rideIdStr);
            System.out.println("[RideController] Step 1: Finding ride with relations...");
            Optional<Ride> rideOpt = rideService.findByIdWithRelations(rideId);

            if (rideOpt.isPresent()) {
                Ride ride = rideOpt.get();
                System.out.println("[RideController] Step 2: Ride found - " + ride.getDepartureLocation());

                // Vérifier que l'utilisateur est bien le conducteur
                if (!ride.getDriver().getId().equals(currentUser.getId())) {
                    throw new Exception("Vous n'êtes pas le conducteur de ce trajet");
                }
                System.out.println("[RideController] Step 3: Driver verified");

                // Vérifier que l'heure de départ est passée
                if (ride.getDepartureTime().isAfter(LocalDateTime.now())) {
                    throw new Exception("Ce trajet n'a pas encore commencé. Vous pourrez le terminer après "
                            + ride.getDepartureTime().format(DateTimeFormatter.ofPattern("dd/MM à HH:mm")));
                }
                System.out.println("[RideController] Step 4: Departure time verified - past");

                // Marquer le trajet comme terminé
                System.out.println("[RideController] Step 5: Completing ride...");
                rideService.completeRide(ride);
                System.out.println("[RideController] Step 6: Ride completed successfully");

                // Compléter toutes les réservations confirmées et envoyer notifications
                System.out.println("[RideController] Step 7: Finding confirmed bookings...");
                List<Booking> bookings = bookingService.findConfirmedByRide(rideId);
                System.out.println("[RideController] Step 8: Found " + bookings.size() + " confirmed bookings");

                String rideInfo = ride.getDepartureLocation() + " → " + ride.getArrivalLocation();

                for (Booking booking : bookings) {
                    System.out.println("[RideController] Step 9: Completing booking " + booking.getId());
                    bookingService.completeBooking(booking);
                    System.out.println("[RideController] Step 10: Sending notification to passenger "
                            + booking.getPassenger().getId());
                    notificationService.notifyRideCompleted(
                            booking.getPassenger().getId(),
                            rideInfo,
                            rideId,
                            false // isDriver = false
                    );
                }

                // Notifier le conducteur
                System.out.println("[RideController] Step 11: Sending notification to driver");
                notificationService.notifyRideCompleted(
                        currentUser.getId(),
                        rideInfo,
                        rideId,
                        true // isDriver = true
                );
                System.out.println("[RideController] Step 12: All done!");
            } else {
                System.out.println("[RideController] ERROR: Ride not found for id " + rideId);
            }

            response.sendRedirect(request.getContextPath() + "/rides/my-rides?completed=true");

        } catch (Exception e) {
            System.err.println(
                    "[RideController] ERROR in processComplete: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/rides/my-rides?error="
                    + URLEncoder.encode(e.getMessage() != null ? e.getMessage() : "Erreur inconnue",
                            StandardCharsets.UTF_8));
        }
    }

    private void showEditPage(HttpServletRequest request, HttpServletResponse response, Long rideId)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");
        Optional<Ride> rideOpt = rideService.findByIdWithRelations(rideId);

        if (rideOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Trajet non trouvé");
            return;
        }

        Ride ride = rideOpt.get();
        if (!ride.getDriver().getId().equals(currentUser.getId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous ne pouvez pas modifier ce trajet");
            return;
        }

        List<com.uemf.yalah.model.Vehicle> vehicles = rideService.findVehiclesByOwner(currentUser.getId());
        request.setAttribute("ride", ride);
        request.setAttribute("vehicles", vehicles);
        request.getRequestDispatcher("/WEB-INF/views/rides/edit.jsp").forward(request, response);
    }

    private void processUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");
        String rideIdStr = request.getParameter("rideId");

        try {
            Long rideId = Long.parseLong(rideIdStr);
            Optional<Ride> rideOpt = rideService.findById(rideId);
            if (rideOpt.isEmpty())
                throw new Exception("Trajet introuvable");

            Ride ride = rideOpt.get();
            if (!ride.getDriver().getId().equals(currentUser.getId())) {
                throw new Exception("Non autorisé");
            }

            // Update fields
            ride.setDepartureLocation(request.getParameter("departureLocation"));
            ride.setArrivalLocation(request.getParameter("arrivalLocation"));

            String dateStr = request.getParameter("departureDate");
            String timeStr = request.getParameter("departureTime");
            ride.setDepartureTime(
                    LocalDateTime.parse(dateStr + "T" + timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));

            ride.setAvailableSeats(Integer.parseInt(request.getParameter("availableSeats")));
            ride.setPricePerSeat(new BigDecimal(request.getParameter("pricePerSeat")));

            Long vehicleId = Long.parseLong(request.getParameter("vehicleId"));
            Optional<Vehicle> vehicleOpt = rideService.findVehicleById(vehicleId);
            if (vehicleOpt.isPresent()) {
                ride.setVehicle(vehicleOpt.get());
            }

            // Options
            ride.setInstantBooking(request.getParameter("instantBooking") != null);
            ride.setAllowsBaggage(request.getParameter("allowsBaggage") != null);
            ride.setAllowsPets(request.getParameter("allowsPets") != null);
            ride.setAllowsDetours(request.getParameter("allowsDetours") != null);
            String desc = request.getParameter("description");
            if (desc != null)
                ride.setDescription(desc);

            // Coords (seulement si modifiés/présents)
            String depLat = request.getParameter("departureLat");
            if (depLat != null && !depLat.isEmpty()) {
                ride.setDepartureLat(Double.parseDouble(depLat));
                ride.setDepartureLng(Double.parseDouble(request.getParameter("departureLng")));
                ride.setArrivalLat(Double.parseDouble(request.getParameter("arrivalLat")));
                ride.setArrivalLng(Double.parseDouble(request.getParameter("arrivalLng")));
            }

            rideService.updateRide(ride);

            response.sendRedirect(request.getContextPath() + "/rides/" + ride.getId() + "?updated=true");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/rides/my-rides?error=UpdateFailed");
        }
    }
}
