package com.example.timemakerapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;

import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.widget.*;

import androidx.annotation.NonNull;
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
    int images [] = {R.drawable.first,R.drawable.turkey, R.drawable.goal, R.drawable.signboard, R.drawable.schedule };
    //int pgsMax[] = {20,7,13};
    //int pgsVisible[] = {0,1,1};
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("achievements");
    List<AchievementsItem> Achieves;
    private String currentUser;
    private String TAG = "AchievementActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();;

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

        System.out.println("currentuser: "+currentUser);
        System.out.println("Get Achievements...");
        getUpdatedtask();
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
                        //    insertNewUser(task, currentUser);
                            Achieves = new ArrayList<>();
                            //List<AchievementsItem> progress = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                Map<String,Object> taskMap = doc.getData();
                                String title = (String) taskMap.get("title");
                                String subtitle = (String) taskMap.get("subtitle");
                                int  max = ((Number)taskMap.get("max")).intValue();
                                int progress = -1;
                                Map<String,Object> order = (Map<String,Object> )taskMap.get("order");
                                for (Map.Entry<String, Object> entry : order.entrySet()) {
                                    String k = entry.getKey();
                                    if(k.equals(currentUser)) {
                                        progress = ((Number) entry.getValue()).intValue();
                                        break;
                                    }
                                    else continue;
                                }
                                if(progress > max) progress = max;
                                System.out.println("Read Achievements : " +title+" "+max+" "+progress+" ");
                                Achieves.add(new AchievementsItem(title, subtitle , max , progress));
                            }

                            //for(AchievementsItem i :Achieves){System.out.println("Read Achievements : " +i.title);}
                            MyAdapter adapter = new MyAdapter(getActivity(), mTitle, images, Achieves);
                            listview.setAdapter(adapter);

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
                if(k.equals(currentUser)){
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
    private void getUpdatedtask(){

       // final String [] updatedtask = { "day_streak" , "number_of_completed_tasks" };
        DocumentReference streakdoc = FirebaseFirestore.getInstance().collection("user_achievements").document(currentUser);
        streakdoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object>  myListOfDocuments = task.getResult().getData();
                    processAchievement(myListOfDocuments);
                
                } else {
                    System.out.println("Error");
                    // Log.d(TAG, "get failed with ", task.getException());
                    System.out.println(task.getException());
                }
            }
        });



    }

    private void processAchievement(final Map<String, Object> updatetasks){
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List <Object> updated = new ArrayList<>();
        updated.add(updatetasks.get("day_streak"));
        updated.add(updatetasks.get("number_of_completed_task"));
        Map<String, Object> newcountUpdate = new HashMap<>();
       // Map<String, Object> newstreakUpdate = new HashMap<>();
        Map<String, Object> newcountUpdates = new HashMap<>();
        //Map<String, Object> newstreakUpdates = new HashMap<>();
        newcountUpdate.put(currentUser , updatetasks.get("number_of_completed_tasks"));
        newcountUpdates.put("order", newcountUpdate);


        String [] AchieveList1 = { "1stuse", "complete100goals", "complete10goals"};
        String [] AchieveList2 = { "3daysinrow", "perfectweek" };
        //int day_streak = ((Number)updatetasks.get("day_streak")).intValue();

        for (String element : AchieveList1) {
            DocumentReference ref = FirebaseFirestore.getInstance().collection("achievements").document(element);
            if (element.equals("1stuse")){
                System.out.println("1stuse: "+((Number)newcountUpdate.get(currentUser)).intValue()  );
                if( ((Number)newcountUpdate.get(currentUser)).intValue() == 1 ){
                    Map<String, Object> firstuseUpdates = new HashMap<>();
                    firstuseUpdates.put("order", newcountUpdate);
                    ref.update(firstuseUpdates);
                }
            }
            else ref.update(newcountUpdates);

        }


        /*
        for (String element : AchieveList2) {
            DocumentReference ref = FirebaseFirestore.getInstance().collection("achievements").document(element);
            Map<String, Object> newstreakUpdate = new HashMap<>();
            int int_streak = ((Number)updatetasks.get("day_streak")).intValue();
            if (element.equals("3daysinrow")){
                if(int_streak %3 == 0  && int_streak !=0 ) {
                    newstreakUpdate.put(currentUser, updatetasks.get("day_streak"));
                    Map<String, Object> firstuseUpdates = new HashMap<>();
                    firstuseUpdates.put("order", newcountUpdate);
                    ref.update(firstuseUpdates);
                }
            }
            else ref.update(newcountUpdates);
        }*/

        final DocumentReference ref = FirebaseFirestore.getInstance().collection("achievements").document("3daysinrow");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@Nonnull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                  //  System.out.println("daystreak2: "+((Number) updatetasks.get("day_streak")).intValue() );
                    if (((Number) updatetasks.get("day_streak_3")).intValue() % 3 == 0 && ((Number) updatetasks.get("day_streak_3")).intValue() != 0 ) {
                        System.out.println("daystreak3: "+((Number) updatetasks.get("day_streak_3")).intValue() );
                        Map<String,Object> taskMap= task.getResult().getData();
                        Map<String,Object> order = (Map<String,Object> )taskMap.get("order");
                        for (Map.Entry<String, Object> entry : order.entrySet()) {
                            String k = entry.getKey();
                            if(k.equals(currentUser)) {
                                //int progress = ((Number) entry.getValue()).intValue() + 1;
                                Map<String, Object> newstreakUpdate = new HashMap<>();
                                Map<String, Object> newstreakUpdates = new HashMap<>();

                                newstreakUpdate.put(currentUser , ((Number) entry.getValue()).intValue() + 1);
                                newstreakUpdates.put("order", newstreakUpdate);
                                ref.update(newstreakUpdates);
                                resetAchievementStreak_3();
                                break;
                            }
                            else continue;
                        }

                    }

                } else {
                    System.out.println("Query Failed");
                }
            }
        });

        final DocumentReference ref2 = FirebaseFirestore.getInstance().collection("achievements").document("perfectweek");
        ref2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@Nonnull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if (((Number) updatetasks.get("day_streak_7")).intValue() % 7 == 0 && ((Number) updatetasks.get("day_streak_7")).intValue() != 0 ) {

                        Map<String,Object> taskMap= task.getResult().getData();
                        Map<String,Object> order = (Map<String,Object> )taskMap.get("order");
                        for (Map.Entry<String, Object> entry : order.entrySet()) {
                            String k = entry.getKey();
                            if(k.equals(currentUser)) {
                                int progress = ((Number) entry.getValue()).intValue() + 1;
                                Map<String, Object> newstreakUpdate = new HashMap<>();
                                Map<String, Object> newstreakUpdates = new HashMap<>();

                                newstreakUpdate.put(currentUser , ((Number) entry.getValue()).intValue() + 1);
                                newstreakUpdates.put("order", newstreakUpdate);
                                ref2.update(newstreakUpdates);
                                resetAchievementStreak_7();
                                break;
                            }
                            else continue;
                        }

                    }

                } else {
                    System.out.println("Query Failed");
                }
            }
        });
    }

    private void resetAchievementStreak_3()
    {
        Map<String, Object> docData = new HashMap<>();
        docData.put("day_streak_3", 0);
        db.collection("user_achievements").document(currentUser).update(docData);
    }

    private void resetAchievementStreak_7()
    {
        Map<String, Object> docData = new HashMap<>();
        docData.put("day_streak_7", 0);
        db.collection("user_achievements").document(currentUser).update(docData);
    }


    public AchievementsFragment() {
        // Required empty public constructor
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    //overide view functions
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

        getUpdatedtask();

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
            LinearLayout progressBarView = convertView.findViewById(R.id.progressBarView);

            images.setImageResource(rImgs[position]);
            myTitle.setText(rDBdata.get(position).title);
            myDescription.setText(rDBdata.get(position).subtitle);
            pgsHolder.pgsBar.setMax(rDBdata.get(position).max);

            //update achievements status from firestore DB

            if(rDBdata.get(position).max != -1) {updateAchievements(pgsHolder, position);}
            else {
                progressBarView.setVisibility(View.INVISIBLE);
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