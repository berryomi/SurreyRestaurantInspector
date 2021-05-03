package com.example.sodium_project.sodium_project.model;

import android.content.Context;

import com.example.sodium_project.R;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class Name: DateDisplay
 *
 * Description: A class that calculates and displays dates (for inspection dates).
 *
 */

public class DateDisplay {

    /**
     * Description: Display inspection dates in a meaningful way:
     *              - If it was within 30 days, display number of days ago it was (e.g.: "24 days").
     *              - If it was less than a year ago, display the month and day (e.g.: "May 12")
     *              - Otherwise, display the month and year (e.g.: "May 2018")
     */
    public String displayInspectionDateMeaningfully(Inspection inspection, Context context) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date currentDate = new Date();
        String theDayFormat;

        long diffDays = 0;
        try {
            diffDays = currentDate.getTime() - dateFormat.parse(inspection.getInspectionDate()).getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot parse");
        }

        int difference = (int) (diffDays / (24 * 60 * 60 * 1000));

        if (difference <= 30) {
            theDayFormat = context.getResources().getString(R.string.days, difference);
        }
        else if (difference > 30 && difference <= 365) {
            theDayFormat = getMonth(inspection.getInspectionMonth()) + " " + inspection.getInspectionDay();
        }
        else {
            theDayFormat = getMonth(inspection.getInspectionMonth()) + " " + inspection.getInspectionYear();
        }
        return theDayFormat;
    }

    /**
     * Description: Display the full date of an inspection (e.g.: April 6, 2020)
     */
    public String displayInspectionDateFully(Inspection inspection, Context context) {
        return getMonth( inspection.getInspectionMonth() ) + " "
                + inspection.getInspectionDay() + ", "
                + inspection.getInspectionYear();
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }
}
