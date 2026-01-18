package com.uemf.yalah.filter;

import com.uemf.yalah.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtre d'authentification pour protéger les pages nécessitant une connexion
 */
@WebFilter(urlPatterns = { "/rides/*", "/bookings/*", "/profile/*", "/messages/*" })
public class AuthenticationFilter implements Filter {

    // URLs publiques qui ne nécessitent pas d'authentification
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/auth/forgot-password",
            "/rides/search",
            "/rides/view",
            "/rides"); // Allow browsing rides without login

    // Extensions statiques à ignorer
    private static final List<String> STATIC_EXTENSIONS = Arrays.asList(
            ".css", ".js", ".png", ".jpg", ".jpeg", ".gif", ".webp", ".ico", ".woff", ".woff2");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation si nécessaire
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Ignorer les ressources statiques
        if (isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Ignorer les URLs publiques
        if (isPublicUrl(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Vérifier l'authentification
        HttpSession session = httpRequest.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            // Sauvegarder l'URL demandée pour redirection après login
            session = httpRequest.getSession(true);
            session.setAttribute("redirectUrl", requestURI);

            // Rediriger vers la page de connexion
            httpResponse.sendRedirect(contextPath + "/auth/login");
            return;
        }

        // Vérifier que le compte est actif
        if (!currentUser.getActive()) {
            session.invalidate();
            httpResponse.sendRedirect(contextPath + "/auth/login?error=account_disabled");
            return;
        }

        // Ajouter l'utilisateur comme attribut de requête pour faciliter l'accès
        httpRequest.setAttribute("currentUser", currentUser);

        chain.doFilter(request, response);
    }

    private boolean isStaticResource(String path) {
        return STATIC_EXTENSIONS.stream().anyMatch(path::endsWith);
    }

    private boolean isPublicUrl(String path) {
        return PUBLIC_URLS.stream().anyMatch(path::startsWith);
    }

    @Override
    public void destroy() {
        // Cleanup si nécessaire
    }
}
