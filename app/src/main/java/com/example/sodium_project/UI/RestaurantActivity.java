package com.example.sodium_project.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.sodium_project.R;
import com.example.sodium_project.sodium_project.model.DateDisplay;
import com.example.sodium_project.sodium_project.model.FavouritesManager;
import com.example.sodium_project.sodium_project.model.Inspection;
import com.example.sodium_project.sodium_project.model.InspectionManager;
import com.example.sodium_project.sodium_project.model.Restaurant;
import com.example.sodium_project.sodium_project.model.RestaurantManager;
import com.example.sodium_project.sodium_project.model.SortByRecentDate;
import com.example.sodium_project.util.IconForRestaurantLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Class name: RestaurantActivity
 *
 * Description: RestaurantActivity provides short descriptions about all inspections that have
 *              been carried out at the clicked restaurant (from RestaurantListActivity).
 *
 */

public class RestaurantActivity extends AppCompatActivity {
    private static final String EXTRA_RESTAURANTINDEX = "com.example.sodium_project.UI.RestaurantActivity - the restaurantIndex";
    private int restaurantIndex;
    private RestaurantManager restaurantManager = RestaurantManager.getInstance();
    private InspectionManager inspectionManager = InspectionManager.getInstance();
    private DateDisplay aDateDisplayer = new DateDisplay();
    private Restaurant restaurant;
    private List<Inspection> inspectionList;
    private String previousActivity;

