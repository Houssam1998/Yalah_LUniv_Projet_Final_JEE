package com.uemf.yalah.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller for static pages (about, contact, faq, etc.)
 */
@WebServlet(urlPatterns = { "/about", "/contact", "/faq", "/safety", "/terms", "/privacy" })
public class StaticPagesController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String viewName;

        switch (path) {
            case "/about":
                viewName = "about";
                break;
            case "/contact":
                viewName = "contact";
                break;
            case "/faq":
                viewName = "faq";
                break;
            case "/safety":
                viewName = "safety";
                break;
            case "/terms":
                viewName = "terms";
                break;
            case "/privacy":
                viewName = "privacy";
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }

        request.getRequestDispatcher("/WEB-INF/views/static/" + viewName + ".jsp")
                .forward(request, response);
    }
}
