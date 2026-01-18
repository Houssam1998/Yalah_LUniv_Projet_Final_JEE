package com.uemf.yalah.filter;

import jakarta.servlet.*;

import java.io.IOException;

/**
 * TripCompletionChecker - DÉSACTIVÉ
 * 
 * Ce filtre créait des problèmes de notifications en double.
 * La fonctionnalité de notification de trajet terminé devrait être
 * gérée différemment:
 * - Soit par un scheduled job (Quartz, Spring Scheduler)
 * - Soit manuellement par le conducteur via le bouton "Terminer"
 * 
 * Pour le moment, ce filtre ne fait rien.
 */
// @WebFilter("/*") - DISABLED
public class TripCompletionChecker implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Disabled
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Just pass through - no trip checking
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup
    }
}
