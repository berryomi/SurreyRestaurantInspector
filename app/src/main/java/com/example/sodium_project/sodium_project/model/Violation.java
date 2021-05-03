package com.example.sodium_project.sodium_project.model;

/**
 * Class Name: Violation
 *
 * Description: A class that stores information about one violation object.
 *
 */

public class Violation {
    private int violationNumber;
    private String criticalness;
    private String detailDescription;
    private String shortDescription;
    private String violationNature;

    public Violation(int violationNumber, String criticalness,
                     String detailDescription,
                     String violationNature) {
        this.violationNumber = violationNumber;
        this.criticalness = criticalness;
        this.detailDescription = detailDescription;
        this.violationNature = violationNature;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public int getViolationNumber() {
        return violationNumber;
    }

    public String getDetailDescription() {
        return detailDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getCriticalness(){
        return criticalness;
    }

    public String getViolationNature(){
        return violationNature;
    }

    @Override
    public String toString() {
        return "Violation{" +
                "violationNumber=" + violationNumber +
                ", isCritical='" + criticalness + '\'' +
                ", detailDescription='" + detailDescription + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", violationNature='" + violationNature + '\'' +
                '}';
    }
}
