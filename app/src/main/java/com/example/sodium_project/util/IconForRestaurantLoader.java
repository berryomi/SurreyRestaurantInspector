package com.example.sodium_project.util;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.sodium_project.R;
import com.example.sodium_project.sodium_project.model.Restaurant;
/**
 * Class Name: IconForRestaurantLoader
 * Description: This class loads the icon representing each restaurant
 *              (can be the restaurant's logo, or default icon).
 * Input: Restaurant
 * Output: The number associated with the drawable image for the logos
 */

public class IconForRestaurantLoader {

    public int loadImage(Restaurant restaurant) {
        //Images for restaurants
        //Fix all strings to be consistent
        String restaurantNameNoSpace = restaurant.getName().replaceAll(
                "\\s", "");
        String restaurantNameNoCapitals = restaurantNameNoSpace.toLowerCase();
        if (restaurantNameNoCapitals.contains("church'schicken")) {
            return R.drawable.church_chicken;
        } else if (restaurantNameNoCapitals.contains("7-eleven")) {
            return R.drawable.seven_eleven;
        } else if (restaurantNameNoCapitals.contains("panago")) {
            return R.drawable.panago;
        } else if (restaurantNameNoCapitals.contains("pizzahut")) {
            return R.drawable.pizzahut;
        } else if (restaurantNameNoCapitals.contains("starbuckscoffee")) {
            return R.drawable.starbucks;
        } else if (restaurantNameNoCapitals.contains("subway")) {
            return R.drawable.subway;
        } else if (restaurantNameNoCapitals.contains("burgerking")) {
            return R.drawable.burger_king;
        } else if (restaurantNameNoCapitals.contains("timhortons")) {
            return R.drawable.timhortons;
        } else if (restaurantNameNoCapitals.contains("mcdonald's")) {
            return R.drawable.mcdonalds;
        } else if (restaurantNameNoCapitals.contains("dairyqueen")) {
            return R.drawable.dairy_queen;
        } else {
            return R.drawable.restaurant_picture;
        }
    }
}
