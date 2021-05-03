package com.example.sodium_project.sodium_project.model;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class Name: Restaurant
 *
 * Description: A class that stores information about one restaurant object.
 *
 */

public class Restaurant implements Comparable<Restaurant> {
    private String trackingNumber;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private boolean first = true;

    // Each restaurant is responsible to know how many inspections have been carried out at its place
    private List<Inspection> inspectionsOfRestaurant = new ArrayList<>();
    private InspectionManager inspectionManager = InspectionManager.getInstance();

    // Parameterized Constructor
    public Restaurant(String aTrackingNumber, String aName, String anAddress,
                      double aLatitude, double aLongitude) {
        trackingNumber = aTrackingNumber;
        name = aName;
        address = anAddress;
        latitude = aLatitude;
        longitude = aLongitude;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setName(String aName) {
        name = aName;
    }

    public int getNumberOfRestaurantInspections() {
        return inspectionsOfRestaurant.size();
    }

    public List<Inspection> getInspectionList(String aTrackingNumber) {
        if (first) { // to ensure that adding inspections to list is executed only once
            for (int i = 0; i < inspectionManager.getTotalNumberOfInspection(); i++) {
                Inspection currentInspection = inspectionManager.retrieve(i);
                if (currentInspection.getTrackingNumber().equals(aTrackingNumber)) {
                    inspectionsOfRestaurant.add(currentInspection);
                }
            }
            first = false;
        }

        return inspectionsOfRestaurant;
    }

    public Inspection getLatestInspection(String aTrackingNumber) {
        List<Inspection> inspectionsList = getInspectionList(aTrackingNumber);

        if(inspectionsList.size() == 0)
            return null;

        int latestDate = 0;
        Inspection latestInspection = inspectionsList.get(0);

        for (int i = 0; i < inspectionsList.size(); i++) {
            int date = Integer.parseInt(inspectionsList.get(i).getInspectionDate());
            if (date > latestDate) {
                latestDate = date;
                latestInspection = inspectionsList.get(i);
            }
        }
        return latestInspection;
    }

    @Override
    public String toString() {
        return "Restaurant( " +
                "trackingNumber = " + trackingNumber +
                ", name = " + name +
                ", address = " + address +
                ", coordinates = (" + latitude +
                ", " + longitude + ")" +
                " ).";
    }
    public int compareTo(Restaurant o1){
        return this.getName().compareTo(o1.getName());
    }
}
