package com.example.sodium_project.sodium_project.model;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Class Name: InspectionManager
 *
 * Description: A class that stores and manages a list of all inspections that occurred before.
 *
 */

public class InspectionManager implements Iterable<Inspection>{

    private List<Inspection> inspectionList = new ArrayList<>();

    //Singleton support
    private static InspectionManager instance;

    private InspectionManager() {

    }

    public static InspectionManager getInstance() {
        if (instance == null) {
            instance = new InspectionManager();
        }
        return instance;
    }

    public void add(Inspection inspection) {
        inspectionList.add(inspection);
    }

    public Inspection retrieve(int position) {
        return inspectionList.get(position);
    }

    public int getTotalNumberOfInspection() {
        return inspectionList.size();
    }

    public void resetAllInspections() {
        inspectionList.clear();
    }

    @Override
    public Iterator<Inspection> iterator() {
        return inspectionList.iterator();
    }
}
