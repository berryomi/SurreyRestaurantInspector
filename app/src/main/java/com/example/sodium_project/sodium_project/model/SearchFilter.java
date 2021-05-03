package com.example.sodium_project.sodium_project.model;

import android.util.Log;

import java.util.Calendar;
import java.util.List;

/**
 * Class Name: SearchFilter
 *
 * Description: A class that filters the restaurants list based on users' preferences.
 *              It can filter the restaurants list by name, by recent inspections with specific
 *              hazard level, by number of critical violations, and by users' favourite restaurants.
 *
 */

// Change to singleton
public class SearchFilter {
    private RestaurantManager restaurantManager = RestaurantManager.getInstance();
    private FavouritesManager favouritesManager = FavouritesManager.getInstance();
    private SearchFilterManager filteredName = SearchFilterManager.getInstance();

    private String inputNameOfRestaurant = SearchFilterManager.inputNameOfRestaurant;
    private String inputHazardLevel = SearchFilterManager.inputHazardLevel;     //Low,Moderate or high
    private int inputLessThanCriticalViolations = SearchFilterManager.inputLessThanCriticalViolations;
    private int inputGreaterThanCriticalViolations = SearchFilterManager.inputGreaterThanCriticalViolations;
    private boolean isInputFavourites = SearchFilterManager.isInputFavourites;


    public SearchFilter(){
        filteredName.clearFilterList();
        addAllRestaurantsToList();
        filterByName();
        filterByRecentInspection();
        filterByLessThanNumberCriticalViolations();
        filterByGreaterThanNumberCriticalViolations();
        filterByFavourite();
    }

    private String consistentStrings(String name){
        name = name.replaceAll("\\s", "");
        name = name.toLowerCase();
        return name;
    }

    private void addAllRestaurantsToList(){
        for(int size = 0; size < restaurantManager.getRestaurantListSize();size++){
            filteredName.addRestaurant(restaurantManager.getRestaurantList().get(size));
        }
    }

    private void filterByName(){
        if(inputNameOfRestaurant.equals("-1")){
            return;
        }
        //Make strings consistent
        inputNameOfRestaurant = consistentStrings(inputNameOfRestaurant);

        for(int i = 0; i < filteredName.getRestaurantListSize();){
            String restaurantName = filteredName.getRestaurantList().get(i).getName();
            restaurantName = consistentStrings(restaurantName);
            if(!restaurantName.contains(inputNameOfRestaurant)){
                filteredName.getRestaurantList().remove(i);
            }else{
                i++;
            }
        }
    }
    private void filterByRecentInspection(){
        if(inputHazardLevel.equals("-1")){
            return;
        }
        for(int i = 0; i < filteredName.getRestaurantListSize();){
            Restaurant restaurant = filteredName.getRestaurantList().get(i);
            Inspection inspection = restaurant.getLatestInspection(restaurant.getTrackingNumber());
            if(inspection != null) {

                String hazardLevel = inspection.getHazardRating();
                if (!hazardLevel.equals(inputHazardLevel)) {
                    filteredName.getRestaurantList().remove(i);
                } else {
                    i++;
                }
            }else{  //Info about inspection is unavailable (N/A)
                filteredName.getRestaurantList().remove(i);
            }
        }

    }
    //Check for number of critical violations within the last year
    private int criticalViolationsWithinLastYear(Inspection inspection){
        int numberCriticalViolations = 0;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month  = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if(inspection.getInspectionYear() == year-1){   //In 2019(Currently 2020)
            if(inspection.getInspectionMonth() > month) {
                numberCriticalViolations = inspection.getNumCrit();
            }
            if(inspection.getInspectionMonth() == month){
                if(inspection.getInspectionDay() >= day) {
                    numberCriticalViolations = inspection.getNumCrit();
                }
            }
        }
        if(inspection.getInspectionYear() == year){ //If in 2020
            numberCriticalViolations = inspection.getNumCrit();
        }
        return numberCriticalViolations;
    }

