package com.azhengapps.fencingpoolandscorekeeper;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import com.azhengapps.fencingpoolandscorekeeper.data.PoolFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewPoolDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_new_pool, null);
        List<Integer> items = new ArrayList<>(PoolFormat.getPoolBoutsOrderMap().keySet());
        Collections.sort(items);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        ((Spinner) view.findViewById(R.id.spinner_poolsize)).setAdapter(adapter);

        InputFilterMinMax scoresFilter = new InputFilterMinMax(1, 99);
        ((EditText) view.findViewById(R.id.text_max_scores)).setFilters(new InputFilter[] { scoresFilter });

        builder.setView(view)
                .setTitle(R.string.title_newpool)
                // Add action buttons
                .setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = ((EditText)getDialog().findViewById(R.id.text_poolname)).getText().toString();
                        if (name.length() == 0) {
                            name =  getResources().getString(R.string.text_pool);
                        }
                        int size = (Integer)((Spinner) getDialog().findViewById(R.id.spinner_poolsize)).getSelectedItem();
                        String scoreStr = ((EditText)getDialog().findViewById(R.id.text_max_scores)).getText().toString();

                        final int maxScores = scoreStr == null || scoreStr.length() == 0 ? Constants.DEFAULT_POOL_MAX_SCORE :  Integer.parseInt(scoreStr);
                        ((PoolActivity)getActivity()).initializeNewPool(name, size, maxScores);
                    }
                });
        return builder.create();
    }
}
