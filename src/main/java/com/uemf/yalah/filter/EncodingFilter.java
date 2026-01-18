package com.uemf.yalah.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Filtre pour définir l'encodage UTF-8 sur toutes les requêtes
 */
@WebFilter("/*")
public class EncodingFilter implements Filter {

    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String configEncoding = filterConfig.getInitParameter("encoding");
        if (configEncoding != null) {
            this.encoding = configEncoding;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        // Skip Content-Type modification for static resources
        boolean isStaticResource = requestURI.endsWith(".css") ||
                requestURI.endsWith(".js") ||
                requestURI.endsWith(".png") ||
                requestURI.endsWith(".jpg") ||
                requestURI.endsWith(".svg") ||
                requestURI.endsWith(".webp") ||
                requestURI.endsWith(".ico") ||
                requestURI.endsWith(".woff") ||
                requestURI.endsWith(".woff2");

        // Encodage de la requete
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(encoding);
        }

        // Encodage de la reponse (seulement pour HTML)
        response.setCharacterEncoding(encoding);
        if (!isStaticResource) {
            httpResponse.setHeader("Content-Type", "text/html; charset=" + encoding);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup si nécessaire
    }
}
