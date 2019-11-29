package com.example.timemakerapp.models;

public class Progress {
    public Boolean accomplishedToday;
    public Integer completedGoals;
    public Integer daysInRow;
    public String[] unlockedBadges;

    public Progress(Boolean accomplishedToday, Integer completedGoals, Integer daysInRow, String[] unlockedBadges) {
        this.accomplishedToday = accomplishedToday;
        this.completedGoals = completedGoals;
        this.daysInRow = daysInRow;
        this.unlockedBadges = unlockedBadges;
    }

    public Progress() {

    }

    public void setInitProgress() {
        this.accomplishedToday = false;
        this.completedGoals = 0;
        this.daysInRow = 0;
        this.unlockedBadges = null;
    }
}
