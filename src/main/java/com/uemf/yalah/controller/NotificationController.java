package com.uemf.yalah.controller;

import com.uemf.yalah.model.Notification;
import com.uemf.yalah.model.User;
import com.uemf.yalah.service.NotificationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur pour la gestion des notifications
 */
@WebServlet("/notifications/*")
public class NotificationController extends HttpServlet {

    private final NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        System.out.println("[NotificationController] GET pathInfo: " + pathInfo);

        switch (pathInfo) {
            case "/":
                listNotifications(request, response);
                break;
            default:
                // Check if reading a specific notification
                if (pathInfo.startsWith("/read/")) {
                    String notifIdStr = pathInfo.substring(6);
                    try {
                        Long notifId = Long.parseLong(notifIdStr);
                        markAsReadAndRedirect(request, response, notifId);
                    } catch (NumberFormatException e) {
                        response.sendRedirect(request.getContextPath() + "/notifications");
                    }
                } else {
                    response.sendRedirect(request.getContextPath() + "/notifications");
                }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        System.out.println("[NotificationController] POST pathInfo: " + pathInfo);

        switch (pathInfo) {
            case "/read-all":
                markAllAsRead(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/notifications");
        }
    }

    /**
     * Affiche la liste des notifications
     */
    private void listNotifications(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        List<Notification> notifications = notificationService.getNotifications(currentUser.getId());
        long unreadCount = notificationService.countUnread(currentUser.getId());

        request.setAttribute("notifications", notifications);
        request.setAttribute("unreadCount", unreadCount);

        request.getRequestDispatcher("/WEB-INF/views/notifications/list.jsp").forward(request, response);
    }

    /**
     * Marque une notification comme lue et redirige vers l'URL associée
     */
    private void markAsReadAndRedirect(HttpServletRequest request, HttpServletResponse response,
            Long notificationId) throws IOException {

        notificationService.markAsRead(notificationId);

        // Récupérer l'URL de redirection depuis le paramètre ou rediriger vers
        // notifications
        String redirectUrl = request.getParameter("redirect");
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            response.sendRedirect(request.getContextPath() + redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/notifications");
        }
    }

    /**
     * Marque toutes les notifications comme lues
     */
    private void markAllAsRead(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        User currentUser = (User) request.getSession().getAttribute("user");
        notificationService.markAllAsRead(currentUser.getId());

        response.sendRedirect(request.getContextPath() + "/notifications?success=all_read");
    }
}