    //Less than N critical violations within the last year
    private void filterByLessThanNumberCriticalViolations(){
        if(inputLessThanCriticalViolations == -1){
            return;
        }
        int numberCriticalViolationsInYear;
        for(int names = 0; names < filteredName.getRestaurantListSize();){
            numberCriticalViolationsInYear = 0;
            Restaurant restaurant = filteredName.getRestaurantList().get(names);
            List<Inspection> inspections = restaurant.getInspectionList(restaurant.getTrackingNumber());
            for(int inspectionSize = 0 ; inspectionSize < inspections.size(); inspectionSize++){
                numberCriticalViolationsInYear += criticalViolationsWithinLastYear(inspections.get(inspectionSize));
            }
            //Remove any restaurants that have more than N violations
            if(numberCriticalViolationsInYear > inputLessThanCriticalViolations){
                filteredName.getRestaurantList().remove(names);
            }else{
                names++;
            }
        }
    }
    //Greater than N critical violations within the last year
    private void filterByGreaterThanNumberCriticalViolations(){
        if(inputGreaterThanCriticalViolations == -1){
            return;
        }
        int numberCriticalInspectionsInYear;
        for(int names = 0; names < filteredName.getRestaurantListSize();){
            numberCriticalInspectionsInYear = 0;
            Restaurant restaurant = filteredName.getRestaurantList().get(names);
            List<Inspection> inspections = restaurant.getInspectionList(restaurant.getTrackingNumber());
            for(int inspectionSize = 0 ; inspectionSize < inspections.size(); inspectionSize++){
                numberCriticalInspectionsInYear += criticalViolationsWithinLastYear(inspections.get(inspectionSize));
            }
            //Remove any restaurants that have less than N violations
            if(numberCriticalInspectionsInYear < inputGreaterThanCriticalViolations){
                filteredName.getRestaurantList().remove(names);
            }else{
                names++;
            }
        }
    }



    private void filterByFavourite(){
        boolean isRestaurantSame = false;
        if(!isInputFavourites){ //Favourites mode unselected
            return;
        }

        for(int names = 0 ;names < filteredName.getRestaurantListSize();){
            Restaurant filteredRestaurant = filteredName.retrieveRestaurant(names);
            for(int favourites = 0 ; favourites < favouritesManager.getFavouriteRestaurantList().size(); favourites++){
                Restaurant favouriteRestaurant = favouritesManager.retrieveFavouriteRestaurant(favourites);
                String filteredRestaurantID = filteredRestaurant.getTrackingNumber();
                String favouriteRestaurantID = favouriteRestaurant.getTrackingNumber();
                if(filteredRestaurantID.equals(favouriteRestaurantID)){
                    isRestaurantSame = true;
                }
            }
            if(!isRestaurantSame){
                filteredName.getRestaurantList().remove(names);
            }else{
                names++;
            }
            isRestaurantSame = false;
        }


    }

    public String getInputNameOfRestaurant() {
        return inputNameOfRestaurant;
    }

    public void setInputNameOfRestaurant(String inputNameOfRestaurant) {
        this.inputNameOfRestaurant = inputNameOfRestaurant;
        SearchFilterManager.inputNameOfRestaurant = inputNameOfRestaurant;
        filteredName.clearFilterList();
        addAllRestaurantsToList();
        filterByName();
        filterByRecentInspection();
        filterByLessThanNumberCriticalViolations();
        filterByGreaterThanNumberCriticalViolations();

        filterByFavourite();
    }

    public String getInputHazardLevel() {
        return inputHazardLevel;
    }

    public void setInputHazardLevel(String inputHazardLevel) {
        this.inputHazardLevel = inputHazardLevel;
        SearchFilterManager.inputHazardLevel = inputHazardLevel;
        filteredName.clearFilterList();
        addAllRestaurantsToList();
        filterByName();
        filterByRecentInspection();
        filterByLessThanNumberCriticalViolations();
        filterByGreaterThanNumberCriticalViolations();

        filterByFavourite();
    }

    public int getInputLessThanCriticalViolations() {
        return inputLessThanCriticalViolations;
    }

    public void setInputLessThanCriticalViolations(int inputLessThanCriticalViolations) {
        this.inputLessThanCriticalViolations = inputLessThanCriticalViolations;
        SearchFilterManager.inputLessThanCriticalViolations = inputLessThanCriticalViolations;
        filteredName.clearFilterList();
        addAllRestaurantsToList();
        filterByName();
        filterByRecentInspection();
        filterByLessThanNumberCriticalViolations();
        filterByGreaterThanNumberCriticalViolations();

        filterByFavourite();
    }

    public int getInputGreaterThanCriticalViolations() {
        return inputGreaterThanCriticalViolations;
    }

    public void setInputGreaterThanCriticalViolations(int inputGreaterThanCriticalViolations) {
        this.inputGreaterThanCriticalViolations = inputGreaterThanCriticalViolations;
        SearchFilterManager.inputGreaterThanCriticalViolations = inputGreaterThanCriticalViolations;
        filteredName.clearFilterList();
        addAllRestaurantsToList();
        filterByName();
        filterByRecentInspection();
        filterByLessThanNumberCriticalViolations();
        filterByGreaterThanNumberCriticalViolations();

        filterByFavourite();
    }

    public void setInputFavourites(boolean inputFavourites) {
        isInputFavourites = inputFavourites;
        SearchFilterManager.isInputFavourites = inputFavourites;
        filteredName.clearFilterList();
        addAllRestaurantsToList();
        filterByName();
        filterByRecentInspection();
        filterByLessThanNumberCriticalViolations();
        filterByGreaterThanNumberCriticalViolations();

        filterByFavourite();

    }
}
