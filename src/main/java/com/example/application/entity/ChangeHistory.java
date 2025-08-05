package com.example.application.entity;

import java.time.LocalDateTime; // Import statement for LocalDateTime

public class ChangeHistory {
    private String timestamp;
    private String user;
    private String taskName;
    private String fieldChanged;
    private String oldValue;
    private String newValue;

    public ChangeHistory(){

    }

    public void setDetails(String user, String taskName, String fieldChanged, String oldValue, String newValue) {
        this.timestamp = LocalDateTime.now().toString();
        this.user = user;
        this.taskName = taskName;
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public void setTimestamp(String localDateTime){
        this.timestamp = localDateTime;
    }
    //getters for the fields
    public String getTimestamp() {
        return timestamp;
    }

    public String getUser() {
        return user;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getFieldChanged() {
        return fieldChanged;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }
}
