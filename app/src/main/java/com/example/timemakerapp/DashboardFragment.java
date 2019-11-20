package com.example.timemakerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;


public class DashboardFragment extends Fragment {

    private CardView mDailyFocusCard;
    private RecyclerView mRecyclerView;
    private TextView mDailyFocus;
    private CheckBox mDailyTaskCheckbox;
    private PrevTaskListAdapter mAdapter;
    private final LinkedList<String> mPreviousTasks = new LinkedList<String>();

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Get list of previous tasks from database
        mPreviousTasks.add("Make storyboard");
        mPreviousTasks.add("Write report");

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final Context context = this.getContext();

        mRecyclerView = view.findViewById(R.id.recyclerview);
        mAdapter = new PrevTaskListAdapter(context, mPreviousTasks);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        mDailyFocus = view.findViewById(R.id.daily_focus_text);
        mDailyTaskCheckbox = view.findViewById(R.id.checkBox);
        mDailyTaskCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    new AlertDialog.Builder(context)
                            .setTitle("So close!")
                            .setMessage("Have you completed today's focus?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mDailyTaskCheckbox.setVisibility(View.INVISIBLE);
                                    mDailyFocus.setText(R.string.daily_task_achieved);
                                    Toast.makeText(context, "Nice job!", Toast.LENGTH_SHORT).show();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();

                    mDailyTaskCheckbox.setChecked(false);
                }
            }
        });


        //TODO: Get current task from database

        return view;
    }


}
