package com.uemf.yalah.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entité représentant une notification in-app pour un utilisateur
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String type; // BOOKING_REQUEST, BOOKING_CONFIRMED, RIDE_REMINDER, etc.

    @Column(name = "related_url")
    private String relatedUrl;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructeurs
    public Notification() {
    }

    public Notification(User user, String title, String message, String type) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
    }

    public Notification(User user, String title, String message, String type, String relatedUrl) {
        this(user, title, message, type);
        this.relatedUrl = relatedUrl;
    }

    // Méthodes utilitaires
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelatedUrl() {
        return relatedUrl;
    }

    public void setRelatedUrl(String relatedUrl) {
        this.relatedUrl = relatedUrl;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
