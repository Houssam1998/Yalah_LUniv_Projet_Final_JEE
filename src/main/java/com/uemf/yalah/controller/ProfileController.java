package com.uemf.yalah.controller;

import com.uemf.yalah.dao.UserDAO;
import com.uemf.yalah.dao.VehicleDAO;
import com.uemf.yalah.model.User;
import com.uemf.yalah.model.Vehicle;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des profils utilisateurs
 */
@WebServlet("/profile/*")
public class ProfileController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        System.out.println("[ProfileController] GET pathInfo: " + pathInfo);

        switch (pathInfo) {
            case "/":
                showMyProfile(request, response);
                break;
            case "/edit":
                showEditProfile(request, response);
                break;
            case "/vehicles":
                showVehicles(request, response);
                break;
            default:
                // Vérifier si c'est un profil public: /profile/user/123
                if (pathInfo.startsWith("/user/")) {
                    String userIdStr = pathInfo.substring(6);
                    try {
                        Long userId = Long.parseLong(userIdStr);
                        showPublicProfile(request, response, userId);
                    } catch (NumberFormatException e) {
                        response.sendRedirect(request.getContextPath() + "/profile");
                    }
                } else {
                    response.sendRedirect(request.getContextPath() + "/profile");
                }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        System.out.println("[ProfileController] POST pathInfo: " + pathInfo);

        switch (pathInfo) {
            case "/edit":
                processEditProfile(request, response);
                break;
            case "/vehicles/add":
                processAddVehicle(request, response);
                break;
            case "/vehicles/delete":
                processDeleteVehicle(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/profile");
        }
    }

    /**
     * Affiche le profil de l'utilisateur connecté
     */
    private void showMyProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        // Charger les véhicules
        List<Vehicle> vehicles = vehicleDAO.findByOwner(currentUser.getId());

        // TODO: Charger les statistiques (trajets, km, CO2)
        // TODO: Charger les avis reçus

        request.setAttribute("user", currentUser);
        request.setAttribute("vehicles", vehicles);
        request.setAttribute("isOwnProfile", true);

        request.getRequestDispatcher("/WEB-INF/views/profile/view.jsp").forward(request, response);
    }

    /**
     * Affiche le formulaire d'édition du profil
     */
    private void showEditProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");
        request.setAttribute("user", currentUser);

        request.getRequestDispatcher("/WEB-INF/views/profile/edit.jsp").forward(request, response);
    }

    /**
     * Affiche la liste des véhicules
     */
    private void showVehicles(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");
        List<Vehicle> vehicles = vehicleDAO.findByOwner(currentUser.getId());

        request.setAttribute("vehicles", vehicles);

        request.getRequestDispatcher("/WEB-INF/views/profile/vehicles.jsp").forward(request, response);
    }

    /**
     * Affiche le profil public d'un utilisateur
     */
    private void showPublicProfile(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws ServletException, IOException {

        Optional<User> userOpt = userDAO.findById(userId);

        if (userOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur non trouvé");
            return;
        }

        User profileUser = userOpt.get();
        User currentUser = (User) request.getSession().getAttribute("user");

        // Vérifier si c'est son propre profil
        boolean isOwnProfile = currentUser != null && currentUser.getId().equals(userId);

        // Charger les véhicules (pour afficher le nombre)
        List<Vehicle> vehicles = vehicleDAO.findByOwner(userId);

        // TODO: Charger les avis reçus

        request.setAttribute("user", profileUser);
        request.setAttribute("vehicles", vehicles);
        request.setAttribute("isOwnProfile", isOwnProfile);

        request.getRequestDispatcher("/WEB-INF/views/profile/view.jsp").forward(request, response);
    }

    /**
     * Traite la modification du profil
     */
    private void processEditProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        // Récupérer les paramètres
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        String bio = request.getParameter("bio");

        // Préférences (using existing User model fields)
        boolean prefMusic = request.getParameter("prefMusic") != null;
        boolean prefTalking = request.getParameter("prefTalking") != null;
        boolean prefSmoking = request.getParameter("prefSmoking") != null;

        try {
            // Mettre à jour l'utilisateur
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setPhone(phone);
            currentUser.setBio(bio);
            currentUser.setPrefMusic(prefMusic);
            currentUser.setPrefTalking(prefTalking);
            currentUser.setPrefSmoking(prefSmoking);

            // Sauvegarder
            userDAO.update(currentUser);

            // Mettre à jour la session
            request.getSession().setAttribute("user", currentUser);

            response.sendRedirect(request.getContextPath() + "/profile?success=profile_updated");

        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la mise à jour: " + e.getMessage());
            request.setAttribute("user", currentUser);
            request.getRequestDispatcher("/WEB-INF/views/profile/edit.jsp").forward(request, response);
        }
    }

    /**
     * Ajoute un nouveau véhicule
     */
    private void processAddVehicle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");

        String brand = request.getParameter("brand");
        String model = request.getParameter("model");
        String color = request.getParameter("color");
        String licensePlate = request.getParameter("licensePlate");
        String seatsStr = request.getParameter("seats");

        try {
            int seats = Integer.parseInt(seatsStr);

            Vehicle vehicle = new Vehicle();
            vehicle.setBrand(brand);
            vehicle.setModel(model);
            vehicle.setColor(color);
            vehicle.setLicensePlate(licensePlate);
            vehicle.setSeats(seats);
            vehicle.setOwner(currentUser);
            vehicle.setActive(true);

            vehicleDAO.save(vehicle);

            response.sendRedirect(request.getContextPath() + "/profile/vehicles?success=vehicle_added");

        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de l'ajout: " + e.getMessage());
            showVehicles(request, response);
        }
    }

    /**
     * Supprime un véhicule
     */
    private void processDeleteVehicle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("user");
        String vehicleIdStr = request.getParameter("vehicleId");

        try {
            Long vehicleId = Long.parseLong(vehicleIdStr);

            // Vérifier que le véhicule appartient à l'utilisateur
            Optional<Vehicle> vehicleOpt = vehicleDAO.findByIdWithOwner(vehicleId);

            if (vehicleOpt.isPresent() && vehicleOpt.get().getOwner().getId().equals(currentUser.getId())) {
                // Soft delete - marquer comme inactif
                Vehicle vehicle = vehicleOpt.get();
                vehicle.setActive(false);
                vehicleDAO.update(vehicle);

                response.sendRedirect(request.getContextPath() + "/profile/vehicles?success=vehicle_deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/profile/vehicles?error=not_authorized");
            }

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/profile/vehicles?error=delete_failed");
        }
    }
}
