package com.uemf.yalah.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un véhicule d'un conducteur
 */
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @NotBlank(message = "La marque est obligatoire")
    @Column(nullable = false)
    private String brand;

    @NotBlank(message = "Le modèle est obligatoire")
    @Column(nullable = false)
    private String model;

    @Column(length = 50)
    private String color;

    @NotBlank(message = "La plaque d'immatriculation est obligatoire")
    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @Positive(message = "Le nombre de places doit être positif")
    @Column(nullable = false)
    private Integer seats = 4;

    private String photo;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relations
    @OneToMany(mappedBy = "vehicle")
    private List<Ride> rides = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructeurs
    public Vehicle() {
    }

    public Vehicle(User owner, String brand, String model, String licensePlate, Integer seats) {
        this.owner = owner;
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.seats = seats;
    }

    // Méthodes utilitaires
    public String getFullDescription() {
        return brand + " " + model + (color != null ? " (" + color + ")" : "");
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Ride> getRides() {
        return rides;
    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }
}
