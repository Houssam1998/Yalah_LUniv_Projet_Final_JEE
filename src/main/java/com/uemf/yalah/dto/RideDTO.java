package com.uemf.yalah.dto;

import com.uemf.yalah.model.Ride;
import com.uemf.yalah.model.enums.RideStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO simple pour la sérialisation JSON des trajets
 * Évite les problèmes de proxies Hibernate et de récursion
 */
public class RideDTO {
    private Long id;
    private String departureLocation;
    private String arrivalLocation;
    private String departureTime;
    private BigDecimal pricePerSeat;
    private int availableSeats;
    private RideStatus status;
    private DriverDTO driver;
    private VehicleDTO vehicle;

    public RideDTO(Ride ride) {
        this.id = ride.getId();
        this.departureLocation = ride.getDepartureLocation();
        this.arrivalLocation = ride.getArrivalLocation();
        this.departureTime = ride.getDepartureTime().toString();
        this.pricePerSeat = ride.getPricePerSeat();
        this.availableSeats = ride.getAvailableSeats();
        this.status = ride.getStatus();

        if (ride.getDriver() != null) {
            this.driver = new DriverDTO(ride.getDriver().getId(),
                    ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName());
        }

        if (ride.getVehicle() != null) {
            this.vehicle = new VehicleDTO(ride.getVehicle().getBrand(), ride.getVehicle().getModel());
        }
    }

    // Inner DTOs
    public static class DriverDTO {
        public Long id;
        public String name;

        public DriverDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class VehicleDTO {
        public String brand;
        public String model;

        public VehicleDTO(String brand, String model) {
            this.brand = brand;
            this.model = model;
        }
    }
}
