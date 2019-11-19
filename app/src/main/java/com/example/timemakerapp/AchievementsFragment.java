package com.example.timemakerapp;


import android.content.Context;
import android.os.Bundle;

import android.os.Handler;
import android.text.Layout;
import android.widget.*;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.*;


import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.timemakerapp.R.layout.achievements_row;


/**
 * A simple {@link Fragment} subclass.
 */
public class AchievementsFragment extends Fragment {

    ListView listview;
    Context mC;
    String mTitle[] = {"First Use", "3 Days in a Row", "Completed 10 Goals", "a","b", "c", "d","a","b", "c", "d"};
    String mDescription[] = {"Complete your first goal", "....bar...", "...bar...",  "....bar...", "...bar...", "....bar...", "...bar...", "...bar...", "....bar...", "...bar..."};

    int images [] = {R.drawable.achievements_firstprice,R.drawable.achievements_firstprice,R.drawable.achievements_firstprice, R.drawable.achievements_firstprice,R.drawable.achievements_firstprice,R.drawable.achievements_firstprice,R.drawable.achievements_firstprice,R.drawable.achievements_firstprice,R.drawable.achievements_firstprice,R.drawable.achievements_firstprice};
    int pgsMax[] = {20 , 7 , 13,1,1,1,1,1,1,1};


    private DatabaseReference mDatabase;

    /* Connection to Firebase Firestore to get achievements datas
    private void getAchievementsItems() {




        FirebaseFirestore.getInstance()
                .collection("users")
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

    }*/


    public AchievementsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View fragView = inflater.inflate(R.layout.fragment_achievements, container, false);

        listview =(ListView) fragView.findViewById(R.id.listview);
        MyAdapter adapter = new MyAdapter(getActivity(), mTitle, mDescription, images);
        listview.setAdapter(adapter);

       // getAchievementsItems();
        return fragView;
    }



    class MyAdapter extends ArrayAdapter<String>{

        private LayoutInflater mInflater;
        private Context context;
        private String rTitle[];
        private String rDescription[];
        private int rImgs[];
        private int progressStatus = 0;

        private class ProgressHolder{
            TextView textview;
            ProgressBar pgsBar;
        }

        MyAdapter(Context c, String title[], String description[], int imgs[])
        {
            super(c, R.layout.achievements_row, R.id.textview1, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImgs = imgs;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //set progressholder to null every time
            ProgressHolder pgsHolder = null;

            if (convertView == null){
                convertView = mInflater.inflate(achievements_row, null);
            }
            //set id, position of image title, descriptiom, progressbar
            ImageView images = convertView.findViewById(R.id.image1);
            TextView myTitle = convertView.findViewById(R.id.textview1);
            TextView myDescription = convertView.findViewById(R.id.textview2);
            pgsHolder = new ProgressHolder();
            pgsHolder.textview = convertView.findViewById(R.id.textview3);
            pgsHolder.pgsBar = convertView.findViewById(R.id.pBar);

            images.setImageResource(rImgs[position]);
            myTitle.setText(rTitle[position]);
            myDescription.setText(rDescription[position]);
            pgsHolder.pgsBar.setMax(pgsMax[position]);

            //progress bar
            setpgsBar(pgsHolder);
            return convertView;
        }

        public void setpgsBar(ProgressHolder pHolder){
            int i = pHolder.pgsBar.getProgress();
            i+=1;
            pHolder.pgsBar.setProgress(i);
            pHolder.textview.setText(i+"/"+pHolder.pgsBar.getMax());
        }
    }

}
