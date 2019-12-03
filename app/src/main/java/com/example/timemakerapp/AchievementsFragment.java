package com.example.timemakerapp;


import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;

import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.widget.*;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;
import com.google.firebase.firestore.*;
import com.google.firebase.auth.FirebaseAuth;


import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static com.example.timemakerapp.R.layout.achievements_row;


/**
 * A simple {@link Fragment} subclass.
 */
public class AchievementsFragment extends Fragment {

    ListView listview;
    Context mC;
    String mTitle[] = {"First Use", "3 Days in a Row", "Completed 10 Goals", "Perfect Week", "Completed 100 Goals"};
    // String mDescription[] = {"Completed your first goal", "Completed 3 daily goals consecutively", "", "Finished all goals in a week","", "", "....bar...", "...bar..."};
    int images [] = {R.drawable.achievements_firstprice,R.drawable.achievements_firstprice,R.drawable.achievements_firstprice,R.drawable.achievements_firstprice,R.drawable.achievements_firstprice,R.drawable.achievements_firstprice,R.drawable.achievements_firstprice,R.drawable.achievements_firstprice};
    //int pgsMax[] = {20,7,13};
    //int pgsVisible[] = {0,1,1};
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("achievements");
    List<AchievementsItem> Achieves;
    private String currentUser;


    private DatabaseReference mDatabase;
    // class for achievements from firebase
    private class AchievementsItem {
        public AchievementsItem(String t,String s, int m , int o){
            this.title = t; this.subtitle = s; this.max = m; this.order = o;
        }
        public String title;
        public String subtitle;
        public int max;
        public int order;
    }
    //Connection to Firebase Firestore to get achievements datas
    private void getAchievementsItems() {


        System.out.println("Get Achievements...");
        //final List<AchievementsItem> Achieves = new ArrayList<>();
        FirebaseFirestore.getInstance()
                .collection(
                        "achievements")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //  List<DocumentSnapshot> myListOfDocuments = task.getResult();
                            //System.out.println("OnSuccess : " + myListOfDocuments);
                            currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            insertNewUser(task, currentUser);
                            Achieves = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                Map<String,Object> taskMap = doc.getData();
                                String title = (String) taskMap.get("title");
                                String subtitle = (String) taskMap.get("subtitle");
                                int  max = ((Number)taskMap.get("max")).intValue();
                                //int order = ((Number)taskMap.get("order")).intValue();
                                //System.out.println("Read Achievements : " +title+" "+max+" "+order+" ");
                                //Achieves.add(new AchievementsItem(title, subtitle , max , order));
                            }

                            //for(AchievementsItem i :Achieves){System.out.println("Read Achievements : " +i.title);}
                            //MyAdapter adapter = new MyAdapter(getActivity(), mTitle, images, Achieves);
                            //listview.setAdapter(adapter);

                        }else
                        {
                            System.out.println("Query Failed");
                        }
                    }
                });


    }

    private void insertNewUser(Task<QuerySnapshot> task, String currentUser){
        //check new user
        boolean NewUser = true;
        for (QueryDocumentSnapshot doc : task.getResult()){
            Map<String,Object> taskMap = doc.getData();
            Map<String,Object> order =  (Map<String,Object> )taskMap.get("order");
            for (Map.Entry<String, Object> entry : order.entrySet()) {
                String k = entry.getKey();
                System.out.println("OnSuccess : " + currentUser);
                if(k == currentUser){
                    NewUser = false;
                    break;
                }
            }
            break;

        }

        if(NewUser){
            Map<String, Object> newOrder = new HashMap<>();
            Map<String, Object> newOrders = new HashMap<>();
            newOrder.put(currentUser, 0);
            newOrders.put("order", newOrder);

            for (QueryDocumentSnapshot doc : task.getResult()) {
                FirebaseFirestore.getInstance().collection("achievements").document(doc.getId()).set(newOrders, SetOptions.merge());
            }
        }
    }
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

        //get firestore data
        getAchievementsItems();
        //MyAdapter adapter = new MyAdapter(getActivity(), mTitle, mDescription, images, Achieves);
        //listview.setAdapter(adapter);



        return fragView;
    }



    class MyAdapter extends ArrayAdapter<String>{

        private LayoutInflater mInflater;
        private Context context;
        private String rTitle[];
        private String rDescription[];
        private int rImgs[];
        private List<AchievementsItem> rDBdata;

        private class ProgressHolder{
            TextView textview;
            ProgressBar pgsBar;
        }

        MyAdapter(Context c, String title[], int imgs[], List<AchievementsItem> DBdata)
        {
            super(c, R.layout.achievements_row, R.id.textview1, title);
            this.context = c;
            this.rTitle = title;
            //this.rDescription = description;
            this.rImgs = imgs;
            System.out.println("Update Achievements : " +DBdata.size());
            this.rDBdata = new ArrayList<AchievementsItem>(DBdata);
            for(AchievementsItem i :rDBdata){System.out.println("Update Achievements : " +i.title);}
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            System.out.println("Get Views...");
            //set progressholder to null every time
            ProgressHolder pgsHolder = null;

            if (convertView == null){
                convertView = mInflater.inflate(achievements_row, null);
            }
            //set id, position of image title, descriptiom
            ImageView images = convertView.findViewById(R.id.image1);
            TextView myTitle = convertView.findViewById(R.id.textview1);
            TextView myDescription = convertView.findViewById(R.id.textview2);
            pgsHolder = new ProgressHolder();
            pgsHolder.textview = convertView.findViewById(R.id.textview3);
            pgsHolder.pgsBar = convertView.findViewById(R.id.pBar);

            images.setImageResource(rImgs[position]);
            myTitle.setText(rDBdata.get(position).title);
            myDescription.setText(rDBdata.get(position).subtitle);
            pgsHolder.pgsBar.setMax(rDBdata.get(position).max);

            //update achievements status from firestore DB

            if(rDBdata.get(position).max != -1) {updateAchievements(pgsHolder, position);}
            else {
                pgsHolder.pgsBar.setVisibility(View.GONE);
                pgsHolder.textview.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        private void updateAchievements(ProgressHolder pHolder, int pos){


            System.out.println("Update Achievements : " +rDBdata.get(pos).title);
            pHolder.pgsBar.setProgress(rDBdata.get(pos).order);
            pHolder.textview.setText(rDBdata.get(pos).order+"/"+rDBdata.get(pos).max);

        }
    }

}