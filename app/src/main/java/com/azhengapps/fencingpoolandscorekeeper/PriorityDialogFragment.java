package com.azhengapps.fencingpoolandscorekeeper;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import androidx.appcompat.app.AlertDialog;

import com.azhengapps.fencingpoolandscorekeeper.data.Bout;

public class PriorityDialogFragment extends DialogFragment {
    private final CountDownTimer timer;
    private Random rand = new Random();
    private View priorityView;

    private Button positiveButton;

    public PriorityDialogFragment() {
        long time = 3 + rand.nextInt(8);
        timer = new CountDownTimer(1000 * time, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (priorityView != null) {
                    TextView left = priorityView.findViewById(R.id.priority_box_left);
                    TextView right = priorityView.findViewById(R.id.priority_box_right);
                    left.setVisibility(left.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
                    right.setVisibility(right.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
                }
            }

            @Override
            public void onFinish() {
                TextView selection = priorityView.findViewById(R.id.text_priority_selection);
                TextView left = priorityView.findViewById(R.id.priority_box_left);
                TextView right = priorityView.findViewById(R.id.priority_box_right);
                selection.setText(String.format(getString(R.string.text_priority_desc),
                        (left.getVisibility() == View.VISIBLE ? getString(R.string.text_left) : getString(R.string.text_right))));
                positiveButton.setEnabled(true);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setEnabled(false);
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_priority, null);
        TextView selection = view.findViewById(R.id.text_priority_selection);
        selection.setText(R.string.text_please_wait);

        builder.setView(view)
                .setTitle(R.string.title_priority)
                // Add action buttons
                .setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (view != null) {
                            TextView left = view.findViewById(R.id.priority_box_left);
                            Bout.Side result = left.getVisibility() == View.VISIBLE ? Bout.Side.LEFT : Bout.Side.RIGHT;

                            ((CreateRegBout)getActivity()).onSetPriority(result);
                        }
                    }
                });
        priorityView = view;
        timer.start();
        return builder.create();
    }
}