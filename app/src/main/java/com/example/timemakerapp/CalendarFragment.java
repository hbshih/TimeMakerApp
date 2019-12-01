package com.example.timemakerapp;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.annotation.Nonnull;

import android.graphics.Color;


import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.example.timemakerapp.Utils.DrawableUtils;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.utils.DateUtils;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;



/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    ListView listview;
    Context mC;
    private DatabaseReference myRef = database.getReference().child("achievements");

    //Connection to Firebase Firestore to get achievements datas
    private void getAchievementsItems() {

        FirebaseFirestore.getInstance()
                .collection(
                        "users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                            System.out.println("OnSuccess : " + myListOfDocuments);
                        }else
                        {
                            System.out.println("Query Failed");
                        }
                    }
                });

    }

    //Connectionn to Firebase Database
    private void readRealtimeDatabaseValue(){

        System.out.println("Check Database Value");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                System.out.println("Realtime Database Value is " + value);
                // Log.d(, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Database Error " + error);
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }




    public CalendarFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar,container,false);

        List<EventDay> events = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, R.drawable.sample_three_icons));
//
 //       events.add(new EventDay(calendar, new Drawable()));
//or if you want to specify event label color
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

        CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendarView);

        Calendar min = Calendar.getInstance();
        min.add(Calendar.MONTH, -2);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 2);

        calendarView.setMinimumDate(min);
        calendarView.setMaximumDate(max);

        calendarView.setEvents(events);

        return view;
    }

}
