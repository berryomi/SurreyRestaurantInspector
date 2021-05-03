package com.example.sodium_project.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.sodium_project.R;
import com.example.sodium_project.sodium_project.model.DateDisplay;
import com.example.sodium_project.sodium_project.model.FavouritesManager;
import com.example.sodium_project.sodium_project.model.FileReader;


import com.example.sodium_project.sodium_project.model.Inspection;
import com.example.sodium_project.sodium_project.model.InspectionManager;
import com.example.sodium_project.sodium_project.model.Restaurant;
import com.example.sodium_project.sodium_project.model.RestaurantManager;
import com.example.sodium_project.sodium_project.model.SearchFilter;
import com.example.sodium_project.sodium_project.model.SearchFilterManager;
import com.example.sodium_project.sodium_project.model.ViolationManager;
import com.example.sodium_project.util.IconForRestaurantLoader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Class name: RestaurantListActivity
 *
 * Description: RestaurantListActivity will populate a list of restaurants and show some brief information.
 *              If the user click a restaurant, it will call the RestaurantActivity to show more
 *              details about the clicked restaurant.
 */


public class RestaurantListActivity extends AppCompatActivity implements FilterDialogFragment.myFilterDialogListener{
    private RestaurantManager fullRestaurantManager;
    private SearchFilterManager restaurantManager;
    private InspectionManager inspectionManager;
    private ViolationManager violationManager;
    private FileReader fileReader;
    private FavouritesManager favouritesManager;
    private DateDisplay aDateDisplayer = new DateDisplay();
    private ArrayAdapter<Restaurant> restaurantArrayAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inspectionManager = InspectionManager.getInstance();
        violationManager = ViolationManager.getInstance();
        fileReader = new FileReader();
        favouritesManager = FavouritesManager.getInstance();

        loadFavouritesList();

        fullRestaurantManager = RestaurantManager.getInstance();
        Collections.sort(fullRestaurantManager.getRestaurantList());
        restaurantManager = SearchFilterManager.getInstance();       //Testing function usage
        Collections.sort(restaurantManager.getRestaurantList());

        if(violationManager.violationIsEmpty() <= 0){
            fileReader.readRestaurantData(this, R.raw.restaurants_itr1);
            fileReader.readInspectionData(this, R.raw.inspectionreports_itr1);
            fileReader.readViolationTxtFile(this, R.raw.all_violations);
            violationManager.loadShortDescriptionFromResources(this, R.array.shortDescription);
            displayDialogBox();
        }