    private ImageButton favourite;
    private FavouritesManager favouritesManager = FavouritesManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonPressToMapOrList();
            }
        });

        extractDataFromIntent();

        restaurant = restaurantManager.retrieveRestaurant(restaurantIndex);
        inspectionList = restaurant.getInspectionList(restaurant.getTrackingNumber());
        favourite = findViewById(R.id.favButton);

        setUpFavouritesButton();
        loadFavouritesList();

        emptyInspectionList();
        setTexts();

        populateInspectionListView();
        clickListView();
        setUpCoordinatesClickListener();

        clickFavourites();
    }

    private void setUpFavouritesButton() {
        if (favouritesManager.isFavouriteRestaurant(restaurant.getTrackingNumber())) {
            favourite.setImageResource(R.drawable.smallyellowstar);
        } else {
            favourite.setImageResource(R.drawable.smallhollowstar);
        }

    }

    private void clickFavourites() {
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favouritesManager.isFavouriteRestaurant(restaurant.getTrackingNumber())) {
                    favourite.setImageResource(R.drawable.smallhollowstar);
                    favouritesManager.removeRestaurantsFromFavourites(restaurant.getTrackingNumber());
                    saveFavouritesList(); // whenever there is an update to the favourites list, save it
                } else {
                    favourite.setImageResource(R.drawable.smallyellowstar);

                    // for restaurants with no inspections
                    if (restaurant.getInspectionList(restaurant.getTrackingNumber()).size() <= 0) {
                        favouritesManager.addRestaurantToFavourites(restaurant.getTrackingNumber() + ", ");
                    } else {
                        favouritesManager.addRestaurantToFavourites(restaurant.getTrackingNumber() + ","
                                + restaurant.getLatestInspection(restaurant.getTrackingNumber()).getInspectionDate());
                    }

                    saveFavouritesList();
                }
            }
        });
    }

    public void saveFavouritesList() {
        SharedPreferences sharedPreferences = getSharedPreferences("favourites", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favouritesManager.getFavouritesList());
        editor.putString("FavouritesList", json);
        editor.apply();
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

    private void backButtonPressToMapOrList(){
        if (previousActivity.equals("LIST")) {
            //Go back to ListActivity
            Intent intent = RestaurantListActivity.makeIntentForRestaurantListActivity
                    (RestaurantActivity.this);
            startActivity(intent);
        }else if(previousActivity.equals("MAP")){
            //startActivity(MapsActivity.makeIntentForMapsActivity(this, ""));
            //Go back to MapActivity
            Intent intent = MapsActivity.makeIntentForMapsActivity(this,-1);
            startActivity(intent);
        }
    }

    /**
     * Function created by Henry. So when the user clicks the coordinates, it will navigate to
     * the MapView. Let me know if this function create bugs
     */
    private void setUpCoordinatesClickListener() {
        String coords = "" + restaurant.getLatitude() + "," + restaurant.getLongitude();
        Button btn = findViewById(R.id.gpsBtn);
        btn.setText(coords);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MapsActivity.makeIntentForMapsActivity(getApplicationContext(),
                        getIntent().getIntExtra(EXTRA_RESTAURANTINDEX, -1));
                startActivity(intent);

            }
        });
    }

    public static Intent makeIntent(Context context, int restaurantIndex) {
        Intent intent = new Intent (context, RestaurantActivity.class);
        intent.putExtra(EXTRA_RESTAURANTINDEX, restaurantIndex);

        return intent;
    }


    private void extractDataFromIntent() {
        Intent intent = getIntent();
        restaurantIndex = intent.getIntExtra(EXTRA_RESTAURANTINDEX, 0);
        previousActivity = intent.getStringExtra("FROM_ACTIVITY");
    }

    private void setTexts() {
        TextView nameView = findViewById(R.id.restaurantName);
        nameView.setText(restaurant.getName());

        TextView addressView = findViewById(R.id.address);
        addressView.setText(restaurant.getAddress());

        //Load images
        ImageView restaurantLogo = findViewById(R.id.singleRestaurantImage);
        IconForRestaurantLoader imageLoader = new IconForRestaurantLoader();
        int imageNum = imageLoader.loadImage(restaurant);
        restaurantLogo.setImageResource(imageNum);
    }

    private void populateInspectionListView() {
        ArrayAdapter<Inspection> arrayAdapter = new RestaurantActivity.MyListAdapter();
        ListView listView = findViewById(R.id.inspectionList_ListView);
        listView.setAdapter(arrayAdapter);
    }

    private class MyListAdapter extends ArrayAdapter<Inspection> {
        public MyListAdapter() {
            super(RestaurantActivity.this, R.layout.restaurant_activity_listview, inspectionList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurant_activity_listview,
                        parent, false);
            }
            java.util.Collections.sort(inspectionList, new SortByRecentDate()); // sort by recent date at top
            Inspection inspection = inspectionList.get(position);

            String theDayFormat = aDateDisplayer.displayInspectionDateMeaningfully(
                                                inspection, RestaurantActivity.this);

            TextView timeView = itemView.findViewById(R.id.inspectionTime);
            timeView.setText(theDayFormat);

            TextView critView = itemView.findViewById(R.id.critical);
            String crit = String.valueOf(inspection.getNumCrit());
            critView.setText(crit);
            TextView nonCritView = itemView.findViewById(R.id.nonCritical);
            String nonCrit = String.valueOf(inspection.getNumNonCrit());
            nonCritView.setText(nonCrit);

            TextView hazardView = itemView.findViewById(R.id.hazardLevel);
            switch (inspection.getHazardRating()){
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

            ImageView imageView = itemView.findViewById(R.id.hazardIcon);
            switch (inspection.getHazardRating()) {
                case "Low":
                    hazardView.setTextColor(ContextCompat.getColor(RestaurantActivity.this, R.color.colorGreen));
                    imageView.setImageResource(R.drawable.green_smiling_face);
                    break;
                case "Moderate":
                    hazardView.setTextColor(ContextCompat.getColor(RestaurantActivity.this, R.color.colorYellow));
                    imageView.setImageResource(R.drawable.yellow_smiling_face);
                    break;
                case "High":
                    hazardView.setTextColor(ContextCompat.getColor(RestaurantActivity.this, R.color.colorRed));
                    imageView.setImageResource(R.drawable.red_smiling_face);
                    break;
            }
            return itemView;
        }
    }

    private void clickListView() {
        ListView listView = findViewById(R.id.inspectionList_ListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =  InspectionActivity.makeIntent(RestaurantActivity.this,inspectionList,position);
                startActivity(intent);

            }
        });
    }

    private void emptyInspectionList() {
        TextView textView = findViewById(R.id.noinspectionText);
        if (restaurant.getNumberOfRestaurantInspections() == 0) {
            textView.setText(getString(R.string.noInspectionYet));
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onBackPressed(){
        backButtonPressToMapOrList();
    }
}
