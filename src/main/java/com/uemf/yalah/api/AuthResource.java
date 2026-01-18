package com.uemf.yalah.api;

import com.google.gson.Gson;
import com.uemf.yalah.model.User;
import com.uemf.yalah.model.enums.UserRole;
import com.uemf.yalah.service.AuthService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * API REST pour l'authentification (Mobile Android/iOS)
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private AuthService authService = new AuthService();
    private Gson gson = new Gson();

    /**
     * DTO pour le login
     */
    public static class LoginRequest {
        public String email;
        public String password;
    }

    /**
     * DTO pour l'inscription
     */
    public static class RegisterRequest {
        public String email;
        public String password;
        public String firstName;
        public String lastName;
        public String role; // "STUDENT" ou "PROFESSOR"
    }

    @POST
    @Path("/login")
    public Response login(String jsonBody) {
        try {
            LoginRequest req = gson.fromJson(jsonBody, LoginRequest.class);

            if (req.email == null || req.password == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Email et mot de passe requis\"}").build();
            }

            Optional<User> userOpt = authService.login(req.email, req.password);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // En production, on générerait un vrai Token JWT ici.
                // Pour ce projet, on renvoie une "clé API" simulée (l'ID utilisateur)
                Map<String, Object> response = new HashMap<>();
                response.put("token", "user_" + user.getId());
                response.put("user", user);
                response.put("message", "Connexion réussie");

                return Response.ok(gson.toJson(response)).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Identifiants incorrects\"}").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/register")
    public Response register(String jsonBody) {
        try {
            RegisterRequest req = gson.fromJson(jsonBody, RegisterRequest.class);

            // Validation basique
            if (req.email == null || req.password == null || req.firstName == null || req.lastName == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Tous les champs sont obligatoires\"}").build();
            }

            UserRole role = UserRole.STUDENT;
            if (req.role != null && !req.role.isEmpty()) {
                try {
                    role = UserRole.valueOf(req.role.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Ignorer et garder STUDENT par défaut
                }
            }

            User user = authService.register(req.email, req.password, req.firstName, req.lastName, role);

            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("message", "Inscription réussie");

            return Response.status(Response.Status.CREATED).entity(gson.toJson(response)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
}