        populateRestaurantListView();
        setUpOnClickListenerForRestaurantsList();
        setUpOnclickListenerForGoToMapView();
        SearchFilter searchFilter = new SearchFilter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        final SearchView mySearchView = (SearchView) menuItem.getActionView();
        searchView = mySearchView;
        mySearchView.setQueryHint(getString(R.string.searchViewHint));

        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchFilterManager.inputNameOfRestaurant = query;
                SearchFilter searchFilter = new SearchFilter();
                closeKeyBoard();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    SearchFilterManager.inputNameOfRestaurant = "-1";
                    SearchFilter searchFilter = new SearchFilter();
                    restaurantArrayAdapter = new MyListAdapter(restaurantManager.getRestaurantList());
                    ListView listView = findViewById(R.id.restaurantList_ListVIew);
                    listView.setAdapter(restaurantArrayAdapter);
                    closeKeyBoard();
                }
                else
                    restaurantArrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        if(!SearchFilterManager.inputNameOfRestaurant.equals("-1") &&
                !SearchFilterManager.inputNameOfRestaurant.equals("")){
            mySearchView.setQuery(SearchFilterManager.inputNameOfRestaurant, false);
            searchView.clearFocus();
            searchView.setIconified(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.filter){
            final DialogFragment dialogFragment = new FilterDialogFragment();
            dialogFragment.setCancelable(false);

            dialogFragment.show(getSupportFragmentManager(), "restaurantList");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        SearchFilterManager.inputNameOfRestaurant = searchView.getQuery().toString();
        SearchFilter searchFilter = new SearchFilter();
        super.onPause();
    }

    public static Intent makeIntentForRestaurantListActivity(Context context){
        return new Intent(context, RestaurantListActivity.class);
    }

    private void displayDialogBox() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantListActivity.this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.main_activity_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setTitle(R.string.mainActivityDialogTitle);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void populateRestaurantListView() {
        restaurantArrayAdapter = new MyListAdapter(restaurantManager.getRestaurantList());
        ListView listView = findViewById(R.id.restaurantList_ListVIew);
        listView.setAdapter(restaurantArrayAdapter);
    }

    // set the actions when the user clicks a element
    private void setUpOnClickListenerForRestaurantsList() {
        ListView listView = findViewById(R.id.restaurantList_ListVIew);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int count = 0;
                RestaurantManager manager = RestaurantManager.getInstance();
                String trackingNumber = restaurantManager.getRestaurantList().get(position).getTrackingNumber();

                for(Restaurant restaurant : manager.getRestaurantList()){
                    if(restaurant.getTrackingNumber().equals(trackingNumber))
                        break;
                    count++;
                }

                Intent intent = RestaurantActivity.makeIntent(RestaurantListActivity.this, count);
                intent.putExtra("FROM_ACTIVITY","LIST");
                startActivity(intent);
            }
        });
    }

    private void setUpOnclickListenerForGoToMapView(){
        FloatingActionButton floatingActionButton = findViewById(R.id.mapView_floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MapsActivity.makeIntentForMapsActivity(getApplicationContext()
                        , -1));
                finish();
            }
        });
    }

    @Override
    public void onOkClicked() {
        restaurantArrayAdapter = new MyListAdapter(restaurantManager.getRestaurantList());
        ListView listView = findViewById(R.id.restaurantList_ListVIew);
        listView.setAdapter(restaurantArrayAdapter);
    }

    private class MyListAdapter extends ArrayAdapter<Restaurant> implements Filterable {
        private List<Restaurant> restaurantList;
        private List<Restaurant> copyOfRestaurantList;

        public MyListAdapter(List<Restaurant> myRestaurantList) {
            super(RestaurantListActivity.this, R.layout.main_activity_listview,
                    myRestaurantList);
            this.restaurantList = myRestaurantList;
            copyOfRestaurantList = new ArrayList<>(this.restaurantList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //inflate the ListView in to each element of the list
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.main_activity_listview,
                        parent, false);
            }

            // Change the text of the list elements
            Restaurant restaurant = restaurantManager.retrieveRestaurant(position);
            String hazardLevel = "";
            Inspection inspection = restaurant.getLatestInspection(restaurant.getTrackingNumber());

            TextView textView = view.findViewById(R.id.ResNameOutPut_textView);
            textView.setText(restaurant.getName());

            textView = view.findViewById(R.id.numIssuesFoundOutput_textView);
            if(inspection != null)
                textView.setText(String.format(getResources().getString(R.string.display_numbers),
                        inspection.getNumCrit() + inspection.getNumNonCrit()));
            else
                textView.setText(String.format(getResources().getString(R.string.NA)));

            textView = view.findViewById(R.id.numCriticalOutput_textView);
            if(inspection != null)
                textView.setText(String.format(getResources().getString(R.string.display_numbers),
                        inspection.getNumCrit()));
            else
                textView.setText(String.format(getResources().getString(R.string.NA)));


            textView = view.findViewById(R.id.numNonCriticalOutput_textView);
            if(inspection != null)
                textView.setText(String.format(getResources().getString(R.string.display_numbers),
                        inspection.getNumNonCrit()));
            else
                textView.setText(String.format(getResources().getString(R.string.NA)));

            textView = view.findViewById(R.id.inspectionTimeOutput_textView);
            if(inspection != null)
                textView.setText(aDateDisplayer.displayInspectionDateMeaningfully(
                                                inspection, RestaurantListActivity.this));
            else
                textView.setText(String.format(getResources().getString(R.string.NA)));

            ImageView imageView = view.findViewById(R.id.smilingFace_imageView);

            if(inspection != null)
                hazardLevel = inspection.getHazardRating();

            imageView.setVisibility(View.VISIBLE);
            switch (hazardLevel) {

                case "Low":
                    imageView.setImageResource(R.drawable.green_smiling_face);
                    break;
                case "Moderate":
                    imageView.setImageResource(R.drawable.yellow_smiling_face);
                    break;
                case "High":
                    imageView.setImageResource(R.drawable.red_smiling_face);
                    break;
                default:
                    imageView.setImageResource(R.drawable.question_smiling_face);
                    break;
            }

            ImageView favView = view.findViewById(R.id.favouriteRestaurant);
            if (!favouritesManager.isFavouriteRestaurant(restaurant.getTrackingNumber())) {
                favView.setImageResource(R.drawable.starhollow);
            } else {
                favView.setImageResource(R.drawable.staryellow);
            }

            ImageView logo = view.findViewById(R.id.restaurantImage_imageView);
            IconForRestaurantLoader imageLoader = new IconForRestaurantLoader();
            int imageNum = imageLoader.loadImage(restaurantManager.getRestaurantList().get(position));
            logo.setImageResource(imageNum);

            return view;
        }

        @Override
        public Filter getFilter(){
            return myCustomFilter;
        }

        private Filter myCustomFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Restaurant> filteredList = new ArrayList<>();

                if(constraint == null || constraint.length() == 0){
                    filteredList.addAll(copyOfRestaurantList);
                }
                else {
                    String filterText = constraint.toString().toLowerCase().trim();

                    for(Restaurant restaurant : copyOfRestaurantList){
                        if(restaurant.getName().toLowerCase().contains(filterText)){
                            filteredList.add(restaurant);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                restaurantList.clear();
                restaurantList.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public void loadFavouritesList() {
        List<String> tempList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("favourites", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("FavouritesList", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        tempList = gson.fromJson(json, type);
        favouritesManager.setFavouritesList(tempList);
        favouritesManager.initializeFavouriteRestaurantList();
    }

    private void closeKeyBoard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        //Create a brand new map, going to current location of person
        Intent mapIntent = MapsActivity.makeIntentForNewMap(RestaurantListActivity.this);
        startActivity(mapIntent);
    }

    @Override
    public void onBackPressed() {
        //Goes back to home page
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
