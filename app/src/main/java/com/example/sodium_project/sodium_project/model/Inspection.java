package com.example.sodium_project.sodium_project.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class Name: Inspection
 *
 * Description: A class that stores information about one inspection object.
 *
 */

public class Inspection implements Serializable {   //Needed to store values for inspectionActivity
    private String trackingNumber;
    private String inspectionDate;
    private int inspectionYear;
    private int inspectionMonth;
    private int inspectionDay;
    private String inspectionType;
    private int numCrit;
    private int numNonCrit;
    private String hazardRating;
    private String violLump; // whole CSV line of violLump

    private List<String> violations = new ArrayList<>(); // has the string of violations for each inspection
    private List<Integer> violationCode = new ArrayList<>(); // has the violation codes for each inspection

    public Inspection(String trackingNumber, String inspectionDate, int inspectionYear, int inspectionMonth,
                      int inspectionDay, String inspectionType, int numCrit, int numNonCrit,
                      String hazardRating, String violLump, List<String> violations, List<Integer> violationCode) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.inspectionYear = inspectionYear;
        this.inspectionMonth = inspectionMonth;
        this.inspectionDay = inspectionDay;
        this.inspectionType = inspectionType;
        this.numCrit = numCrit;
        this.numNonCrit = numNonCrit;
        this.hazardRating = hazardRating;
        this.violLump = violLump;
        this.violations = violations;
        this.violationCode = violationCode;
    }

    // Getters
    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public int getInspectionYear() {
        return inspectionYear;
    }

    public int getInspectionMonth() {
        return inspectionMonth;
    }

    public int getInspectionDay() {
        return inspectionDay;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public int getNumCrit() {
        return numCrit;
    }

    public int getNumNonCrit() {
        return numNonCrit;
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public String getViolLump() {
        return violLump;
    }

    public List<Integer> getViolationCode() {
        return violationCode;
    }

    @Override
    public String toString() {
        return "Inspection{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", inspectionDate=" + inspectionDate +
                ", inspectionType='" + inspectionType + '\'' +
                ", numCrit=" + numCrit +
                ", numNonCrit=" + numNonCrit +
                ", hazardRating='" + hazardRating + '\'' +
                ", violations=" + violations + '\'' +
                ", violationCode=" + violationCode + '\'' +
                '}';
    }
}
