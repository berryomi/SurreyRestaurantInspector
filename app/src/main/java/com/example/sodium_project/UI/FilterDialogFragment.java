package com.example.sodium_project.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.sodium_project.R;
import com.example.sodium_project.sodium_project.model.SearchFilter;
import com.example.sodium_project.sodium_project.model.SearchFilterManager;

import java.util.Objects;

/**
 * Class name: FilterDialogFragment
 *
 * Description: A Filter Dialog Fragment that will ask users to choose the constraints of the
 *              restaurants based on their preferences, then show the list view and map markers
 *              showing the corresponding restaurants.
 *
 */
public class FilterDialogFragment extends DialogFragment {
    private myFilterDialogListener myFilterDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.filter_dialog, null);
        final Spinner spinner = view.findViewById(R.id.filterHazardLevel_spinner);
        final EditText editTextLessThanOrEqual = view.findViewById(R.id.lessThanOrEqual_editText);
        final EditText editTextGreaterThanOrEqual = view.findViewById(R.id.greaterThanOrEqual_editText);
        final Switch mySwitch = view.findViewById(R.id.myFavourite_switch);

        mySwitch.setChecked(SearchFilterManager.isInputFavourites);

        if(SearchFilterManager.inputHazardLevel != null){
            switch (SearchFilterManager.inputHazardLevel){
                case "Low":
                    spinner.setSelection(1);
                    break;
                case "Moderate":
                    spinner.setSelection(2);
                    break;
                case "High":
                    spinner.setSelection(3);
                    break;
                default:
                    spinner.setSelection(0);
            }
        }

        if(SearchFilterManager.inputLessThanCriticalViolations == -1)
            editTextLessThanOrEqual.setText(null);
        else
            editTextLessThanOrEqual.setText(
                    String.valueOf(SearchFilterManager.inputLessThanCriticalViolations));

        if(SearchFilterManager.inputGreaterThanCriticalViolations == -1)
            editTextGreaterThanOrEqual.setText(null);
        else
            editTextGreaterThanOrEqual.setText(
                    String.valueOf(SearchFilterManager.inputGreaterThanCriticalViolations));


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = spinner.getSelectedItemPosition();

                if(position != 0){
                    switch (position){
                        case 1:
                            SearchFilterManager.inputHazardLevel = "Low";
                            break;
                        case 2:
                            SearchFilterManager.inputHazardLevel = "Moderate";
                            break;
                        case 3:
                            SearchFilterManager.inputHazardLevel = "High";
                            break;
                    }
                }
                else
                    SearchFilterManager.inputHazardLevel = "-1";

                if(!String.valueOf(editTextLessThanOrEqual.getText()).equals(""))
                    SearchFilterManager.inputLessThanCriticalViolations =
                            Integer.parseInt(String.valueOf(editTextLessThanOrEqual.getText()));
                else
                    SearchFilterManager.inputLessThanCriticalViolations = -1;

                if(!String.valueOf(editTextGreaterThanOrEqual.getText()).equals(""))
                    SearchFilterManager.inputGreaterThanCriticalViolations =
                            Integer.parseInt(String.valueOf(editTextGreaterThanOrEqual.getText()));
                else
                    SearchFilterManager.inputGreaterThanCriticalViolations = -1;

                SearchFilterManager.isInputFavourites = mySwitch.isChecked();

                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        getString(R.string.filter_applied), Toast.LENGTH_SHORT).show();

                SearchFilter searchFilter = new SearchFilter();

                myFilterDialogListener.onOkClicked();

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton(R.string.clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SearchFilterManager.inputLessThanCriticalViolations = -1;
                SearchFilterManager.inputGreaterThanCriticalViolations = -1;
                SearchFilterManager.inputHazardLevel = "-1";
                SearchFilterManager.isInputFavourites = false;
                SearchFilterManager.inputNameOfRestaurant = "-1";
                SearchFilter searchFilter = new SearchFilter();
                myFilterDialogListener.onOkClicked();
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),
                        getString(R.string.filter_cleared), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    public interface myFilterDialogListener {
        void onOkClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            myFilterDialogListener = (myFilterDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + "must implement filterDialogListener");
        }
    }
}

