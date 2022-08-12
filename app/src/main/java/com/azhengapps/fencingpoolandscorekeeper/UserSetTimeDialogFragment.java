package com.azhengapps.fencingpoolandscorekeeper;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

public class UserSetTimeDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_user_settime, null);

        InputFilterMinMax minFilter = new InputFilterMinMax(0, 59);
        InputFilterMinMax secFilter = new InputFilterMinMax(0, 59);

        ((EditText) view.findViewById(R.id.input_minutes)).setFilters(new InputFilter[] { minFilter });
        ((EditText) view.findViewById(R.id.input_seconds)).setFilters(new InputFilter[] { secFilter });

        builder.setView(view)
                .setTitle(R.string.text_set_time)
                // Add action buttons
                .setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String minuteStr = ((EditText)getDialog().findViewById(R.id.input_minutes)).getText().toString();
                        String secondStr = ((EditText)getDialog().findViewById(R.id.input_seconds)).getText().toString();
                        int minutes = minuteStr.length() > 0 ? Integer.parseInt(minuteStr) : 0;
                        int seconds = secondStr.length() > 0 ? Integer.parseInt(secondStr) : 0;
                        ((CreateRegBout)getActivity()).onSetTime(minutes * 60 + seconds);
                    }
                });
        return builder.create();
    }
}
