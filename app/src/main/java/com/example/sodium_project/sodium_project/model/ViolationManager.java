package com.example.sodium_project.sodium_project.model;
import android.content.Context;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class Name: ViolationManager
 *
 * Description: A class that stores and manages a list of all violations
 *              in Surrey's violation database.
 *              Other classes may get specific information by passing a violation number.
 *
 */

public class ViolationManager implements Iterable<Violation> {
    private static ViolationManager instance;
    private String[] shortDescriptionString;
    private List<Violation> violationList = new ArrayList<>();

    public static ViolationManager getInstance(){
        if(instance == null)
            instance = new ViolationManager();
        return instance;
    }

    public int violationIsEmpty(){
        return violationList.size();
    }

    public void loadShortDescriptionFromResources(Context context, int stringArrayID) {
        this.shortDescriptionString = context.getResources().getStringArray(stringArrayID);

        for(int i = 0; i < violationList.size(); i++){
            String violationNumTemp = "Violation " +
                    violationList.get(i).getViolationNumber() + ":";
            for(int j = 0; j < shortDescriptionString.length; j++){
                if(shortDescriptionString[j].contains(violationNumTemp)){
                    String token[] = shortDescriptionString[i].split(": ");
                    violationList.get(i).setShortDescription(token[1]);
                }
            }
        }
    }

    private Violation getViolation(int violationNum){
        for(int i = 0; i < violationList.size(); i++){
            if(violationList.get(i).getViolationNumber() == violationNum)
                return violationList.get(i);
        }
        return null;
    }

    public void addViolation(int violationID, String isCritical,
                             String detailDescription,
                             String violationNature){
        violationList.add(new Violation(violationID, isCritical,
                detailDescription, violationNature));
    }

    public String getViolationDetailedDescription(int violationNum){
        return getViolation(violationNum).getDetailDescription();
    }

    public String getViolationShortDescription(int violationNum){
        return getViolation(violationNum).getShortDescription();
    }

    public String getViolationCriticalness(int violationNum){
        return getViolation(violationNum).getCriticalness();
    }
    public String getViolationNature(int violationNum){
        return getViolation(violationNum).getViolationNature();
    }

    @NonNull
    @Override
    public Iterator<Violation> iterator() {
        return violationList.iterator();
    }
}
