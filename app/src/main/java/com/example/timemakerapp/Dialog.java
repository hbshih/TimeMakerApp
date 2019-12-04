package com.example.timemakerapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDialogFragment;

public class Dialog extends AppCompatDialogFragment {

    private String dayGoal;
    private String title = "Day achievement";

    // Dialog for calendar
    public Dialog(String dayGoal) {
        this.dayGoal = dayGoal;
    }
    String TAG = "Dialog";
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "Dialog java class");
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(this.title).setMessage(this.dayGoal)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "Dialog closed");
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
