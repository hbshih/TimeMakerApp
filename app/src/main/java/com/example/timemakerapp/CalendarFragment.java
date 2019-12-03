package com.example.timemakerapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import android.graphics.Color;


import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.widget.Button;
import android.widget.Toast;

import com.example.timemakerapp.Utils.DrawableUtils;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;



/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    private FirebaseFirestore db;
    private String currentUser;
    private DailyTask currentTask;
    private final LinkedList<DailyTask> allTasks = new LinkedList<DailyTask>();

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get db instance
        db = FirebaseFirestore.getInstance();
        //get current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("userid", currentUser);


        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        List<EventDay> events = new ArrayList<>();

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

                                allTasks.add(dailyTask);

                                Log.d("TaskRead", "TASK READ SUCCESS");
                                Log.d("TaskRead", taskName);


                            }
                            //putting in events
                            for(int num=0; num<allTasks.size(); num++)
                            {
                                DailyTask taskEvent = getTasks(allTasks,num);
                                if (taskEvent.isAchieved() == true){
                                    addSuccessEvents(events,taskEvent.getTime());
                                    System.out.println(taskEvent.getTime());
                                    System.out.println("success added");

                                }
                                else
                                {
                                    addFailedEvents(events,taskEvent.getTime());
                                    System.out.println("failed added");
                                }
                            }

                            CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendarView);
                            calendarView.setEvents(events);

                        }

                        else {
                            Log.d("TaskRead", "DailyTask failed reading");
                        }
                    }
                });


        return view;
    }

    private void addSuccessEvents(List<EventDay> events, Date date) {

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(date.getYear()+1900,date.getMonth(),date.getDate());
        events.add(new EventDay(calendar1, R.drawable.sample_icon_2));
        System.out.println("success added, add to success void");
        System.out.println("in success void"+ (date.getYear()+1900)+ "YY" + date.getMonth() + "MM" + date.getDate());



    }

    private void addFailedEvents(List<EventDay> events,Date date) {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(date.getYear()+1900,date.getMonth(),date.getDate());
        events.add(new EventDay(calendar2, R.drawable.sample_icon_3));
        System.out.println("success added, add to failed void");
        System.out.println("in failed void"+ (date.getYear()+1900)+ "YY" + date.getMonth() + "MM" + date.getDate());

    }

    private DailyTask getTasks(LinkedList<DailyTask> TaskList, int num){
        DailyTask task = TaskList.get(num);
        return task;
    }

}
