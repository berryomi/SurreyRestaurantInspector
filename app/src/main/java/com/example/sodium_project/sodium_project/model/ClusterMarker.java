package com.example.sodium_project.sodium_project.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Class Name: ClusterMarker
 *
 * Description: A class that stores information about a map marker object.
 *
 */

public class ClusterMarker implements ClusterItem {
    private LatLng position;
    private String restaurantName;
    private String snippet;
    private int hazardLevelPicture;
    private Restaurant restaurant;

    public ClusterMarker(LatLng position, String restaurantName, String snippet,
                         int hazardLevelPicture, Restaurant restaurant) {
        this.position = position;
        this.restaurantName = restaurantName;
        this.snippet = snippet;
        this.hazardLevelPicture = hazardLevelPicture;
        this.restaurant = restaurant;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return restaurantName;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public int getHazardLevelPicture() {
        return hazardLevelPicture;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public String getHazardLevel() {
        Inspection inspection = restaurant.getLatestInspection(restaurant.getTrackingNumber());
        if(inspection == null)
            return null;
        return inspection.getHazardRating();
    }

    public String getRestaurantTrackingID() {
        return restaurant.getTrackingNumber();
    }
}
