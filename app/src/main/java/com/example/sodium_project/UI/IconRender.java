package com.example.sodium_project.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.sodium_project.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;


/**
 * Pre render icons since Bitmap can dramatically influence performance if we used too often
 * The following code can probably be improved using a Map. Since there are only 4 icons in
 * total, I just used copy and paste
 */

public class IconRender {
    private static IconRender instance;
    private static BitmapDescriptor greenFace;
    private static BitmapDescriptor yellowFace;
    private static BitmapDescriptor redFace;
    private static BitmapDescriptor questionFace;

    private IconRender(Context context) {
        generateIcons(context);
    }

    public static IconRender getInstance(Context context) {
        if(instance == null)
            instance = new IconRender(context);

        return instance;
    }

    private void generateIcons(Context context){
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.bigger_happy_smiley_pin);
        icon = Bitmap.createScaledBitmap(icon, 84, 113, false);
        greenFace = BitmapDescriptorFactory.fromBitmap(icon);

        icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.bigger_neutral_smiley_pin);
        icon = Bitmap.createScaledBitmap(icon, 84, 113, false);
        yellowFace = BitmapDescriptorFactory.fromBitmap(icon);

        icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.bigger_sad_smiley_pin);
        icon = Bitmap.createScaledBitmap(icon, 84, 113, false);
        redFace = BitmapDescriptorFactory.fromBitmap(icon);

        icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.bigger_noinspection_smiley_pin);
        icon = Bitmap.createScaledBitmap(icon, 84, 113, false);
        questionFace = BitmapDescriptorFactory.fromBitmap(icon);
    }

    public BitmapDescriptor getGreenFace() {
        return greenFace;
    }

    public BitmapDescriptor getYellowFace() {
        return yellowFace;
    }

    public BitmapDescriptor getRedFace() {
        return redFace;
    }

    public BitmapDescriptor getQuestionFace() {
        return questionFace;
    }
}
