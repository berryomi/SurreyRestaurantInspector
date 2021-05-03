package com.example.sodium_project.sodium_project.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Name: SearchFilterManager
 *
 * Description: A class that manages the list of restaurants filtered by users' preferences.
 *              This class is a singleton.
 *
 */

public class SearchFilterManager {
    private List<Restaurant> restaurants = new ArrayList<>();
    public static String inputNameOfRestaurant;
    public static String inputHazardLevel;     //Low,Moderate or high
    public static int inputLessThanCriticalViolations;
    public static int inputGreaterThanCriticalViolations;
    public static boolean isInputFavourites;


    //Singleton support
    private static SearchFilterManager instance;

    private SearchFilterManager() {
        // private and do nothing to ensure this is a singleton
    }

    public static SearchFilterManager getInstance() {
        if (instance == null) {
            inputNameOfRestaurant = "-1";
            inputHazardLevel = "-1";
            inputLessThanCriticalViolations = -1;
            inputGreaterThanCriticalViolations = -1;
            isInputFavourites = false;
            instance = new SearchFilterManager();
        }
        return instance;
    }

    public List<Restaurant> getRestaurantList(){
        return restaurants;
    }

    public void addRestaurant(Restaurant Restaurant) {
        restaurants.add(Restaurant);
    }

    public Restaurant retrieveRestaurant(int position) {
        return restaurants.get(position);
    }

    public int getRestaurantListSize() {
        return restaurants.size();
    }

    public void clearFilterList(){
        restaurants.clear();
    }

}
