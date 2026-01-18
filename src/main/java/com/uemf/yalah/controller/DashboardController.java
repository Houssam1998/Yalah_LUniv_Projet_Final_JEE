package com.uemf.yalah.controller;

import com.uemf.yalah.dao.ActivityLogDAO;
import com.uemf.yalah.dao.BookingDAO;
import com.uemf.yalah.dao.RideDAO;
import com.uemf.yalah.dao.UserDAO;
import com.uemf.yalah.model.User;
import com.uemf.yalah.util.MongoDBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur pour le Dashboard Analytics
 * Combine données PostgreSQL et MongoDB (Polyglot Persistence)
 */
@WebServlet("/dashboard/*")
public class DashboardController extends HttpServlet {

    private final RideDAO rideDAO = new RideDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialiser la connexion MongoDB au démarrage
        MongoDBUtil.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        System.out.println("[DashboardController] GET pathInfo: " + pathInfo);

        switch (pathInfo) {
            case "/":
                showDashboard(request, response);
                break;
            case "/admin":
                showAdminDashboard(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    /**
     * Affiche le dashboard utilisateur avec ses statistiques
     */
    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        // Stats PostgreSQL
        long totalRides = rideDAO.countByDriver(currentUser.getId());
        long activeRides = rideDAO.countActiveByDriver(currentUser.getId());
        long totalBookings = bookingDAO.countByPassenger(currentUser.getId());

        // Stats MongoDB (si connecté)
        long searchesToday = 0;
        List<Document> recentActivity = List.of();

        if (MongoDBUtil.isConnected()) {
            searchesToday = activityLogDAO.countSearchesTodayByUser(currentUser.getId());
            recentActivity = activityLogDAO.getRecentLogs(10);
        }

        request.setAttribute("totalRides", totalRides);
        request.setAttribute("activeRides", activeRides);
        request.setAttribute("totalBookings", totalBookings);
        request.setAttribute("searchesToday", searchesToday);
        request.setAttribute("recentActivity", recentActivity);
        request.setAttribute("mongoConnected", MongoDBUtil.isConnected());

        request.getRequestDispatcher("/WEB-INF/views/dashboard/index.jsp").forward(request, response);
    }

    /**
     * Affiche le dashboard admin avec statistiques globales
     */
    private void showAdminDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        // Vérifier le rôle admin
        if (currentUser.getRole() == null ||
                !currentUser.getRole().toString().equals("ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        // Stats globales PostgreSQL
        long totalUsers = userDAO.countAll();
        long totalRides = rideDAO.countAll();
        long totalBookings = bookingDAO.countAll();

        // Stats MongoDB (si connecté)
        List<Document> topRoutes = List.of();
        List<Document> eventsByType = List.of();
        List<Document> recentLogs = List.of();

        if (MongoDBUtil.isConnected()) {
            topRoutes = activityLogDAO.getTopSearchedRoutes(5);
            eventsByType = activityLogDAO.getEventsByType();
            recentLogs = activityLogDAO.getRecentLogs(20);
        }

        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalRides", totalRides);
        request.setAttribute("totalBookings", totalBookings);
        request.setAttribute("topRoutes", topRoutes);
        request.setAttribute("eventsByType", eventsByType);
        request.setAttribute("recentLogs", recentLogs);
        request.setAttribute("mongoConnected", MongoDBUtil.isConnected());

        request.getRequestDispatcher("/WEB-INF/views/dashboard/admin.jsp").forward(request, response);
    }

    @Override
    public void destroy() {
        // Fermer proprement la connexion MongoDB
        MongoDBUtil.close();
        super.destroy();
    }
}
