package com.example.timemakerapp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DailyTask {

    private String mTaskName;
    private Date mTimestamp;
    private boolean achieved;
    private String id = "";

    public DailyTask(String name, boolean achieved){
        mTaskName = name;
        mTimestamp = new Date();
        this.achieved = achieved;
    }

    public DailyTask(String name, Date time, boolean achieved){
        mTaskName = name;
        mTimestamp = time;
        this.achieved = achieved;
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
}
