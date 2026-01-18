package com.uemf.yalah.service;

import com.uemf.yalah.dao.RideDAO;
import com.uemf.yalah.dao.BookingDAO;
import com.uemf.yalah.dao.VehicleDAO;
import com.uemf.yalah.model.Ride;
import com.uemf.yalah.model.User;
import com.uemf.yalah.model.Vehicle;
import com.uemf.yalah.model.enums.RideStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des trajets
 */
public class RideService {

    private final RideDAO rideDAO;
    private final BookingDAO bookingDAO;
    private final VehicleDAO vehicleDAO;

    public RideService() {
        this.rideDAO = new RideDAO();
        this.bookingDAO = new BookingDAO();
        this.vehicleDAO = new VehicleDAO();
    }

    /**
     * Crée un nouveau trajet
     */
    public Ride createRide(User driver, Vehicle vehicle, String departureLocation,
            String arrivalLocation, LocalDateTime departureTime,
            int availableSeats, BigDecimal pricePerSeat) throws Exception {

        // Validations
        if (departureTime.isBefore(LocalDateTime.now())) {
            throw new Exception("La date de départ doit être dans le futur");
        }

        if (availableSeats <= 0 || availableSeats > vehicle.getSeats()) {
            throw new Exception("Nombre de places invalide");
        }

        Ride ride = new Ride(driver, vehicle, departureLocation, arrivalLocation,
                departureTime, availableSeats, pricePerSeat);

        return rideDAO.save(ride);
    }

    /**
     * Met à jour un trajet existant
     */
    public Ride updateRide(Ride ride) {
        return rideDAO.update(ride);
    }

    /**
     * Met à jour les coordonnées GPS d'un trajet
     */
    public Ride setCoordinates(Ride ride, Double depLat, Double depLng,
            Double arrLat, Double arrLng) {
        ride.setDepartureLat(depLat);
        ride.setDepartureLng(depLng);
        ride.setArrivalLat(arrLat);
        ride.setArrivalLng(arrLng);
        return rideDAO.update(ride);
    }

    /**
     * Met à jour la distance et durée estimées
     */
    public Ride setDistanceAndDuration(Ride ride, Double distanceKm, Integer durationMinutes) {
        ride.setDistanceKm(distanceKm);
        ride.setDurationMinutes(durationMinutes);

        // Calculer l'heure d'arrivée estimée
        if (durationMinutes != null) {
            ride.setEstimatedArrivalTime(ride.getDepartureTime().plusMinutes(durationMinutes));
        }

        return rideDAO.update(ride);
    }

    /**
     * Recherche de trajets
     */
    public List<Ride> searchRides(String departure, String arrival, LocalDateTime date,
            int minSeats, Double maxPrice) {
        return rideDAO.searchRides(departure, arrival, date,
                minSeats > 0 ? minSeats : 1, maxPrice);
    }

    /**
     * Recherche tous les trajets avec coordonnées pour filtrage par proximité
     */
    public List<Ride> findAvailableRidesWithCoordinates(LocalDateTime date, int minSeats, Double maxPrice) {
        return rideDAO.findAvailableRidesWithCoordinates(date, minSeats > 0 ? minSeats : 1, maxPrice);
    }

    /**
     * Trouve les trajets d'un conducteur
     */
    public List<Ride> findByDriver(Long driverId) {
        return rideDAO.findByDriver(driverId);
    }

    /**
     * Trouve les trajets à venir d'un conducteur
     */
    public List<Ride> findUpcomingByDriver(Long driverId) {
        return rideDAO.findUpcomingByDriver(driverId);
    }

    /**
     * Trouve les trajets récents pour la page d'accueil
     */
    public List<Ride> findRecentRides(int limit) {
        return rideDAO.findRecentRides(limit);
    }

    /**
     * Trouve les trajets vers/depuis le campus
     */
    public List<Ride> findCampusRides(int limit) {
        return rideDAO.findRidesToCampus(limit);
    }

    /**
     * Trouve un trajet par ID
     */
    public Optional<Ride> findById(Long rideId) {
        return rideDAO.findById(rideId);
    }

    /**
     * Trouve un trajet par ID avec ses relations chargées (driver, vehicle)
     */
    public Optional<Ride> findByIdWithRelations(Long rideId) {
        return rideDAO.findByIdWithRelations(rideId);
    }

    /**
     * Trouve tous les trajets (pour API/Admin)
     */
    public List<Ride> findAll() {
        return rideDAO.findAll();
    }

    /**
     * Trouve tous les trajets avec relations (pour API DTO)
     */
    public List<Ride> findAllWithRelations() {
        return rideDAO.findAllWithRelations();
    }

    /**
     * Annule un trajet
     */
    public void cancelRide(Ride ride) {
        ride.setStatus(RideStatus.CANCELLED);
        rideDAO.update(ride);

        // TODO: Notifier les passagers et annuler les réservations
    }

    /**
     * Démarre un trajet
     */
    public void startRide(Ride ride) {
        ride.setStatus(RideStatus.IN_PROGRESS);
        rideDAO.update(ride);
    }

    /**
     * Termine un trajet
     */
    public void completeRide(Ride ride) {
        ride.setStatus(RideStatus.COMPLETED);
        rideDAO.update(ride);

        // TODO: Marquer les réservations comme terminées
    }

    /**
     * Statistiques
     */
    public long countActiveRides() {
        return rideDAO.countActiveRides();
    }

    /**
     * Trouve les véhicules d'un propriétaire
     */
    public List<Vehicle> findVehiclesByOwner(Long ownerId) {
        return vehicleDAO.findByOwner(ownerId);
    }

    /**
     * Trouve un véhicule par ID avec son propriétaire
     */
    public java.util.Optional<Vehicle> findVehicleById(Long vehicleId) {
        return vehicleDAO.findByIdWithOwner(vehicleId);
    }
}
