package com.example.task_it;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Item {
    private String title;
    private String description;

    private Date taskDate;

    public Item(String title, String description, Date taskDate) {
        this.title = title;
        this.description = description;
        this.taskDate = taskDate;
    }
    // Getters et setters
    public Date getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(Date taskDate) {
        this.taskDate = taskDate;
    }
    public String getTaskName() {
        return title;
    }

    public void setTaskName(String title) {
        this.title = title;
    }

    public String getTaskDescription() {
        return description;
    }

    public void setTaskDescription(String description) {
        this.description = description;
    }
}

