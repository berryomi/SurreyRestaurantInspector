package com.example.sodium_project.sodium_project.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class Name: RestaurantManager
 *
 * Description: A class that stores and manages a list of all restaurants.
 *
 */

public class RestaurantManager {
    private List<Restaurant> restaurantsList = new ArrayList<>();

    // Singleton Support Code starts here

    private static RestaurantManager instance;

    // Default Constructor
    private RestaurantManager() {
        // private and do nothing to ensure this is a singleton
    }

    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }
        return instance;
    }

    // Function added by Henry, need to access this list in order to populate listView
    public List<Restaurant> getRestaurantList(){
        return restaurantsList;
    }

    // Normal Object Code starts here

    public void addRestaurant(Restaurant aRestaurant) {
        restaurantsList.add(aRestaurant);
    }

    public Restaurant retrieveRestaurant(int position) {
        return restaurantsList.get(position);
    }

    public void resetAllRestaurants() {
        restaurantsList.clear();
    }

    public int getRestaurantListSize() {
        return restaurantsList.size();
    }

}