package com.uemf.yalah.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uemf.yalah.model.Booking;
import com.uemf.yalah.model.Ride;
import com.uemf.yalah.model.User;
import com.uemf.yalah.service.AuthService;
import com.uemf.yalah.service.BookingService;
import com.uemf.yalah.service.RideService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * API REST pour la gestion des réservations
 */
@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource {

    private BookingService bookingService = new BookingService();
    private RideService rideService = new RideService();
    private AuthService authService = new AuthService();

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc,
                    context) -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .create();

    public static class CreateBookingRequest {
        public Long rideId;
        public int seats;
        public String message;
    }

    @GET
    public Response getMyBookings(@HeaderParam("Authorization") String token) {
        Long userId = parseToken(token);
        if (userId == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        List<Booking> bookings = bookingService.findByPassenger(userId);
        return Response.ok(gson.toJson(bookings)).build();
    }

    @POST
    public Response createBooking(@HeaderParam("Authorization") String token, String jsonBody) {
        Long userId = parseToken(token);
        if (userId == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        try {
            CreateBookingRequest req = gson.fromJson(jsonBody, CreateBookingRequest.class);

            Optional<User> userOpt = authService.findById(userId);
            if (userOpt.isEmpty())
                return Response.status(Response.Status.UNAUTHORIZED).build();

            Optional<Ride> rideOpt = rideService.findByIdWithRelations(req.rideId);
            if (rideOpt.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Trajet introuvable\"}")
                        .build();
            }

            Booking booking = bookingService.createBooking(
                    userOpt.get(),
                    rideOpt.get(),
                    req.seats,
                    req.message);

            return Response.status(Response.Status.CREATED).entity(gson.toJson(booking)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

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
