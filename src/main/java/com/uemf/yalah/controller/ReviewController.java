package com.uemf.yalah.controller;

import com.uemf.yalah.dao.BookingDAO;
import com.uemf.yalah.model.Booking;
import com.uemf.yalah.model.Review;
import com.uemf.yalah.model.User;
import com.uemf.yalah.service.ReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des avis
 */
@WebServlet("/reviews/*")
public class ReviewController extends HttpServlet {

    private final ReviewService reviewService = new ReviewService();
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        System.out.println("[ReviewController] GET pathInfo: " + pathInfo);

        User currentUser = (User) request.getSession().getAttribute("user");

        // /reviews/create/bookingId
        if (pathInfo.startsWith("/create/")) {
            String bookingIdStr = pathInfo.substring(8);
            try {
                Long bookingId = Long.parseLong(bookingIdStr);
                showCreateForm(request, response, currentUser, bookingId);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/bookings/my-bookings");
            }
        }
        // /reviews/user/userId - Avis d'un utilisateur
        else if (pathInfo.startsWith("/user/")) {
            String userIdStr = pathInfo.substring(6);
            try {
                Long userId = Long.parseLong(userIdStr);
                showUserReviews(request, response, userId);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/bookings/my-bookings");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        System.out.println("[ReviewController] POST pathInfo: " + pathInfo);

        if (pathInfo.equals("/create")) {
            processCreateReview(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/bookings/my-bookings");
        }
    }

    /**
     * Affiche le formulaire de création d'avis
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response,
            User currentUser, Long bookingId) throws ServletException, IOException {

        // Vérifier si l'utilisateur peut noter ce trajet
        if (!reviewService.canReview(bookingId, currentUser.getId())) {
            response.sendRedirect(request.getContextPath() + "/bookings/my-bookings?error=cannot_review");
            return;
        }

        Optional<Booking> bookingOpt = bookingDAO.findByIdWithDetails(bookingId);
        if (bookingOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Réservation non trouvée");
            return;
        }

        Booking booking = bookingOpt.get();

        // Déterminer qui sera noté
        User toReview = currentUser.getId().equals(booking.getPassenger().getId())
                ? booking.getRide().getDriver()
                : booking.getPassenger();

        request.setAttribute("booking", booking);
        request.setAttribute("toReview", toReview);

        request.getRequestDispatcher("/WEB-INF/views/reviews/create.jsp").forward(request, response);
    }

    /**
     * Affiche les avis d'un utilisateur
     */
    private void showUserReviews(HttpServletRequest request, HttpServletResponse response,
            Long userId) throws ServletException, IOException {

        List<Review> reviews = reviewService.getReviewsReceived(userId);
        Double avgRating = reviewService.getAverageRating(userId);

        request.setAttribute("reviews", reviews);
        request.setAttribute("avgRating", avgRating);
        request.setAttribute("reviewCount", reviews.size());

        request.getRequestDispatcher("/WEB-INF/views/reviews/list.jsp").forward(request, response);
    }

    /**
     * Traite la création d'un avis
     */
    private void processCreateReview(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        String bookingIdStr = request.getParameter("bookingId");
        String ratingStr = request.getParameter("rating");
        String comment = request.getParameter("comment");

        if (bookingIdStr == null || ratingStr == null) {
            response.sendRedirect(request.getContextPath() + "/bookings/my-bookings?error=invalid_params");
            return;
        }

        try {
            Long bookingId = Long.parseLong(bookingIdStr);
            int rating = Integer.parseInt(ratingStr);

            if (rating < 1 || rating > 5) {
                response.sendRedirect(
                        request.getContextPath() + "/reviews/create/" + bookingId + "?error=invalid_rating");
                return;
            }

            reviewService.createReview(bookingId, currentUser.getId(), rating, comment);

            response.sendRedirect(request.getContextPath() + "/bookings/my-bookings?success=review_created");

        } catch (IllegalStateException e) {
            response.sendRedirect(request.getContextPath() + "/bookings/my-bookings?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/bookings/my-bookings?error=create_failed");
        }
    }
}
