package com.uemf.yalah.dto;

import com.uemf.yalah.model.Ride;

/**
 * DTO for ride search results with proximity information
 */
public class RideSearchResult {
    private Ride ride;
    private Double departureProximityKm; // Distance from user's departure to ride's departure
    private Double arrivalProximityKm; // Distance from user's arrival to ride's arrival
    private Double relevanceScore; // Combined score for sorting

    public RideSearchResult(Ride ride) {
        this.ride = ride;
    }

    public RideSearchResult(Ride ride, Double departureProximityKm, Double arrivalProximityKm) {
        this.ride = ride;
        this.departureProximityKm = departureProximityKm;
        this.arrivalProximityKm = arrivalProximityKm;
        // Relevance score: lower is better (sum of distances)
        if (departureProximityKm != null && arrivalProximityKm != null) {
            this.relevanceScore = departureProximityKm + arrivalProximityKm;
        } else if (departureProximityKm != null) {
            this.relevanceScore = departureProximityKm * 2; // Only departure match
        } else if (arrivalProximityKm != null) {
            this.relevanceScore = arrivalProximityKm * 2; // Only arrival match
        } else {
            this.relevanceScore = 1000.0; // No proximity data
        }
    }

    /**
     * Calculate distance between two GPS points using Haversine formula
     * 
     * @return distance in kilometers
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Earth radius in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    // Formatted proximity text for display
    public String getDepartureProximityText() {
        if (departureProximityKm == null)
            return null;
        if (departureProximityKm < 1) {
            return String.format("%.0f m", departureProximityKm * 1000);
        }
        return String.format("%.1f km", departureProximityKm);
    }

    public String getArrivalProximityText() {
        if (arrivalProximityKm == null)
            return null;
        if (arrivalProximityKm < 1) {
            return String.format("%.0f m", arrivalProximityKm * 1000);
        }
        return String.format("%.1f km", arrivalProximityKm);
    }

    // Proximity level for UI styling
    public String getDepartureProximityLevel() {
        if (departureProximityKm == null)
            return "unknown";
        if (departureProximityKm <= 0.5)
            return "exact"; // < 500m = exact match
        if (departureProximityKm <= 2.0)
            return "close"; // < 2km = close
        if (departureProximityKm <= 5.0)
            return "nearby"; // < 5km = nearby
        return "far"; // > 5km = far
    }

    public String getArrivalProximityLevel() {
        if (arrivalProximityKm == null)
            return "unknown";
        if (arrivalProximityKm <= 0.5)
            return "exact";
        if (arrivalProximityKm <= 2.0)
            return "close";
        if (arrivalProximityKm <= 5.0)
            return "nearby";
        return "far";
    }

    // Getters and Setters
    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public Double getDepartureProximityKm() {
        return departureProximityKm;
    }

    public void setDepartureProximityKm(Double departureProximityKm) {
        this.departureProximityKm = departureProximityKm;
    }

    public Double getArrivalProximityKm() {
        return arrivalProximityKm;
    }

    public void setArrivalProximityKm(Double arrivalProximityKm) {
        this.arrivalProximityKm = arrivalProximityKm;
    }

    public Double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(Double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }
}
