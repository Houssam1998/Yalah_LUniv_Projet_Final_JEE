package com.uemf.yalah.model;

import com.uemf.yalah.model.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un utilisateur (étudiant, professeur, staff)
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.STUDENT;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Float rating = 0.0f;

    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;

    // Préférences
    @Column(name = "pref_music")
    private Boolean prefMusic = true;

    @Column(name = "pref_smoking")
    private Boolean prefSmoking = false;

    @Column(name = "pref_talking")
    private Boolean prefTalking = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relations
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<Ride> ridesAsDriver = new ArrayList<>();

    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructeurs
    public User() {
    }

    public User(String email, String password, String firstName, String lastName, UserRole role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    // Méthodes utilitaires
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isUemfEmail() {
        return email != null && email.endsWith("@ueuromed.org");
    }

    public void updateRating(int newRating) {
        float totalScore = this.rating * this.ratingCount + newRating;
        this.ratingCount++;
        this.rating = totalScore / this.ratingCount;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Boolean getPrefMusic() {
        return prefMusic;
    }

    public void setPrefMusic(Boolean prefMusic) {
        this.prefMusic = prefMusic;
    }

    public Boolean getPrefSmoking() {
        return prefSmoking;
    }

    public void setPrefSmoking(Boolean prefSmoking) {
        this.prefSmoking = prefSmoking;
    }

    public Boolean getPrefTalking() {
        return prefTalking;
    }

    public void setPrefTalking(Boolean prefTalking) {
        this.prefTalking = prefTalking;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public List<Ride> getRidesAsDriver() {
        return ridesAsDriver;
    }

    public void setRidesAsDriver(List<Ride> ridesAsDriver) {
        this.ridesAsDriver = ridesAsDriver;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
