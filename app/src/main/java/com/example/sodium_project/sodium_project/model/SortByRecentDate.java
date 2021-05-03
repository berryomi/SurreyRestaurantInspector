package com.example.sodium_project.sodium_project.model;

import java.util.Comparator;

/**
 * Class Name: SortByRecentDate
 *
 * Description: A class that sorts all inspections by recent date
 *
 */

public class SortByRecentDate implements Comparator<Inspection> {

    @Override
    public int compare(Inspection o1, Inspection o2) {
        return Integer.parseInt(o2.getInspectionDate()) - Integer.parseInt(o1.getInspectionDate());
    }
}
