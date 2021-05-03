
package com.example.sodium_project.sodium_project.model;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sodium_project.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Name: FileReader
 *
 * Description: A class that loads data from external files (e.g.: .csv files, .txt files)
 *              and stores the data in their respective classes.
 *
 */

public class FileReader extends AppCompatActivity {

    private RestaurantManager restaurantManager = RestaurantManager.getInstance();
    private InspectionManager inspectionManager = InspectionManager.getInstance();
    private ViolationManager violationManager = ViolationManager.getInstance();

    // Description: Load data about all restaurants from external file
    //              (eg.: csv file, excel file, etc.)
    // Pre-condition: Assume the restaurants list in the file has already been sorted by names
    //                in alphabetically order. The columns in the file should be in this order:
    //                Tracking Number -> Name -> Address -> City
    //                -> Facility Type -> Latitude -> Longitude
    //
    //                Assume there are no empty cells in the restaurant data file
    public void readRestaurantData(Context context, int aFileID) {
        InputStream is = context.getResources().openRawResource(aFileID);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String aLine = "";
        try {
            reader.readLine(); // read the header

            while( (aLine = reader.readLine()) != null ) {
                // Split data by ','
                String[] tokens = aLine.split(",");

                // Read data
                String aTrackingNumber = tokens[0].replaceAll("\"", "");
                String aName = tokens[1].replaceAll("\"", "");
                String anAddress = tokens[2].replaceAll("\"", "");
                double aLatitude = Double.parseDouble(tokens[5]);
                double aLongitude = Double.parseDouble(tokens[6]);

                // create a restaurant object and add to the list
                Restaurant aRestaurant = new Restaurant(
                                        aTrackingNumber, aName, anAddress, aLatitude, aLongitude);
                restaurantManager.addRestaurant(aRestaurant);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading");

        }
    }

    // Description: Load all inspection data from external file
    //              (eg.: csv file, excel file, etc.)
    // Pre-condition: The columns in the file should be in this order:
    //                Tracking Number -> Inspection Date -> Inspection Type -> NumCritical
    //                -> NumNonCritical -> Hazard Rating -> Violation Lump
    //
    //                Inspection Date should be in the form YYYYMMDD
    //
    //                There can be empty cells in the inspection data file
    //                (eg.: no violation found during an inspection)
    public void readInspectionData(Context context, int aFileID) {
        InputStream is = context.getResources().openRawResource(aFileID);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String aLine = "";
        try {
            reader.readLine(); // read the header

            while( (aLine = reader.readLine()) != null ) {
                // Split data by ','
                String[] tokens = aLine.split(",");

                // Read data
                String aTrackingNumber = tokens[0].replaceAll("\"", "");
                String anInspectionDate = tokens[1].replaceAll("\"", "");
                String anInspectionType = tokens[2].replaceAll("\"", "");
                int numCritical = Integer.parseInt(tokens[3]);
                int numNonCritical = Integer.parseInt(tokens[4]);
                String aHazardRating = tokens[5].replaceAll("\"", "");
                String aViolationLump = ""; //the whole single line of violations string from CSV

                if ( (tokens.length > 6) && (tokens[6].length() > 0) ) {
                    aViolationLump = tokens[6].replaceAll("\"", "");
                    for (int i = 7; i < tokens.length; i++) {
                        aViolationLump = aViolationLump + ","
                                                + tokens[i].replaceAll("\"", "");
                    }
                } else {
                    aViolationLump = "No violations";
                }

                // whole violations string per inspection
                List<String> violation = new ArrayList<>();
                String[] viol = aViolationLump.split("\\|");
                for (int j = 0; j < viol.length; j++) {
                    if ( (viol.length > 0) && viol[j].length() > 0) {
                        violation.add(viol[j]);
                    }
                }

                // whole violation codes per inspection
                List<Integer> violationCode = new ArrayList<>();
                for (String v : violation) {
                    String[] code = v.split(",");
                    if (!v.equals("No violations")) {
                        violationCode.add(Integer.parseInt(code[0])); // store violation code
                    }
                }

                // extract date, month and year of an inspection
                int inspectionDate = Integer.valueOf(anInspectionDate);
                int aDate = inspectionDate % 100;
                int aMonth = (inspectionDate/100)%100;
                int aYear = (inspectionDate/10000);

                Inspection anInspection = new Inspection(
                        aTrackingNumber, anInspectionDate, aYear, aMonth, aDate,
                        anInspectionType, numCritical, numNonCritical,
                        aHazardRating, aViolationLump, violation, violationCode);
                inspectionManager.add(anInspection);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading file");
        }
    }

    public void readViolationTxtFile(Context context, int fileID){
        InputStream inputStream = context.getResources().openRawResource(fileID);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName("UTF-8"))
        );

        String line = "";

        try {
            while ((line = bufferedReader.readLine()) != null){
                String token[] = line.split(",");
                violationManager.addViolation(Integer.parseInt(token[0]),
                        token[1], token[2], token[5]);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading file");
        }

    }
}
