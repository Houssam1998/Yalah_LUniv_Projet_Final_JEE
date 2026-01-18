package com.uemf.yalah.model;

import com.uemf.yalah.model.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * Entité représentant une réservation de place(s) sur un trajet
 */
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @Positive(message = "Le nombre de places doit être positif")
    @Column(name = "seats_booked", nullable = false)
    private Integer seatsBooked = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String message; // Message du passager au conducteur

    @Column(name = "booked_at", nullable = false, updatable = false)
    private LocalDateTime bookedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @PrePersist
    protected void onCreate() {
        bookedAt = LocalDateTime.now();
    }

    // Constructeurs
    public Booking() {
    }

    public Booking(Ride ride, User passenger, Integer seatsBooked) {
        this.ride = ride;
        this.passenger = passenger;
        this.seatsBooked = seatsBooked;
    }

    // Méthodes utilitaires
    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = BookingStatus.REJECTED;
        this.ride.releaseSeats(this.seatsBooked);
    }

    public void cancel(String reason) {
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
        this.ride.releaseSeats(this.seatsBooked);
    }

    public void complete() {
        this.status = BookingStatus.COMPLETED;
    }

    public boolean isPending() {
        return status == BookingStatus.PENDING;
    }

    public boolean isConfirmed() {
        return status == BookingStatus.CONFIRMED;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public Integer getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(Integer seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}
