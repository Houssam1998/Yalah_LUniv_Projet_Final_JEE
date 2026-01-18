package com.uemf.yalah.filter;

import com.uemf.yalah.model.User;
import com.uemf.yalah.service.MessageService;
import com.uemf.yalah.service.NotificationService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filtre pour charger les compteurs (notifications, messages) dans la session
 * pour affichage dans le header
 */
@WebFilter("/*")
public class HeaderDataFilter implements Filter {

    private NotificationService notificationService;
    private MessageService messageService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        notificationService = new NotificationService();
        messageService = new MessageService();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);

        // Charger les compteurs si l'utilisateur est connecté
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");

            try {
                // Compteur notifications non lues
                long unreadNotifications = notificationService.countUnread(user.getId());
                session.setAttribute("unreadNotifications", unreadNotifications);

                // Compteur messages non lus
                long unreadMessages = messageService.countUnread(user.getId());
                session.setAttribute("unreadMessages", unreadMessages);
            } catch (Exception e) {
                // En cas d'erreur, on continue sans les compteurs
                System.err.println("[HeaderDataFilter] Erreur: " + e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
