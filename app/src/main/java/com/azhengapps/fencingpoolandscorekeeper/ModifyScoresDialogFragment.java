package com.azhengapps.fencingpoolandscorekeeper;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.azhengapps.fencingpoolandscorekeeper.data.PoolFormat;
import com.azhengapps.fencingpoolandscorekeeper.data.PoolViewModel;

public class ModifyScoresDialogFragment extends DialogFragment {

    private EditText editRight;
    private EditText editLeft;
    private CheckBox checkboxRight;
    private CheckBox checkboxLeft;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_mod_score, null);

        final PoolViewModel.BoutResult boutResult = ((PoolActivity)getActivity()).getPoolViewModel().getBoutResult(((PoolActivity)getActivity()).getBoutNo());
        int[] bout = boutResult.getScores();

        InputFilterMinMax minMaxFilter = new InputFilterMinMax(0, ((PoolActivity)getActivity()).getPoolViewModel().getMaxScore());

        this.editRight = (EditText) view.findViewById(R.id.mod_right_score);
        this.editLeft = (EditText) view.findViewById(R.id.mod_left_score);
        this.editRight.setFilters(new InputFilter[] { minMaxFilter });
        this.editLeft.setFilters(new InputFilter[] { minMaxFilter });

        ((EditText) view.findViewById(R.id.mod_right_score)).setText(Integer.toString(bout[0]));
        ((EditText) view.findViewById(R.id.mod_left_score)).setText(Integer.toString(bout[1]));
        PoolFormat boutOrder = new PoolFormat();
        int[] fencers = boutOrder.getPoolBoutsOrderMap().get(((PoolActivity)getActivity()).getPoolViewModel().getSize()).get(((PoolActivity)getActivity()).getBoutNo() - 1);
        ((TextView) view.findViewById(R.id.fencer_right)).setText("Fencer " + fencers[0] + ": ");
        ((TextView) view.findViewById(R.id.fencer_left)).setText("Fencer " + fencers[1] + ": ");

        this.checkboxRight = (CheckBox)view.findViewById(R.id.checkbox_victor_right);
        this.checkboxLeft = (CheckBox)view.findViewById(R.id.checkbox_victor_left);
        checkboxRight.setChecked(boutResult.isVictorRight());
        checkboxRight.setOnCheckedChangeListener((buttonView, isChecked) -> checkboxLeft.setChecked(!isChecked));
        checkboxLeft.setChecked(!boutResult.isVictorRight());
        checkboxLeft.setOnCheckedChangeListener((buttonView, isChecked) -> checkboxRight.setChecked(!isChecked));
        if (bout[0] == bout[1]) {
            checkboxRight.setEnabled(false);
            checkboxLeft.setEnabled(false);
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateState();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
        editRight.addTextChangedListener(textWatcher);
        editLeft.addTextChangedListener(textWatcher);

        updateState();

        builder.setView(view)
                .setTitle(R.string.text_change_scores)
                
                // Add action buttons
                .setPositiveButton("Save", (dialog, id) -> {
                    String leftStr = ((EditText)getDialog().findViewById(R.id.mod_left_score)).getText().toString();
                    String rightStr = ((EditText)getDialog().findViewById(R.id.mod_right_score)).getText().toString();
                    int left = leftStr.length() > 0 ? Integer.parseInt(leftStr) : 0;
                    int right = rightStr.length() > 0 ? Integer.parseInt(rightStr) : 0;
                    boolean victorRight = right == left ? checkboxRight.isChecked() : right > left;
                    ((PoolActivity)getActivity()).getPoolViewModel().setBoutResult(((PoolActivity)getActivity()).getBoutNo(), right, left, victorRight);
                    ((PoolActivity)getActivity()).init();
                });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            return;
        });
        return builder.create();
    }

    private void updateState() {
        String leftStr = editLeft.getText().toString();
        String rightStr = editRight.getText().toString();
        if (leftStr.length() > 0 && rightStr.length() > 0) {
            int left = Integer.parseInt(leftStr);
            int right = Integer.parseInt(rightStr);
            checkboxRight.setEnabled(left == right);
            checkboxLeft.setEnabled(left == right);
            if (right > left) {
                checkboxRight.setChecked(true);
                checkboxLeft.setChecked(false);
            } else if (left > right) {
                checkboxLeft.setChecked(true);
                checkboxRight.setChecked(false);
            }
        }
    }
}
