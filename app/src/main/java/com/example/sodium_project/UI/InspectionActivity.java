package com.example.sodium_project.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.sodium_project.R;
import com.example.sodium_project.sodium_project.model.DateDisplay;
import com.example.sodium_project.sodium_project.model.Inspection;
import com.example.sodium_project.sodium_project.model.InspectionManager;
import com.example.sodium_project.sodium_project.model.ViolationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class name: InspectionActivity
 *
 * Description: InspectionActivity provides details about a single inspection of a restaurant.
 *              Includes a short and detailed explanation of the violations during the inspection.
 */

public class InspectionActivity extends AppCompatActivity {
    private ViolationManager violationManager;
    private InspectionManager inspectionManager;
    private DateDisplay aDateDisplayer = new DateDisplay();
    private static final String INSPECTIONLIST = "INSPECTION";
    private static final String POSITION = "POSITION";
    private int listPosition;

    private List<Integer> violationList = new ArrayList<>();
    private List<Inspection> inspectionList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        violationManager = ViolationManager.getInstance();
        inspectionManager = InspectionManager.getInstance();

        returnIntentData();

        setInspectionDetails();
        populateViolationListView();

    }
    //Intent
    public static Intent makeIntent(Context context, List<Inspection> inspection, int position){
        Intent intent = new Intent(context,InspectionActivity.class);
        intent.putExtra(INSPECTIONLIST,(Serializable)inspection);
        intent.putExtra(POSITION,position);

        return intent;
    }
    private void returnIntentData(){
        Intent intent = getIntent();
        inspectionList = (List<Inspection>) intent.getSerializableExtra(INSPECTIONLIST);
        listPosition = intent.getIntExtra(POSITION,0);
    }

    private void populateViolationListView(){
        ListView inspectionListView = findViewById(R.id.inspectionList);

        listAdapter adapter = new listAdapter(this,
                R.layout.inspection_activity_listview,
                violationList);

        inspectionListView.setAdapter(adapter);
    }
    private void violationButtonClicked(){
        ListView listview = findViewById(R.id.inspectionList);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int violationCode = violationList.get(position);

                String[] detailedDescriptions =
                        getResources().getStringArray(R.array.violationDescriptionToastMessages);

                for (int i = 0; i < detailedDescriptions.length; i++) {
                    if (detailedDescriptions[i].contains(violationCode + "")) {
                        Toast.makeText(InspectionActivity.this,
                                detailedDescriptions[i],
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setInspectionDetails(){
        Inspection inspection = inspectionList.get(listPosition);
        violationList = inspection.getViolationCode();

        TextView textView = findViewById(R.id.no_violations);
        if (violationList.isEmpty()) {
            textView.setText(getString(R.string.noViolations));
        } else {
            textView.setVisibility(View.INVISIBLE);
        }

        TextView dateOfInspection = findViewById(R.id.dateofInspectionEntry);
        String date = aDateDisplayer.displayInspectionDateFully(inspection, InspectionActivity.this);
        dateOfInspection.setText(date);

        TextView inspectionType = findViewById(R.id.inspectionTypeEntry);
        inspectionType.setText(inspection.getInspectionType());

        TextView criticalIssues = findViewById(R.id.criticalIssuesEntry);
        criticalIssues.setText(String.valueOf(inspection.getNumCrit()));

        TextView nonCriticalIssues = findViewById(R.id.nonCricialIssuesEntry);
        nonCriticalIssues.setText(String.valueOf(inspection.getNumNonCrit()));

        ImageView imageRating = findViewById(R.id.inspectionRatingImage);
        TextView hazardLevel = findViewById(R.id.inspectionHazardLevelEntry);
        hazardLevel.setText(inspection.getHazardRating());

        switch (inspection.getHazardRating()) {
            case "Low":
                hazardLevel.setTextColor(ContextCompat.getColor(InspectionActivity.this, R.color.colorGreen));
                imageRating.setImageResource(R.drawable.green_smiling_face);
                break;
            case "Moderate":
                hazardLevel.setTextColor(ContextCompat.getColor(InspectionActivity.this, R.color.colorYellow));
                imageRating.setImageResource(R.drawable.yellow_smiling_face);
                break;
            case "High":
                hazardLevel.setTextColor(ContextCompat.getColor(InspectionActivity.this, R.color.colorRed));
                imageRating.setImageResource(R.drawable.red_smiling_face);
                break;
            default:
                break;
        }
    }

    //Fill data for inspections
    private class listAdapter extends ArrayAdapter<Integer>{

        Context context;
        int resource;
        List<Integer> violation;
        public listAdapter(Context context, int resource, List<Integer> violationList){
            super(context,resource,violationList);
            this.context = context;
            this.resource = resource;
            this.violation = violationList;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(resource, null);

            TextView violationDescription = view.findViewById(R.id.descriptionOfViolationEntry);

            ImageView criticalImage = view.findViewById(R.id.criticalViolationImage);

            ImageView summaryImage = view.findViewById(R.id.natureOfViolationImage);


            Integer violation = violationList.get(position);

            //short description
            violationDescription.setText(violationManager.getViolationShortDescription(violation));

            //Display critical sign
            if(violationManager.getViolationCriticalness(violation).equals("Critical")){
                criticalImage.setImageResource(R.drawable.critical_sign);
            }else if(violationManager.getViolationCriticalness(violation).equals("Not Critical")){
                criticalImage.setImageResource((R.drawable.non_critical_sign));
            }else{
                criticalImage.setVisibility(View.INVISIBLE);
            }
            //Long message toast
            violationButtonClicked();
            //Nature of Violation
            switch(violationManager.getViolationNature(violation)){
                case "Food":
                    summaryImage.setImageResource(R.drawable.food);
                    break;
                case "Foodsafe":
                    summaryImage.setImageResource(R.drawable.foodsafe);
                    break;
                case "Hygiene":
                    summaryImage.setImageResource(R.drawable.hygiene);
                    break;
                case "Temperature":
                    summaryImage.setImageResource(R.drawable.temperature);
                    break;
                case "Sanitation":
                    summaryImage.setImageResource(R.drawable.sanitation);
                    break;
                case "Animals":
                    summaryImage.setImageResource(R.drawable.animals);
                    break;
                case "Equipment":
                    summaryImage.setImageResource(R.drawable.equipment);
                    break;
                case "Premise":
                    summaryImage.setImageResource(R.drawable.premise);
                    break;
                case "Chemicals":
                    summaryImage.setImageResource(R.drawable.chemicals);
                    break;
                case "Pests":
                    summaryImage.setImageResource(R.drawable.pests);
                    break;
                case "Administration":
                    summaryImage.setImageResource(R.drawable.administation);
                    break;
                default:
                    summaryImage.setVisibility(View.INVISIBLE);
                    break;

            }
            return view;
        }
    }


}
