package com.example.application.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SprintBoardUserStory {
    private String userStory;
    private String assignee;
    private String id;
    private String status;

    private String userStoryPoint;

    private String sprintBoardName;

    private ArrayList<String> tags;

    private String priority;

    public SprintBoardUserStory() {
    }

    public void setDetails(String userStory, String assignee, String id, String userStoryPoint, String status,
                           String sprintBoardName, ArrayList<String> tags, String priority) {
        this.userStory = userStory;
        this.assignee = assignee;
        this.id = id; // Ensure id is also set
        this.userStoryPoint = userStoryPoint;
        this.status = status;
        this.sprintBoardName = sprintBoardName;
        this.tags = tags;
        this.priority = priority;
    }

    // Setters and getters
    public void setUserStory(String userStory){
        this.userStory = userStory;
    }

    public String getUserStory() {
        return userStory;
    }

    public void setAssignee(String assignee){
        this.assignee = assignee;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getId() {
        return id;
    }

    public void setUserStoryPoint(String userStoryPoint){
        if (userStoryPoint == null){
            this.userStoryPoint = "1";
            return;
        }
        this.userStoryPoint = userStoryPoint;
    }

    public String getUserStoryPoint(){
        if (userStoryPoint == null){
            userStoryPoint = "1";
        }
        return userStoryPoint;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public void setSprintBoardName(String sprintBoardName){
        this.sprintBoardName = sprintBoardName;
    }

    public void setTags(ArrayList<String> tags){
        if (tags == null){
            this.tags = new ArrayList<String>();
            this.tags.add("Front end");
        }
        this.tags = tags;
    }

    public ArrayList<String> getTags() {
        if (tags == null){
            tags = new ArrayList<String>();
            tags.add("Front end");
        }
        return tags;
    }

    public void setPriority(String priority){
        if (priority == null){
            this.priority = "Medium";
        }
        this.priority = priority;
    }

    public String getPriority() {
        if (priority == null){
            this.priority = "Medium";
        }
        return priority;
    }

    public String getSprintBoardName(){
        return sprintBoardName;
    }
}
