package com.uemf.yalah.controller;

import com.uemf.yalah.model.User;
import com.uemf.yalah.model.enums.UserRole;
import com.uemf.yalah.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Optional;

/**
 * Contrôleur pour l'authentification (login, register, logout)
 */
@WebServlet(urlPatterns = { "/auth/*" })
public class AuthController extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        switch (pathInfo) {
            case "/login":
                showLoginPage(request, response);
                break;
            case "/register":
                showRegisterPage(request, response);
                break;
            case "/logout":
                logout(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/auth/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        switch (pathInfo) {
            case "/login":
                processLogin(request, response);
                break;
            case "/register":
                processRegister(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/auth/login");
        }
    }

    private void showLoginPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si déjà connecté, rediriger vers l'accueil
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    private void showRegisterPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("roles", UserRole.values());
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    private void processLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");

        // Validation
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Veuillez remplir tous les champs");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        // Authentification
        Optional<User> userOpt = authService.login(email, password);

        if (userOpt.isEmpty()) {
            request.setAttribute("error", "Email ou mot de passe incorrect");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        User user = userOpt.get();

        // Créer la session
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());

        // Cookie "Remember me"
        if ("on".equals(remember)) {
            session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 jours
        }

        // Redirection vers l'URL demandée ou l'accueil
        String redirectUrl = (String) session.getAttribute("redirectUrl");
        if (redirectUrl != null) {
            session.removeAttribute("redirectUrl");
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    private void processRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String roleStr = request.getParameter("role");

        // Validation
        if (email == null || password == null || firstName == null || lastName == null) {
            request.setAttribute("error", "Veuillez remplir tous les champs obligatoires");
            request.setAttribute("roles", UserRole.values());
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Les mots de passe ne correspondent pas");
            request.setAttribute("email", email);
            request.setAttribute("firstName", firstName);
            request.setAttribute("lastName", lastName);
            request.setAttribute("roles", UserRole.values());
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        try {
            UserRole role = roleStr != null ? UserRole.valueOf(roleStr) : UserRole.STUDENT;
            User user = authService.register(email, password, firstName, lastName, role);

            // Connecter automatiquement après inscription
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());

            response.sendRedirect(request.getContextPath() + "/?registered=true");

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("email", email);
            request.setAttribute("firstName", firstName);
            request.setAttribute("lastName", lastName);
            request.setAttribute("roles", UserRole.values());
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/auth/login?logout=true");
    }
}
