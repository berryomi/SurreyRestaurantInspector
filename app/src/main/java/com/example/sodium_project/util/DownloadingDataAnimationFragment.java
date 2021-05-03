package com.example.sodium_project.util;

import androidx.appcompat.app.AppCompatDialogFragment;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sodium_project.R;
import com.example.sodium_project.UI.MapsActivity;
import com.example.sodium_project.sodium_project.model.DataDownload;
import com.example.sodium_project.sodium_project.model.InspectionManager;
import com.example.sodium_project.sodium_project.model.RestaurantManager;

// References: https://www.youtube.com/watch?v=2qkgqgeC5r4

/**
 * Class name: DownloadingDataAnimationFragment
 *
 * Description: A dialog box representing data downloading progress.
 *              After users agree to update data from database server, this dialog box will pop up,
 *              showing the download progress. After download finished, the dialog box will be
 *              dismiss, then bring users back to the map view. Users can choose to stop the
 *              download midway, in which case no new data will be updated, i.e. the app will use
 *              the previously downloaded data.
 */

public class DownloadingDataAnimationFragment extends AppCompatDialogFragment {

    private ProgressBar progressBarDownloadingData;
    private ObjectAnimator progressAnimator;
    private boolean isDismiss = false;

    private DataDownload download = DataDownload.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View aView = LayoutInflater.from(getActivity())
                .inflate(R.layout.download_data_dialog, null);
        final View finalView = aView;

        setupProgressBarAnimation(aView);

        progressAnimator.start();
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                // when downloading data "completed"
                progressBarDownloadingData.setVisibility(View.GONE);
                if (!isDismiss) {
                    download.setSuccess(true);

                    Toast.makeText(finalView.getContext(),
                            R.string.download_complete, Toast.LENGTH_SHORT).show();

                    download.setExecuting(false);

                    getDialog().dismiss();
                    isDismiss = true;
                }
            }
        });

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                download.setSuccess(false);
                download.setExecuting(false);

                Toast.makeText(getActivity(),
                                R.string.download_canceled, Toast.LENGTH_SHORT).show();
                if (!isDismiss) {
                    dismiss();
                    isDismiss = true;
                }

                // if user cancels download, go with previous data
                download.setSuccess(false);
                download.setExecuting(false);
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.downloading_data)
                .setView(aView)
                .setNegativeButton(R.string.cancel_downloading_data, listener)
                .create();
    }

    private void setupProgressBarAnimation(View aView) {
        progressBarDownloadingData = aView.findViewById(R.id.progressBarDownloadingData);
        progressAnimator = ObjectAnimator.ofInt(progressBarDownloadingData,
                            "progress", 0, 100);
        progressAnimator.setDuration(8000); //arbitrarily set duration to 8 seconds for now
    }
}
