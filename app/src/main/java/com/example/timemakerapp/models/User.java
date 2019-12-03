package com.example.timemakerapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String uid;
    public String email;
    public Object progress;

    public User(String uid, String email, String goal, String[] previousGoals, Object progress) {
        this.uid = uid;
        this.email = email;
        this.progress = progress;
    }

    public User() {

    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
