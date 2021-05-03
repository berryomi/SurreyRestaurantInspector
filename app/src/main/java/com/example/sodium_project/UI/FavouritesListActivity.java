package com.example.sodium_project.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.sodium_project.sodium_project.model.DateDisplay;
import com.example.sodium_project.sodium_project.model.FavouritesManager;
import com.example.sodium_project.sodium_project.model.InspectionManager;
import com.example.sodium_project.sodium_project.model.Restaurant;
import com.example.sodium_project.sodium_project.model.RestaurantManager;
import com.example.sodium_project.util.IconForRestaurantLoader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sodium_project.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Class name: FavouritesListActivity
 *
 * Description: FavouritesListActivity will populate and manage the list of favourite restaurants
 *              added by the user. The data in this activity will be saved between execution.
 *
 */

public class FavouritesListActivity extends AppCompatActivity {
    private FavouritesManager favouritesManager = FavouritesManager.getInstance();
    private RestaurantManager restaurantManager = RestaurantManager.getInstance();
    private InspectionManager inspectionManager = InspectionManager.getInstance();
    private DateDisplay aDateDisplayer = new DateDisplay();
    private List<Restaurant> updatedFavourites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadFavouritesList();
        populateFavouritesList();
        setUpExitButton();
        populateListView();

    }

    public static Intent makeIntent(Context context){
        return new Intent(context, FavouritesListActivity.class);
    }

    private void setUpExitButton() {
        Button button = findViewById(R.id.favouritesExitButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MapsActivity.makeIntentForMapsActivity(FavouritesListActivity.this, -1);
                startActivity(intent);
            }
        });

    }

    private void populateFavouritesList() {
        for (Restaurant restaurant: restaurantManager.getRestaurantList()) {
            for (String fav : favouritesManager.getFavouritesList()) {
                String[] line = fav.split(",");
                String trackingNum = line[0];
                String inspectionDate = line[1];

                if (restaurant.getInspectionList(restaurant.getTrackingNumber()).size() > 0) { // for restaurant with no inspection
                    String latestInspectionDate = restaurant.getLatestInspection(restaurant.getTrackingNumber()).getInspectionDate();
                    if (trackingNum.equals(restaurant.getTrackingNumber())) { // if this is user's favourite restaurant
                        // check if there is any update to this restaurant
                        if (Integer.valueOf(inspectionDate) < Integer.valueOf(latestInspectionDate)) {
                            updatedFavourites.add(restaurant);
                        }
                    }
                }
            }
        }

        updateFavouritesList();

        if (updatedFavourites.size() > 0) {
            TextView textView = findViewById(R.id.emptyUpdatedFav);
            textView.setText("");
        }
    }

    private void populateListView() {
        ArrayAdapter<Restaurant> arrayAdapter = new FavouritesListActivity.MyListAdapter();
        ListView listView = findViewById(R.id.favouritesListview);
        listView.setAdapter(arrayAdapter);
    }

    private class MyListAdapter extends ArrayAdapter<Restaurant> {
        public MyListAdapter() {
            super(FavouritesListActivity.this, R.layout.favourites_activity_listview,
                    updatedFavourites);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.favourites_activity_listview,
                        parent, false);
            }

            if (updatedFavourites == null) {
                return view;
            }

            Restaurant restaurant = updatedFavourites.get(position);

            TextView nameView = view.findViewById(R.id.favRestaurantName);
            nameView.setText(restaurant.getName());

            TextView dateView = view.findViewById(R.id.FavLatestInspectionDate);
            dateView.setText(aDateDisplayer.displayInspectionDateMeaningfully(
                    restaurant.getLatestInspection(restaurant.getTrackingNumber()),
                    FavouritesListActivity.this));

            TextView hazardView = view.findViewById(R.id.favHazardLevel);
            String hazardRate = restaurant.getLatestInspection(restaurant.getTrackingNumber()).getHazardRating();
            switch (hazardRate){
                case "Low":
                    hazardView.setText(getResources().getStringArray(R.array.filterHazardListOptions)[1]);
                    break;
                case "Moderate":
                    hazardView.setText(getResources().getStringArray(R.array.filterHazardListOptions)[2]);
                    break;
                case "High":
                    hazardView.setText(getResources().getStringArray(R.array.filterHazardListOptions)[3]);
                    break;
            }

            TextView issueView = view.findViewById(R.id.favNumIssuesFound);
            int numCrit = restaurant.getLatestInspection(restaurant.getTrackingNumber()).getNumCrit();
            int numNonCrit = restaurant.getLatestInspection(restaurant.getTrackingNumber()).getNumNonCrit();
            int numIssues = numCrit + numNonCrit;
            issueView.setText("" + numIssues);

            ImageView faceView = view.findViewById(R.id.favHazardLevelFace);
            switch (hazardRate) {
                case "Low":
                    hazardView.setTextColor(ContextCompat.getColor(FavouritesListActivity.this, R.color.colorGreen));
                    faceView.setImageResource(R.drawable.green_smiling_face);
                    break;
                case "Moderate":
                    hazardView.setTextColor(ContextCompat.getColor(FavouritesListActivity.this, R.color.colorYellow));
                    faceView.setImageResource(R.drawable.yellow_smiling_face);
                    break;
                case "High":
                    hazardView.setTextColor(ContextCompat.getColor(FavouritesListActivity.this, R.color.colorRed));
                    faceView.setImageResource(R.drawable.red_smiling_face);
                    break;
            }

            ImageView imageView = view.findViewById(R.id.favRestaurantImage);
            IconForRestaurantLoader imageLoader = new IconForRestaurantLoader();
            int imageNum = imageLoader.loadImage(restaurant);
            imageView.setImageResource(imageNum);

            return view;
        }
    }

    public void loadFavouritesList() {
        List<String> tempList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("favourites", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("FavouritesList", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        tempList = gson.fromJson(json, type);
        favouritesManager.setFavouritesList(tempList);
    }

    public void saveFavouritesList() {
        SharedPreferences sharedPreferences = getSharedPreferences("favourites", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favouritesManager.getFavouritesList());
        editor.putString("FavouritesList", json);
        editor.apply();
    }

    // Method to update the favourites list of new inspection date
    public void updateFavouritesList() {
        for (Restaurant updatedRestaurant: updatedFavourites) {
            favouritesManager.removeRestaurantsFromFavourites(updatedRestaurant.getTrackingNumber());
            favouritesManager.addRestaurantToFavourites(updatedRestaurant.getTrackingNumber() + ","
                    + updatedRestaurant.getLatestInspection(updatedRestaurant.getTrackingNumber()).getInspectionDate());
        }

        saveFavouritesList();
    }


}
