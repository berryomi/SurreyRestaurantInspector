package com.example.sodium_project.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sodium_project.R;
import com.example.sodium_project.sodium_project.model.ClusterMarker;
import com.example.sodium_project.sodium_project.model.DataDownload;
import com.example.sodium_project.sodium_project.model.FavouritesManager;
import com.example.sodium_project.sodium_project.model.FileReader;
import com.example.sodium_project.sodium_project.model.Inspection;
import com.example.sodium_project.sodium_project.model.InspectionManager;
import com.example.sodium_project.sodium_project.model.Restaurant;
import com.example.sodium_project.sodium_project.model.RestaurantManager;
import com.example.sodium_project.sodium_project.model.SearchFilter;
import com.example.sodium_project.sodium_project.model.SearchFilterManager;
import com.example.sodium_project.sodium_project.model.ViolationManager;
import com.example.sodium_project.util.MyClusterManagerRenderer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

// Reference:
// https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
// https://stackoverflow.com/questions/40371321/how-to-move-google-map-zoom-control-position-in-android
// https://stackoverflow.com/questions/42025883/google-map-api-demo-wont-run-unless-you-update-google-play-services
// https://github.com/mitchtabian/Google-Maps-2018/tree/creating-custom-google-map-markers-end/app/src/main/java/com/codingwithmitch/googlemaps2018/ui
// https://www.youtube.com/watch?v=iWYsBDCGhGw
// https://codinginflow.com/tutorials/android/hide-soft-keyboard-programmatically
// ref: https://stackoverflow.com/questions/9570237/android-check-internet-connection

