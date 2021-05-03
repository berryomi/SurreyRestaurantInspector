package com.example.sodium_project.sodium_project.model;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Name: FavouritesManager
 *
 * Description: A class that manages the list of favourite restaurants (added by users).
 *
 */

public class FavouritesManager extends AppCompatActivity {
    private List<String> favouritesList = new ArrayList<>();  // list of restaurant tracking numbers that are marked as favourite
    private List<Restaurant> favouriteRestaurantList = new ArrayList<>(); // list of restaurants marked as favourite

    private RestaurantManager restaurantManager = RestaurantManager.getInstance();

    private static FavouritesManager instance;

    // Default Constructor
    private FavouritesManager() {
        // private and do nothing to ensure this is a singleton
    }

    public static FavouritesManager getInstance() {
        if (instance == null) {
            instance = new FavouritesManager();
        }
        return instance;
    }

    public List<String> getFavouritesList() {
        return favouritesList;
    }

    public List<Restaurant> getFavouriteRestaurantList() {
        return favouriteRestaurantList;
    }

    public Restaurant retrieveFavouriteRestaurant(int position) {
        return favouriteRestaurantList.get(position);
    }

    public void initializeFavouriteRestaurantList() {
        if (favouriteRestaurantList == null) {
            favouriteRestaurantList = new ArrayList<>();
        }
        favouriteRestaurantList.clear();
        for (Restaurant restaurant : restaurantManager.getRestaurantList()) {
            if (isFavouriteRestaurant(restaurant.getTrackingNumber())) {
                favouriteRestaurantList.add(restaurant);
            }
        }
    }

    public int getFavouriteListSize() {
        if (favouritesList == null) {
            return 0;
        }
        return favouritesList.size();
    }

    public void setFavouritesList(List<String> list) {
        favouritesList = list;
    }

    public void addRestaurantToFavourites(String aRestaurant) {
        if (favouritesList == null) {
            favouritesList = new ArrayList<>();
        }
        favouritesList.add(aRestaurant);
    }

    public void removeRestaurantsFromFavourites(String aRestaurant) {
        if (favouritesList == null) {
            return;
        }
        int i = 0;
        for (String restaurant: favouritesList) {
            String[] line = restaurant.split(",");
            if (line[0].equals(aRestaurant)) {
                favouritesList.remove(i);
                return;
            }
            i++;
        }
    }

    // Method to check if a specific restaurant is marked as favourite
    // Receive the restaurant's tracking number as parameter
    public boolean isFavouriteRestaurant(String aRestaurant) {
        if (favouritesList == null) {
            return false;
        }
        for (String restaurant: favouritesList) {
            String[] line = restaurant.split(",");
            if (line[0].equals(aRestaurant)) {
                return true;
            }
        }
        return false;
    }

}
