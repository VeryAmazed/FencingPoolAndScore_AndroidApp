package com.azhengapps.fencingpoolandscorekeeper;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class CardDialogFragment extends DialogFragment {

    public enum Card {
        YELLOW,
        RED,
        BLACK,
        P_YELLOW,
        P_RED,
        P_BLACK
    };

    private Card card;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        if (getArguments() != null) {
            int cardValue = getArguments().getInt("Card");
            card = Card.values()[cardValue];
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_card, null);
        int color = 0;
        if (card.equals(Card.YELLOW)) {
            color = getResources().getColor(R.color.yellow_card);
        } else if (card.equals(Card.RED)) {
            color = getResources().getColor(R.color.red_card);
        } else if (card.equals(Card.BLACK)) {
            color = getResources().getColor(R.color.black);
        } else if (card.equals(Card.P_YELLOW)) {
            // P yellow card
            color = getResources().getColor(R.color.yellow_card);
            TextView textView = view.findViewById(R.id.id_card);
            textView.setText("P");
            textView.setGravity(Gravity.CENTER);
        } else if (card.equals(Card.P_RED)) {
            // P red card
            color = getResources().getColor(R.color.red_card);
            TextView textView = view.findViewById(R.id.id_card);
            textView.setText("P");
            textView.setGravity(Gravity.CENTER);
        } else if (card.equals(Card.P_BLACK)) {
            // P red card
            color = getResources().getColor(R.color.black);
            TextView textView = view.findViewById(R.id.id_card);
            textView.setText("P");
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.colorAccentLight));
        }
        view.setBackgroundColor(color);

        builder.setView(view);
        return builder.create();
    }
}