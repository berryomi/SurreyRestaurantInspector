package com.example.sodium_project.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.media.audiofx.AudioEffect;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Printer;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.sodium_project.R;
import com.example.sodium_project.UI.IconRender;
import com.example.sodium_project.UI.MapsActivity;
import com.example.sodium_project.sodium_project.model.ClusterMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.Map;

/**
 * Class Name: MyClusterManagerRenderer
 *
 * Description: This class will be used in MapsActivity to
 *              manage and cluster the markers intelligently.
 */

public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker>{
    private static BitmapDescriptor redFace;
    private static BitmapDescriptor greenFace;
    private static BitmapDescriptor yellowFace;
    private static BitmapDescriptor questionFace;

    public MyClusterManagerRenderer(
            Context context, GoogleMap map,
            ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);

        IconRender iconRender = IconRender.getInstance(context);
        redFace = iconRender.getRedFace();
        greenFace = iconRender.getGreenFace();
        yellowFace = iconRender.getYellowFace();
        questionFace = iconRender.getQuestionFace();
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
        switch (item.getHazardLevelPicture()){
            case R.drawable.bigger_happy_smiley_pin:
                markerOptions.icon(greenFace);
                break;
            case R.drawable.bigger_neutral_smiley_pin:
                markerOptions.icon(yellowFace);
                break;
            case R.drawable.bigger_sad_smiley_pin:
                markerOptions.icon(redFace);
                break;
            default:
                markerOptions.icon(questionFace);
        }
        markerOptions.title(item.getTitle());
        markerOptions.snippet(item.getSnippet());
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1;
    }
}
