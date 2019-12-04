package com.example.timemakerapp;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public final class AchievementDBupdater {
    private String currentUser;
    private String TAG = "AchievementActivity";

    public AchievementDBupdater(){
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    public void insertNewAchieveEntry(){

        FirebaseFirestore.getInstance()
                .collection(
                        "achievements")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@Nonnull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
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
                        else System.out.println("Query Failed");

                    }
                });
    }
}