/**
 * Class name: MapsActivity
 *
 * Description: This class shows a MapView to the user. The Map has markers representing restaurants.
 *              Users can tell the hazard level of each restaurant by looking at the marker icon, and
 *              tap the icon to see more details that restaurant. Users can also tap the info window
 *              again to see the restaurant's full information. Tap the coordinates from restaurant
 *              page will direct the user to this Map, and the restaurant will appear at the center
 *              of the phone screen.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, FilterDialogFragment.myFilterDialogListener {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;
    private static final String INTENT_ID_INFO = "ID";
    private static final String GOOGLE_MAP_ZOOM_IN_BUTTON = "GoogleMapZoomInButton";
    public static final String SHARED_PREF = "LastUpdateTime";
    public static final String TIME = "TimeLastUpdated";
    public static final String SHARED_PREF_COPY = "CopyCount";
    public static final String COPY = "NumberOfCopies";
    private final LatLng mDefaultLocation = new LatLng(49.7867, -122.8494);
    private static final String TAG = "MapsActivity";
    private static BitmapDescriptor redFace;
    private static BitmapDescriptor greenFace;
    private static BitmapDescriptor yellowFace;
    private static BitmapDescriptor questionFace;

    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ClusterMarker clickedClusterMarker;
    private Restaurant clickedRestaurantFromInspectionActivity;
    private ClusterManager<ClusterMarker> myClusterManager;
    private MyClusterManagerRenderer myClusterManagerRenderer;
    private boolean mLocationPermissionGranted;
    private boolean isFollowing;
    private boolean cameraIsMoving;
    private boolean isFromDialog;

    private int copy;
    private String restaurantDataLastModified = "";
    private String inspectionDataLastModified = "";
    private String lastUpdateTime = "00000000 00:00:00";
    private String restaurantURL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private String inspectionURL = " http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private RestaurantManager restaurantManager;
    private InspectionManager inspectionManager;
    private DataDownload dataDownload;
    private FavouritesManager favouritesManager;
    private SearchFilterManager searchFilterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        searchFilterManager = SearchFilterManager.getInstance();
        restaurantManager = RestaurantManager.getInstance();
        inspectionManager = InspectionManager.getInstance();
        ViolationManager violationManager = ViolationManager.getInstance();
        FileReader fileReader = new FileReader();
        dataDownload = DataDownload.getInstance();
        favouritesManager = FavouritesManager.getInstance();
        SearchFilterManager searchFilterManager = SearchFilterManager.getInstance();

        loadFavouritesList();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        checkGooglePlayService();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();

        moveZoomControls(mapFragment.getView(), 42, -1, -1, 70, false, false);
        isFollowing = true;

        if(restaurantManager.getRestaurantList().size() <= 0){ // when user have cancelled the download
            restaurantManager.resetAllRestaurants();
            inspectionManager.resetAllInspections();

            SharedPreferences prefs = getSharedPreferences(SHARED_PREF_COPY, MODE_PRIVATE);
            copy = prefs.getInt(COPY, 0);

            if (copy > 0) {
                readRestaurantDataLocally(this);
                readInspectionDataLocally(this);
            } else {
                fileReader.readRestaurantData(this, R.raw.restaurants_itr1);
                fileReader.readInspectionData(this, R.raw.inspectionreports_itr1);
            }
            fileReader.readViolationTxtFile(this, R.raw.all_violations);
            violationManager.loadShortDescriptionFromResources(this, R.array.shortDescription);

        }

        loadFavouritesList();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(mLocationPermissionGranted)
            setUpLocationIfPermissionGranted();

        getBitMapIcons();
        updateLocationUI();
        setUpListButtonClickListener();
        setUpOnMapClickListener();
        checkIfCalledByAnotherActivity();
        setUpMapIdleListener();
        setUpSearchView();
        setUpFilterImageButton();
        if(checkNetWorkConnection()){
            if (checkLastUpdate()) {
                CheckWithServer checkWithServer = new CheckWithServer();
                checkWithServer.execute();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                refreshMaps();
            }
            else {
                startActivity(RestaurantListActivity.makeIntentForRestaurantListActivity(this));
            }
        }
    }

    @Override
    public void onOkClicked() {
        isFromDialog = true;
        refreshMapFromFilter();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        //Create a brand new map, going to current location of person
        Intent mapIntent = makeIntentForNewMap(MapsActivity.this);
        startActivity(mapIntent);
    }

    @Override
    public void onBackPressed(){
        //Goes back to home page
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static Intent makeIntentForMapsActivity(Context context, int listPosition){
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra(INTENT_ID_INFO, listPosition);
        return intent;
    }

    public void refreshMaps(){
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private void checkIfCalledByAnotherActivity() {
        BitmapDescriptor bitmapDescriptor;
        Intent intent = getIntent();
        int position = intent.getIntExtra(INTENT_ID_INFO, -1);

        if(position != -1){
            String hazardRating = "";
            final Restaurant restaurant = restaurantManager.retrieveRestaurant(position);
            clickedRestaurantFromInspectionActivity = restaurant;
            FloatingActionButton floatingActionButton =
                    findViewById(R.id.myLocationBlue_floatingActionButton);

            floatingActionButton.callOnClick();

            Inspection inspection = restaurant.getLatestInspection(restaurant.getTrackingNumber());
            if(inspection != null)
                hazardRating = inspection.getHazardRating();

            switch (hazardRating){
                case "Low":
                    bitmapDescriptor = greenFace;
                    break;
                case "Moderate":
                    bitmapDescriptor = yellowFace;
                    break;
                case "High":
                    bitmapDescriptor = redFace;
                    break;
                default:
                    bitmapDescriptor = questionFace;
            }

            final LatLng location = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());

            isFollowing = false;
            Marker marker = mMap.addMarker(new MarkerOptions().title(restaurant.
                    getName()).
                    snippet(restaurant.getAddress()).
                    position(latLng)
                    .icon(bitmapDescriptor));
            mMap.setInfoWindowAdapter(new MapInfoWindowAdapter(this, this));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if(clickedRestaurantFromInspectionActivity != null)
                        infoWindowClickListenerHelper(clickedRestaurantFromInspectionActivity);
                }
            });
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    changeMapToNormalState();
                    return false;
                }
            });
            mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    changeMapToNormalState();
                }
            });
            marker.showInfoWindow();
        }
        else{
            if(mLocationPermissionGranted)
                getDeviceLocation();
            SearchFilter searchFilter = new SearchFilter();
            addMapMarkers(searchFilterManager.getRestaurantList());
            setUpMarkerClusterOnClickListener();
            setUpClusterMapInfoWindow();
        }
    }

    private void changeMapToNormalState(){
        if(clickedRestaurantFromInspectionActivity != null){
            mMap.clear();
            refreshMapFromFilter();
            clickedRestaurantFromInspectionActivity = null;
        }
    }

    public ClusterMarker getClickedClusterMarker() {
        return clickedClusterMarker;
    }

    private void addMapMarkers(List<Restaurant> myRestaurantList){
        LatLng markerPosition;
        LatLngBounds.Builder builder = LatLngBounds.builder();

        if(mMap != null){
            if(myClusterManager == null){
                myClusterManager = new ClusterManager(this, mMap);
            }
            if(myClusterManagerRenderer == null){
                myClusterManagerRenderer = new MyClusterManagerRenderer(
                        this,
                        mMap,
                        myClusterManager
                );
                myClusterManager.setRenderer(myClusterManagerRenderer);
            }

            myClusterManager.clearItems();

            /**
             * This temp List is necessary since we don't want to thrown an
             * "ConcurrentModificationException"
             */
            List<Restaurant> myTempRestaurantList;

            if(myRestaurantList == null)
                 myTempRestaurantList = restaurantManager.getRestaurantList();
            else
                myTempRestaurantList = myRestaurantList;

            List<ClusterMarker> items = new ArrayList<>();

            for(int i = 0; i < myTempRestaurantList.size(); i++){
                int restaurantMarkerPicture;
                String hazardRating = "";
                Restaurant restaurant = myTempRestaurantList.get(i);

                Inspection inspection = restaurant.
                        getLatestInspection(restaurant.getTrackingNumber());
                if(inspection != null)
                    hazardRating = inspection.getHazardRating();

                switch (hazardRating){
                    case "Low":
                        restaurantMarkerPicture = R.drawable.bigger_happy_smiley_pin;
                        break;
                    case "Moderate":
                        restaurantMarkerPicture = R.drawable.bigger_neutral_smiley_pin;
                        break;
                    case "High":
                        restaurantMarkerPicture = R.drawable.bigger_sad_smiley_pin;
                        break;
                    default:
                        restaurantMarkerPicture = R.drawable.bigger_noinspection_smiley_pin;
                }
                ClusterMarker clusterMarker = new ClusterMarker(
                        new LatLng(restaurant.getLatitude(), restaurant.getLongitude()),
                        restaurant.getName(),
                        restaurant.getAddress(),
                        restaurantMarkerPicture,
                        restaurant
                );
                markerPosition = clusterMarker.getPosition();
                builder.include(markerPosition);
                items.add(clusterMarker);
            }
            myClusterManager.addItems(items);
            // mMap.setOnCameraIdleListener(myClusterManager);
            myClusterManager.cluster();

            if(isFromDialog && myTempRestaurantList.size() >= 1){
                isFromDialog = false;
                final LatLngBounds bounds = builder.build();

                if(isFollowing){
                    FloatingActionButton floatingActionButton = findViewById(
                            R.id.myLocationBlue_floatingActionButton);
                    floatingActionButton.performClick();
                }

                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
                } catch (Exception error) {
                    Log.d("MapActivity", Objects.requireNonNull(error.getMessage()));
                }
            }
        }
    }

    private void setUpOnMapClickListener() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                FloatingActionButton floatingActionButton = findViewById(
                        R.id.myLocationBlue_floatingActionButton);
                if(isFollowing)
                    floatingActionButton.performClick();
                if(clickedRestaurantFromInspectionActivity != null)
                    changeMapToNormalState();
            }
        });
    }

    private void setUpClusterMapInfoWindow() {
        myClusterManager.getMarkerCollection().setInfoWindowAdapter(
                new MapInfoWindowAdapter(MapsActivity.this, this));
        // Starts the restaurant activity
        myClusterManager.getMarkerCollection().setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Restaurant restaurant = clickedClusterMarker.getRestaurant();
                        infoWindowClickListenerHelper(restaurant);
                        //finish();
                    }
                });
    }

    private void setUpSearchView(){
        final SearchView searchView = findViewById(R.id.mapSearchView);
        searchView.setQueryHint(getString(R.string.searchViewHint));
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchFilterManager.inputNameOfRestaurant = query;
                SearchFilter searchFilter = new SearchFilter();
                isFromDialog = true;
                refreshMapFromFilter();
                searchView.setQueryHint(getString(R.string.clear_search_hint));
                closeKeyBoard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    SearchFilterManager.inputNameOfRestaurant = "-1";
                    searchView.setQueryHint(getString(R.string.searchViewHint));
                    SearchFilter searchFilter = new SearchFilter();
                    refreshMapFromFilter();
                    closeKeyBoard();
                }
                return false;
            }
        });

        if(!SearchFilterManager.inputNameOfRestaurant.equals("-1") &&
                !SearchFilterManager.inputNameOfRestaurant.equals("")){
            searchView.setQuery(SearchFilterManager.inputNameOfRestaurant, false);
            searchView.clearFocus();
            searchView.setIconified(false);
        }
    }

    private void infoWindowClickListenerHelper(Restaurant restaurant){
        restaurant.getTrackingNumber();
        Intent intent = RestaurantActivity.makeIntent(MapsActivity.this,
                restaurantManager.getRestaurantList().indexOf(restaurant));
        intent.putExtra("FROM_ACTIVITY","MAP");
        startActivity(intent);
    }

    private void setUpFilterImageButton(){
        final DialogFragment dialogFragment = new FilterDialogFragment();
        dialogFragment.setCancelable(false);

        ImageButton imageButton = findViewById(R.id.mapFilter_ImageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.show(getSupportFragmentManager(), "Maps");
            }
        });
    }

    private void setUpMyLocationButtonClickListener() {
        final FloatingActionButton myLocationButtonGrey = findViewById(
                R.id.myLocationGrey_floatingActionButton);
        final FloatingActionButton myLocationButtonBlue = findViewById(
                R.id.myLocationBlue_floatingActionButton);
        final FloatingActionButton floatingActionButtonListBlue =
                findViewById(R.id.listBlue_floatingActionButton);
        final FloatingActionButton floatingActionButtonListGrey =
                findViewById(R.id.listGrey_floatingActionButton);

        myLocationButtonGrey.hide();

        myLocationButtonGrey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude()), 15.0f));
                myLocationButtonBlue.show();
                floatingActionButtonListGrey.hide();
                floatingActionButtonListBlue.show();
                myLocationButtonGrey.hide();
                isFollowing = true;
                Toast.makeText(getApplicationContext(),
                        "Camera Mode: Tracking", Toast.LENGTH_SHORT).show();
            }
        });

        myLocationButtonBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocationButtonBlue.hide();
                floatingActionButtonListGrey.show();
                floatingActionButtonListBlue.hide();
                myLocationButtonGrey.show();
                isFollowing = false;
                Toast.makeText(getApplicationContext(),
                        "Camera Mode: Free", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpListButtonClickListener() {
        final FloatingActionButton floatingActionButton = findViewById(R.id.listBlue_floatingActionButton);
        final FloatingActionButton floatingActionButtonTwo = findViewById(R.id.listGrey_floatingActionButton);

        floatingActionButtonTwo.hide();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RestaurantListActivity.makeIntentForRestaurantListActivity(
                        MapsActivity.this));
            }
        };

        floatingActionButton.setOnClickListener(onClickListener);
        floatingActionButtonTwo.setOnClickListener(onClickListener);
    }

    private void setUpMarkerClusterOnClickListener() {
        myClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ClusterMarker>() {
            @Override
            public boolean onClusterClick(Cluster<ClusterMarker> cluster) {
                if(isFollowing){
                    FloatingActionButton floatingActionButton = findViewById(
                            R.id.myLocationBlue_floatingActionButton);
                    isFollowing = false;
                    floatingActionButton.performClick();
                }
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (ClusterItem item : cluster.getItems()) {
                    LatLng markerPosition = item.getPosition();
                    builder.include(markerPosition);
                }

                final LatLngBounds bounds = builder.build();

                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                } catch (Exception error) {
                    Log.d("MapActivity", Objects.requireNonNull(error.getMessage()));
                }

                return true;
            }
        });

        myClusterManager
                .setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterMarker>() {
                    @Override
                    public boolean onClusterItemClick(ClusterMarker item) {
                        if(isFollowing){
                            FloatingActionButton floatingActionButton = findViewById(
                                    R.id.myLocationBlue_floatingActionButton);
                            isFollowing = false;
                            floatingActionButton.performClick();
                        }
                        clickedClusterMarker = item;
                        return false;
                    }
                });
    }

    private void setUpLocationUpdateLister() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastKnownLocation = locationResult.getLastLocation();

                if (mLastKnownLocation != null && isFollowing && !cameraIsMoving) {
                    cameraIsMoving = true;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                }
            }
        };
        mFusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                getMainLooper());
    }

    private void setUpMapIdleListener (){
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                cameraIsMoving = false;
                if(myClusterManager != null)
                    myClusterManager.onCameraIdle();
            }
        });
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            FloatingActionButton floatingActionButton = findViewById(
                                    R.id.myLocationBlue_floatingActionButton);
                            floatingActionButton.callOnClick();
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            mLocationPermissionGranted = false;

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void checkGooglePlayService(){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(getApplicationContext());

        if(result != ConnectionResult.SUCCESS){
            if(googleApiAvailability.isUserResolvableError(result)){
                Dialog dialog = googleApiAvailability.getErrorDialog(this, result,
                        1);
                dialog.setCancelable(false);
                dialog.show();
            }
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mLastKnownLocation = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setUpLocationIfPermissionGranted(){
        setUpMyLocationButtonClickListener();
        setUpLocationUpdateLister();
        setUpMyLocationButtonClickListener();
    }

    private void moveZoomControls(View mapView, int left, int top, int right,
                                  int bottom, boolean horizontal, boolean vertical) {
        assert mapView != null;
        View zoomIn = mapView.findViewWithTag(GOOGLE_MAP_ZOOM_IN_BUTTON);

        // we need the parent view of the zoomin/zoomout buttons - it didn't have a tag
        // so we must get the parent reference of one of the zoom buttons
        if(zoomIn != null){
            View zoomInOut = (View) zoomIn.getParent();

            if (zoomInOut != null) {
                moveView(zoomInOut,left,top,right,bottom,horizontal,vertical);
            }
        }
    }

    private void moveView(View view, int left, int top, int right,
                          int bottom, boolean horizontal, boolean vertical) {
        try {
            assert view != null;

            // replace existing layout params
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            if (left >= 0) {
                rlp.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            }

            if (top >= 0) {
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            }

            if (right >= 0) {
                rlp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            }

            if (bottom >= 0) {
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            }

            if (horizontal) {
                rlp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            }

            if (vertical) {
                rlp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            }

            rlp.setMargins(left, top, right, bottom);

            view.setLayoutParams(rlp);
        } catch (Exception ex) {
            Log.e(TAG, "moveView() - failed: " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }

    private void getBitMapIcons() {
        if(redFace == null){
            IconRender iconRender = IconRender.getInstance(this);

            redFace = iconRender.getRedFace();
            greenFace = iconRender.getGreenFace();
            yellowFace = iconRender.getYellowFace();
            questionFace = iconRender.getQuestionFace();
        }
    }

    private void closeKeyBoard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean checkNetWorkConnection(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected()){
            return true;
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.warning)
                    .setMessage(R.string.network_warning)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
            return false;
        }
    }

    public static Intent makeIntentForNewMap(Context context){
        Intent intent = new Intent(context, MapsActivity.class);
        return intent;
    }

    private void refreshMapFromFilter(){
        if(myClusterManager != null){
            myClusterManager.clearItems();
            myClusterManagerRenderer.onRemove();
            myClusterManagerRenderer = null;
        }
        mMap.clear();

        SearchFilterManager searchFilterManager = SearchFilterManager.getInstance();
        addMapMarkers(searchFilterManager.getRestaurantList());
        setUpMarkerClusterOnClickListener();
        setUpClusterMapInfoWindow();
    }

    private class CheckWithServer extends AsyncTask<URL, Void, Void>{

        boolean needToUpdate;
        private RequestQueue QueueRestaurantDate;
        private RequestQueue QueueInspectionDate;

        void createAskAlertDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.update_data_dialog, null);
            builder.setView(view)
                    .setCancelable(false)
                    .setTitle(R.string.update_data_dialog_title)
                    .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DownLoadDataTask downLoadDataTask = new DownLoadDataTask();
                            downLoadDataTask.execute();
                        }
                    });
            builder.show();
        }

        @Override
        protected Void doInBackground(URL... urls) {
            checkRestaurantDataLastModified(MapsActivity.this);
            checkInspectionDataLastModified(MapsActivity.this);

            while (!dataDownload.isCheckedFromServer()){
                //spin waiting
            }

            dataDownload.setCheckedFromServer(false);
            needToUpdate = dataDownload.isNeedToDownloadNewDataFromServer();
            dataDownload.setNeedToDownloadNewDataFromServer(false);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(needToUpdate){
                createAskAlertDialog();
            }
        }

        // method to check if the data in server has updated since the app's lastUpdateTime
        private Boolean checkIfUpdateAvailable(String dateLastModified) {
            loadSavedTime();

            Boolean updateAvailable = false;
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

            dateLastModified = dateLastModified.substring(0,4) + dateLastModified.substring(5, 7)
                    + dateLastModified.substring(8, 10) + " " + dateLastModified.substring(11, 13)
                    + ":" + dateLastModified.substring(14, 16) + ":" + dateLastModified.substring(17, 19);

            try {
                if (dateFormat.parse(dateLastModified).getTime() -
                        dateFormat.parse(lastUpdateTime).getTime() > 0) {
                    // Then there is something to download from the server
                    updateAvailable = true;
                } else {
                    // Then there is nothing to download from the server
                    updateAvailable = false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            dataDownload.setCheckedFromServer(true);
            dataDownload.setNeedToDownloadNewDataFromServer(updateAvailable);
            return updateAvailable;
        }

        // check if there is data to update
        private void checkRestaurantDataLastModified(Context context) {
            QueueRestaurantDate = Volley.newRequestQueue(context);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, restaurantURL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray restaurantJsonArray = response.getJSONObject("result").getJSONArray("resources");
                                JSONObject restaurantData = restaurantJsonArray.getJSONObject(0); // choose CSV
                                restaurantDataLastModified = restaurantData.getString("last_modified");

                                checkIfUpdateAvailable(restaurantDataLastModified);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            QueueRestaurantDate.add(request);
        }

        public void checkInspectionDataLastModified(Context context) {
            QueueInspectionDate = Volley.newRequestQueue(context);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, inspectionURL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray inspectionJsonArray = response.getJSONObject("result").getJSONArray("resources");
                                JSONObject inspectionData = inspectionJsonArray.getJSONObject(0); // choose CSV
                                inspectionDataLastModified = inspectionData.getString("last_modified");

                                checkIfUpdateAvailable(inspectionDataLastModified);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            QueueInspectionDate.add(request);
        }
    }

    private void UserCancelUpdate(){
        int temp;

        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_COPY, MODE_PRIVATE);
        temp = prefs.getInt(COPY, 0);
        temp -= 1;
        prefs = getSharedPreferences(SHARED_PREF_COPY, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(COPY, temp);
        editor.apply();
    }

    private class DownLoadDataTask extends AsyncTask<URL, Integer, Boolean>{
        private AlertDialog downloadingAlertDialog;
        private ProgressBar progressBar;
        private String restaurantCSVUrl = null;
        private String inspectionCSVUrl = null;

        void createDownloadingAlertDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.data_downloading_progress_bar, null);
            builder.setView(view)
                    .setCancelable(false)
                    .setTitle(R.string.downloading_data)
                    .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            UserCancelUpdate();
                        }
                    });

            downloadingAlertDialog = builder.create();
            progressBar = view.findViewById(R.id.progressBar);
            downloadingAlertDialog.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            createDownloadingAlertDialog();
        }

        @Override
        protected Boolean doInBackground(URL... urls) {
            String line;
            URL url;
            getRequestRestaurant(MapsActivity.this);
            getRequestInspection(MapsActivity.this);

            while(restaurantCSVUrl == null && inspectionCSVUrl == null){
            }

            publishProgress(4);

            // get shared preference
            SharedPreferences prefs = getSharedPreferences(SHARED_PREF_COPY, MODE_PRIVATE);
            copy = prefs.getInt(COPY, 0);
            if (copy == 0) {
                copy++;
            }

            try {
                url = new URL(restaurantCSVUrl);
                URLConnection connection = url.openConnection();

                String filename = "restaurant_copy_" + copy + ".csv";

                FileOutputStream fileOutputStream =
                        openFileOutput(filename, MODE_PRIVATE);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(connection.getInputStream()));

                while((line = reader.readLine()) != null){
                    if(isCancelled()){
                        publishProgress(RESULT_CANCELED);
                        return null;
                    }

                    outputStreamWriter.write(line + "\n");
                }
                reader.close();
                outputStreamWriter.flush();
                outputStreamWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            publishProgress(8);

            try {
                url = new URL(inspectionCSVUrl);
                URLConnection connection = url.openConnection();

                String filename = "inspection_copy_" + copy + ".csv";

                FileOutputStream fileOutputStream =
                        openFileOutput(filename, MODE_PRIVATE);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(connection.getInputStream()));

                publishProgress(10);
                while((line = reader.readLine()) != null){
                    if(isCancelled()){
                        publishProgress(RESULT_CANCELED);
                        return null;
                    }

                    outputStreamWriter.write(line + "\n");
                }
                reader.close();
                outputStreamWriter.flush();
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Update shared preference
            saveLastUpdateTime(); // save last update time

            prefs = getSharedPreferences(SHARED_PREF_COPY, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(COPY, copy);
            editor.apply();

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            // Read to data to Two Managers, clear them before read
            restaurantManager.resetAllRestaurants();
            inspectionManager.resetAllInspections();

            readRestaurantDataLocally(MapsActivity.this);
            readInspectionDataLocally(MapsActivity.this);

            //check if there is any update to the favourite restaurant
            // now all the inspection data have been read

            callFavouritesListActivity();

            downloadingAlertDialog.dismiss();
            refreshMaps();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0] * 10, true);
            if(values[0] == 10)
                downloadingAlertDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            downloadingAlertDialog.dismiss();
        }

        private void getRequestRestaurant(Context context) {
            RequestQueue queueRestaurant = Volley.newRequestQueue(context);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, restaurantURL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray restaurantJsonArray = response.getJSONObject("result").getJSONArray("resources");
                                JSONObject restaurantData = restaurantJsonArray.getJSONObject(0); // choose CSV
                                restaurantCSVUrl = restaurantData.getString("url");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            queueRestaurant.add(request);

        }

        private void getRequestInspection(Context context) {
            RequestQueue queueInspection = Volley.newRequestQueue(context);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, inspectionURL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray inspectionJsonArray = response.getJSONObject("result").getJSONArray("resources");

                                JSONObject inspectionData = inspectionJsonArray.getJSONObject(0); // choose CSV
                                inspectionCSVUrl = inspectionData.getString("url");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            queueInspection.add(request);
        }
    }

    // method to check if the app's last update time has passed 20 hours
    public boolean checkLastUpdate() {
        loadSavedTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date currentDate = new Date();
        long difference = 0;

        try {
            difference = currentDate.getTime() - dateFormat.parse(lastUpdateTime).getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot parse");
        }

        long diffDays = difference / (24 * 60 * 60 * 1000);
        long diffHours = difference / (60 * 60 * 1000) % 24;

        difference = (diffDays * 24) + diffHours;

        //if it's been 20 hours or more since data was last updated
        if (difference >= 20) {
            // check with the City of Surrey's server to see if there is more recent data to be downloaded.
            //"20 hours have passed"
            return true;
        }

        return false;

    }

    // Method to save the time when the data sync with the server happened
    public void saveLastUpdateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date currentDate = new Date();

        SharedPreferences prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TIME, dateFormat.format(currentDate));
        editor.apply();
    }

    public void loadSavedTime() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        lastUpdateTime = prefs.getString(TIME, "00000000 00:00:00");
    }

    public void readRestaurantDataLocally (Context context) {
        //reading text from file
        try {
            SharedPreferences prefs = getSharedPreferences(SHARED_PREF_COPY, MODE_PRIVATE);
            copy = prefs.getInt(COPY, 0);
            String filename = "restaurant_copy_" + copy + ".csv";

            FileInputStream fileIn = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fileIn, Charset.forName("UTF-8"))
            );

            String aLine;
            try {
                reader.readLine(); // read the header

                while( (aLine = reader.readLine()) != null ) {
                    // Split data by ','
                    String[] tokens = aLine.split(",");

                    // Read data
                    String aTrackingNumber = tokens[0].replaceAll("\"", "");
                    String aName = tokens[1].replaceAll("\"", "");
                    String anAddress = tokens[2].replaceAll("\"", "");
                    double latitude = Double.parseDouble(tokens[tokens.length - 2]);
                    double longitude = Double.parseDouble(tokens[tokens.length - 1]);

                    // create a restaurant object and add to the list
                    Restaurant aRestaurant = new Restaurant(
                            aTrackingNumber, aName, anAddress, latitude, longitude);
                    restaurantManager.addRestaurant(aRestaurant); // add into our list

                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Error reading");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readInspectionDataLocally(Context context) {
        try {
            SharedPreferences prefs = getSharedPreferences(SHARED_PREF_COPY, MODE_PRIVATE);
            copy = prefs.getInt(COPY, 0);
            String filename = "inspection_copy_" + copy + ".csv";

            FileInputStream fileIn = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fileIn, Charset.forName("UTF-8"))
            );

            String aLine = "";
            try {
                reader.readLine(); // read the header

                while( (aLine = reader.readLine()) != null ) {
                    // Split data by ','
                    String[] tokens = aLine.split(",");
                    String aTrackingNumber = "";
                    String anInspectionDate = "";
                    String anInspectionType = "";
                    String aHazardRating = "";
                    String aViolationLump = "";

                    int numCritical = 0;
                    int numNonCritical = 0;

                    int inspectionDate = 0;
                    int aDate = 0;
                    int aMonth = 0;
                    int aYear = 0;

                    if (tokens.length > 6) {
                        if (tokens[0].length() > 0) {
                            aTrackingNumber = tokens[0].replaceAll("\"", "");
                        }
                        if (tokens[1].length() > 0) {
                            anInspectionDate = tokens[1].replaceAll("\"", "");

                            inspectionDate = Integer.valueOf(anInspectionDate);
                            aDate = inspectionDate % 100;
                            aMonth = (inspectionDate/100)%100;
                            aYear = (inspectionDate/10000);
                        }
                        if (tokens[2].length() > 0) {
                            anInspectionType = tokens[2].replaceAll("\"", "");
                        }
                        if (tokens[3].length() > 0) {
                            numCritical = Integer.parseInt(tokens[3]);
                        }
                        if (tokens[4].length() > 0) {
                            numNonCritical = Integer.parseInt(tokens[4]);
                        }
                        if (tokens[tokens.length - 1].length() > 0) {
                            aHazardRating = tokens[tokens.length - 1].replaceAll("\"", "");
                        }
                    }

                    if ( (tokens.length > 6) && (tokens[5].length() > 0) ) {
                        aViolationLump = tokens[5].replaceAll("\"", "");
                        for (int i = 7; i < tokens.length; i++) {
                            aViolationLump = aViolationLump + ","
                                    + tokens[i-1].replaceAll("\"", "");
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

                    Inspection anInspection = new Inspection(
                            aTrackingNumber, anInspectionDate, aYear, aMonth, aDate,
                            anInspectionType, numCritical, numNonCritical,
                            aHazardRating, aViolationLump, violation, violationCode);
                    inspectionManager.add(anInspection);
                }


            } catch (IOException e) {
                throw new IllegalArgumentException("Error reading file");
            }


        } catch (Exception e) {
            e.printStackTrace();
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

        favouritesManager.initializeFavouriteRestaurantList();
    }

    private void callFavouritesListActivity() {
        if (favouritesManager.getFavouriteListSize() > 0) {
            // start fav list activity
            Intent intent = FavouritesListActivity.makeIntent(MapsActivity.this);
            startActivity(intent);
        }
    }
}
