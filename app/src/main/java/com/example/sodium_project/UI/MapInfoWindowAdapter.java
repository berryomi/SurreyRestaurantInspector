package com.example.sodium_project.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sodium_project.R;
import com.example.sodium_project.sodium_project.model.ClusterMarker;
import com.example.sodium_project.sodium_project.model.Inspection;
import com.example.sodium_project.sodium_project.model.Restaurant;
import com.example.sodium_project.sodium_project.model.RestaurantManager;
import com.example.sodium_project.util.IconForRestaurantLoader;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Class name: MapInfoWindowAdapter
 *
 * Description: An adapter for Google Map; will be used in MapsActivity.
 *
 */

public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View view;
    private MapsActivity mapsActivity;
    private IconForRestaurantLoader imageLoader = new IconForRestaurantLoader();

    public MapInfoWindowAdapter(Context context, MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
        this.view = LayoutInflater.from(context).inflate(R.layout.marker_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        ImageView imageView;
        TextView textView;
        ClusterMarker clickedClusterMarker = mapsActivity.getClickedClusterMarker();

        imageView = view.findViewById(R.id.infoWindowRestaurantPicture_imageView);
        textView = view.findViewById(R.id.infoWindowRestaurantName_textView);
        textView.setText(marker.getTitle());
        textView = view.findViewById(R.id.infoWindowAddress_textView);
        textView.setText(marker.getSnippet());
        textView = view.findViewById(R.id.infoWindowHazardLevelBody_textView);

        if(clickedClusterMarker != null){
            if(clickedClusterMarker.getHazardLevel() == null)
                textView.setText(mapsActivity.getString(R.string.NA));
            else
                textView.setText(clickedClusterMarker.getHazardLevel());

            int imageNum = imageLoader.loadImage(clickedClusterMarker.getRestaurant());
            imageView.setImageResource(imageNum);
        }
        else{
            RestaurantManager restaurantManager = RestaurantManager.getInstance();
            List<Restaurant> restaurantList = restaurantManager.getRestaurantList();
            String hazardRating = null;

            for (int i = 0; i < restaurantList.size(); i++){
                Restaurant restaurant = restaurantList.get(i);

                if(restaurant.getAddress().equals(marker.getSnippet())
                        && restaurant.getName().equals(marker.getTitle())){
                    Inspection inspection = restaurant.getLatestInspection(
                            restaurant.getTrackingNumber());

                    int imageNum = imageLoader.loadImage(restaurant);
                    imageView.setImageResource(imageNum);

                    if(inspection != null)
                        hazardRating = inspection.getHazardRating();
                    break;
                }
            }

            if(hazardRating == null)
                textView.setText(mapsActivity.getString(R.string.NA));
            else
                textView.setText(hazardRating);
        }
        return this.view;
    }
}
