package com.example.timemakerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.annotation.Nonnull;


public class DashboardFragment extends Fragment {

    private CardView mDailyFocusCard;
    private RecyclerView mRecyclerView;
    private TextView mDailyFocus;
    private CheckBox mDailyTaskCheckbox;
    private PrevTaskListAdapter mAdapter;
    private final LinkedList<DailyTask> mPreviousTasks = new LinkedList<DailyTask>();
    private DailyTask currentTask;

    private FirebaseFirestore db;
    private String currentUser;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("userid", currentUser);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        updateUserAchivementsInfo();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final Context context = this.getContext();

        mDailyFocusCard = view.findViewById(R.id.daily_focus_card);
        mRecyclerView = view.findViewById(R.id.recyclerview);
        mDailyFocus = view.findViewById(R.id.daily_focus_text);
        mDailyTaskCheckbox = view.findViewById(R.id.checkBox);
        final Button mDailyTaskButton = view.findViewById(R.id.button);

        db.collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                Map<String,Object> taskMap = doc.getData();
                                String taskUser = (String) taskMap.get("userId");

                                if (!currentUser.equals(taskUser)){
                                    continue;
                                }
                                String taskName = (String) taskMap.get("name");
                                Date taskTime = ((Timestamp) taskMap.get("time")).toDate();
                                boolean isAchieved = (boolean) taskMap.get("achieved");
                                DailyTask dailyTask = new DailyTask(taskName, taskTime, isAchieved, currentUser);
                                dailyTask.setId(doc.getId());

                                if (DateUtils.isToday(dailyTask.getTime().getTime())){
                                    currentTask = dailyTask;
                                } else if (!dailyTask.isAchieved()) {
                                    mPreviousTasks.add(dailyTask);
                                }
                            }

                            Log.d("TaskRead" ,"number of prev tasks: " + mPreviousTasks.size());

                            mAdapter = new PrevTaskListAdapter(context, mPreviousTasks, currentTask);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                            mRecyclerView.setAdapter(mAdapter);

                            if (currentTask != null){
                                setDailyTaskLayout(currentTask);
                                if (currentTask.isAchieved()){
                                    mDailyTaskCheckbox.setVisibility(View.INVISIBLE);
                                    mDailyFocus.setText(R.string.daily_task_achieved);
                                }
                            }

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

                                                        if (mAdapter.getCurrentTask() !=  null){
                                                            currentTask = mAdapter.getCurrentTask();
                                                        }

                                                        db.collection("tasks").document(currentTask.getId())
                                                                .update("achieved", true);
                                                        updateUserAchivementsInfo();
                                                    }})
                                                .setNegativeButton(android.R.string.no, null).show();

                                        mDailyTaskCheckbox.setChecked(false);
                                    }
                                }
                            });

                            mDailyTaskButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    createNewTask(v);
                                }
                            });

                        }
                        else {
                            Log.d("TaskRead", "DailyTask failed reading");
                        }
                    }
                });

        return view;
    }

    private void updateUserAchivementsInfo()
    {
        Date today = new Date();
        DocumentReference docRef = db.collection("user_achievements").document(currentUser);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object>  myListOfDocuments = task.getResult().getData();
                    if (myListOfDocuments != null) {
                        //Map<String, Object>  myListOfDocuments = task.getResult().getData();
                        System.out.println("OnSuccess : " + myListOfDocuments);

                        Map<String, Object> docData = new HashMap<>();
                        docData.put("day_streak", 0);
                        docData.put("last_completed_goal_date", today);

                        String last_updated_date = myListOfDocuments.get("last_completed_goal_date").toString();

/*
                        Date today = new Date();


                        LocalDate d1 = LocalDate.parse(last_updated_date, DateTimeFormatter.ISO_LOCAL_DATE);
                        LocalDate d2 = LocalDate.parse(today.toString(), DateTimeFormatter.ISO_LOCAL_DATE);
                        Duration diff = Duration.between(d1.atStartOfDay(), d2.atStartOfDay());
                        long diffDays = diff.toDays();*/

                        System.out.println("Date: " + diffDays);


                        int current_number_of_task = Integer.parseInt(myListOfDocuments.get("number_of_completed_tasks").toString());
                        docData.put("number_of_completed_tasks", current_number_of_task + 1);
                        db.collection("user_achievements").document(currentUser).update(docData);

                    } else {
                        // Create new user data
                        Map<String, Object> docData = new HashMap<>();
                        docData.put("day_streak", 0);
                        docData.put("last_completed_goal_date", today);
                        docData.put("number_of_completed_tasks", 0);
                        db.collection("user_achievements").document(currentUser).set(docData);
                        System.out.println("Query Failed - Created new user");
                    }
                } else {
                    System.out.println("Error");
                   // Log.d(TAG, "get failed with ", task.getException());
                    System.out.println(task.getException());
                }
            }
        });
    }

    private void createNewTask(final View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle(R.string.pickFocusTitle)
                .setMessage(R.string.pickTaskConfirmation)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        EditText enterFocus = mDailyFocusCard.findViewById(R.id.editText);
                        String newTaskName = enterFocus.getText().toString();
                        closeKeyboard();

                        Log.d("userid new task", currentUser);
                        DailyTask newTask = new DailyTask(newTaskName, false, currentUser);
                        setDailyTaskLayout(newTask);

                        db.collection("tasks").document()
                                .set(newTask).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("TaskInsert", "DailyTask successfully inserted");
                                } else {
                                    Log.d("TaskInsert", "DailyTask failed insertion");
                                }
                            }
                        });
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }


    private void setDailyTaskLayout(DailyTask task){
        TextView dailyFocus = mDailyFocusCard.findViewById(R.id.daily_focus_text);
        CheckBox focusCheckbox = mDailyFocusCard.findViewById(R.id.checkBox);
        EditText enterFocus = mDailyFocusCard.findViewById(R.id.editText);
        Button focusButton = mDailyFocusCard.findViewById(R.id.button);

        enterFocus.setVisibility(View.INVISIBLE);
        focusButton.setVisibility(View.INVISIBLE);

        dailyFocus.setText(task.getName());
        dailyFocus.setVisibility(View.VISIBLE);
        focusCheckbox.setVisibility(View.VISIBLE);

        if (mPreviousTasks.size() > 0) {
            mAdapter.setCurrentTask(task);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void closeKeyboard() {
        View view = this.getActivity().getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
