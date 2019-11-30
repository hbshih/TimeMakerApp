package com.example.timemakerapp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DailyTask {

    private String mTaskName;
    private Date mTimestamp;
    private boolean achieved;
    private String id = "";
    private String userId;

    public DailyTask(String name, boolean achieved, String user){
        mTaskName = name;
        mTimestamp = new Date();
        this.achieved = achieved;
        this.userId = user;
    }

    public DailyTask(String name, Date time, boolean achieved, String user){
        mTaskName = name;
        mTimestamp = time;
        this.achieved = achieved;
        this.userId = user;
    }

    public DailyTask(){

    }

    public String getName(){
        return mTaskName;
    }

    public Date getTime(){
        return mTimestamp;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public String getUserId(){
        return userId;
    }

}
