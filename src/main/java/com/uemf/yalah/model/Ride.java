package com.uemf.yalah.model;

import com.uemf.yalah.model.enums.RideStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un trajet proposé par un conducteur
 */
@Entity
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // Localisation départ
    @NotBlank(message = "Le lieu de départ est obligatoire")
    @Column(name = "departure_location", nullable = false)
    private String departureLocation;

    @Column(name = "departure_lat")
    private Double departureLat;

    @Column(name = "departure_lng")
    private Double departureLng;

    // Localisation arrivée
    @NotBlank(message = "Le lieu d'arrivée est obligatoire")
    @Column(name = "arrival_location", nullable = false)
    private String arrivalLocation;

    @Column(name = "arrival_lat")
    private Double arrivalLat;

    @Column(name = "arrival_lng")
    private Double arrivalLng;

    // Horaires
    // Note: @Future removed to allow completing past rides
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "estimated_arrival_time")
    private LocalDateTime estimatedArrivalTime;

    // Capacité et prix
    @Positive(message = "Le nombre de places doit être positif")
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Positive(message = "Le prix doit être positif")
    @Column(name = "price_per_seat", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerSeat;

    // Distance et durée (calculés par GraphHopper)
    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // Préférences du trajet
    @Column(name = "allows_baggage")
    private Boolean allowsBaggage = true;

    @Column(name = "allows_pets")
    private Boolean allowsPets = false;

    @Column(name = "allows_detours")
    private Boolean allowsDetours = false;

    @Column(name = "instant_booking")
    private Boolean instantBooking = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Statut
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status = RideStatus.SCHEDULED;

    // Récurrence
    @Column(name = "is_recurring")
    private Boolean isRecurring = false;

    @Column(name = "recurring_days")
    private String recurringDays; // ex: "MON,TUE,WED"

    // Flag pour éviter les notifications de trajet terminé en double
    @Column(name = "completion_notified")
    private Boolean completionNotified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relations
    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (totalSeats == null) {
            totalSeats = availableSeats;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructeurs
    public Ride() {
    }

    public Ride(User driver, Vehicle vehicle, String departureLocation, String arrivalLocation,
            LocalDateTime departureTime, Integer availableSeats, BigDecimal pricePerSeat) {
        this.driver = driver;
        this.vehicle = vehicle;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.totalSeats = availableSeats;
        this.pricePerSeat = pricePerSeat;
    }

    // Méthodes utilitaires
    public int getBookedSeats() {
        return totalSeats - availableSeats;
    }

    public boolean hasAvailableSeats() {
        return availableSeats > 0;
    }

    public boolean canBook(int requestedSeats) {
        return availableSeats >= requestedSeats && status == RideStatus.SCHEDULED;
    }

    public void bookSeats(int seats) {
        if (canBook(seats)) {
            availableSeats -= seats;
        }
    }

    public void releaseSeats(int seats) {
        availableSeats = Math.min(availableSeats + seats, totalSeats);
    }

    public boolean isPast() {
        return departureTime.isBefore(LocalDateTime.now());
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public Double getDepartureLat() {
        return departureLat;
    }

    public void setDepartureLat(Double departureLat) {
        this.departureLat = departureLat;
    }

    public Double getDepartureLng() {
        return departureLng;
    }

    public void setDepartureLng(Double departureLng) {
        this.departureLng = departureLng;
    }

    public String getArrivalLocation() {
        return arrivalLocation;
    }

    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }

    public Double getArrivalLat() {
        return arrivalLat;
    }

    public void setArrivalLat(Double arrivalLat) {
        this.arrivalLat = arrivalLat;
    }

    public Double getArrivalLng() {
        return arrivalLng;
    }

    public void setArrivalLng(Double arrivalLng) {
        this.arrivalLng = arrivalLng;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public BigDecimal getPricePerSeat() {
        return pricePerSeat;
    }

    public void setPricePerSeat(BigDecimal pricePerSeat) {
        this.pricePerSeat = pricePerSeat;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Boolean getAllowsBaggage() {
        return allowsBaggage;
    }

    public void setAllowsBaggage(Boolean allowsBaggage) {
        this.allowsBaggage = allowsBaggage;
    }

    public Boolean getAllowsPets() {
        return allowsPets;
    }

    public void setAllowsPets(Boolean allowsPets) {
        this.allowsPets = allowsPets;
    }

    public Boolean getAllowsDetours() {
        return allowsDetours;
    }

    public void setAllowsDetours(Boolean allowsDetours) {
        this.allowsDetours = allowsDetours;
    }

    public Boolean getInstantBooking() {
        return instantBooking;
    }

    public void setInstantBooking(Boolean instantBooking) {
        this.instantBooking = instantBooking;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public String getRecurringDays() {
        return recurringDays;
    }

    public void setRecurringDays(String recurringDays) {
        this.recurringDays = recurringDays;
    }

    public Boolean getCompletionNotified() {
        return completionNotified;
    }

    public void setCompletionNotified(Boolean completionNotified) {
        this.completionNotified = completionNotified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
