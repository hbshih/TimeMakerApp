package com.example.timemakerapp;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    private FirebaseFirestore db;
    private String currentUser;

    private LinkedList<DailyTask> userTasks = new LinkedList<DailyTask>();
    private int total_achieved = 0;
    private int total_failed = 0;

    private CalendarView calendarView;
    private TextView failedTasksTextView, achievedTasksTextView;
    private CardView c_dayInfo;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        achievedTasksTextView = view.findViewById(R.id.goalsAchievedNumber);
        failedTasksTextView = view.findViewById(R.id.goalsFailedNumber);
        c_dayInfo = view.findViewById(R.id.c_dayInfo);
        calendarView = view.findViewById(R.id.calendarView);

        getUserTasks();

        createDayListener(view);

        return view;
    }

    private void createDayListener(View view) {
        calendarView.setOnDayClickListener(eventDay -> {
            this.c_dayInfo.setVisibility(View.VISIBLE);
        });
    }

    private void setupCalendar() {
        List<EventDay> events = new ArrayList<>();

        for (DailyTask task : userTasks) {
            Date taskDate = task.getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(taskDate);
            Log.d("Event Time",calendar.get(Calendar.YEAR) + "YY" + calendar.get(Calendar.MONTH) + "MM" + calendar.get(Calendar.DAY_OF_MONTH));

            if (task.isAchieved()){
                events.add(new EventDay(calendar, R.drawable.sample_icon_2));
            }
            else{
                events.add(new EventDay(calendar, R.drawable.sample_icon_3));
            }

            /*
            Calendar calendar = Calendar.getInstance();
            events.add(new EventDay(calendar, R.drawable.sample_three_icons));
            events.add(new EventDay(calendar, R.drawable.sample_three_icons, Color.parseColor("#228B22")));

            Calendar calendar1 = Calendar.getInstance();
            calendar1.add(Calendar.DAY_OF_MONTH, 10);
            events.add(new EventDay(calendar1, R.drawable.sample_icon_2));

            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(Calendar.DAY_OF_MONTH, 5);
            events.add(new EventDay(calendar2, R.drawable.sample_icon_3));

            Calendar calendar3 = Calendar.getInstance();
            calendar3.add(Calendar.DAY_OF_MONTH, 7);
            events.add(new EventDay(calendar3, R.drawable.sample_four_icons));
            */
        }
        calendarView.setEvents(events);
    }


    //Connection to Firebase Firestore to get task data
    private void getUserTasks() {

        db.collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Map<String, Object> taskMap = doc.getData();
                                String taskUser = (String) taskMap.get("userId");

                                if (!currentUser.equals(taskUser)) {
                                    continue;
                                }
                                String taskName = (String) taskMap.get("name");
                                Date taskTime = ((Timestamp) taskMap.get("time")).toDate();
                                boolean isAchieved = (boolean) taskMap.get("achieved");
                                DailyTask dailyTask = new DailyTask(taskName, taskTime, isAchieved, currentUser);
                                dailyTask.setId(doc.getId());

                                userTasks.add(dailyTask);

                                if (isAchieved) {
                                    total_achieved++;
                                } else {
                                    total_failed++;
                                }
                            }

                            achievedTasksTextView.setText(Integer.toString(total_achieved));
                            failedTasksTextView.setText(Integer.toString(total_failed));

                            setupCalendar();

                        } else {
                            Log.d("loading calendar tasks", task.getException().getMessage());
                        }
                    }
                });
    }


}
