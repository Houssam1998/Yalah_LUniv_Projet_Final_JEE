package com.uemf.yalah.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uemf.yalah.dto.RideDTO; // Add import
import com.uemf.yalah.model.Ride;
import com.uemf.yalah.model.User;
import com.uemf.yalah.model.Vehicle;
import com.uemf.yalah.service.AuthService;
import com.uemf.yalah.service.RideService;
import com.uemf.yalah.dto.RideSearchResult;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * API REST pour la gestion des trajets
 */
@Path("/rides")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RideResource {

    private RideService rideService = new RideService();
    private AuthService authService = new AuthService();

    public RideResource() {
        // Constructeur par défaut requis par JAX-RS
    }

    // Adapter Gson pour gérer LocalDateTime
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc,
                    context) -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> LocalDateTime
                            .parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .create();

    /**
     * DTO pour la création de trajet
     */
    public static class CreateRideRequest {
        public String departureLocation;
        public String arrivalLocation;
        public String departureTime; // Format ISO: 2023-12-25T14:30:00
        public int availableSeats;
        public double pricePerSeat;
        public Long vehicleId;
        public Double departureLat;
        public Double departureLng;
        public Double arrivalLat;
        public Double arrivalLng;
    }

    @GET
    @Path("/search")
    public Response search(
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("date") String dateStr,
            @QueryParam("seats") @DefaultValue("1") int seats) {

        try {
            LocalDateTime date = null;
            if (dateStr != null && !dateStr.isEmpty()) {
                date = LocalDateTime.parse(dateStr); // Attend format ISO simple ou complet
            }

            List<Ride> rides = rideService.searchRides(from, to, date, seats, null);

            // Convertir en DTO pour éviter les boucles infinies ou lazy loading exceptions
            List<RideSearchResult> results = rides.stream()
                    .map(RideSearchResult::new)
                    .collect(Collectors.toList());

            return Response.ok(gson.toJson(results)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/my-rides")
    public Response getMyRides(@HeaderParam("Authorization") String token) {
        // Authentification simplifiée (simulée)
        Long userId = parseToken(token);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            List<Ride> rides = rideService.findByDriver(userId);
            return Response.ok(gson.toJson(rides)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    public Response createRide(@HeaderParam("Authorization") String token, String jsonBody) {
        Long userId = parseToken(token);
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            CreateRideRequest req = gson.fromJson(jsonBody, CreateRideRequest.class);

            Optional<User> userOpt = authService.findById(userId);
            if (userOpt.isEmpty())
                return Response.status(Response.Status.UNAUTHORIZED).build();

            Optional<Vehicle> vehicleOpt = rideService.findVehicleById(req.vehicleId);
            if (vehicleOpt.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Véhicule introuvable\"}")
                        .build();
            }

            Ride ride = rideService.createRide(
                    userOpt.get(),
                    vehicleOpt.get(),
                    req.departureLocation,
                    req.arrivalLocation,
                    LocalDateTime.parse(req.departureTime),
                    req.availableSeats,
                    BigDecimal.valueOf(req.pricePerSeat));

            // Coordonnées
            if (req.departureLat != null) {
                rideService.setCoordinates(ride, req.departureLat, req.departureLng, req.arrivalLat, req.arrivalLng);
            }

            return Response.status(Response.Status.CREATED).entity(gson.toJson(ride)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    public Response getAllRides() {
        try {
            // Pour usage Test/Admin : liste tout
            List<Ride> rides = rideService.findAllWithRelations();
            // Utiliser RideDTO pour éviter les problèmes de proxies Hibernate
            List<RideDTO> results = rides.stream()
                    .map(RideDTO::new)
                    .collect(Collectors.toList());
            return Response.ok(gson.toJson(results)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Failed to retrieve rides: " + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "RideResource is working!";
    }

    // Helper pour extraire l'ID du token "user_123"
    private Long parseToken(String token) {
        if (token == null || !token.startsWith("user_"))
            return null;
        try {
            return Long.parseLong(token.substring(5));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
