package com.example.application.views;

import java.time.LocalDateTime;
import java.util.Set;


public class TaskItem {

    private String name;
    private String taskStatus;
    private String taskStages;
    private Double storyPoint;
    private String assignee;
    private Set<String> tags;
    private String priority;
    private boolean checked;
    private Integer id;
    private final LocalDateTime createdAt;

    public TaskItem(String name, String taskStatus, String taskStages, Double storyPoint, String assignee, Set<String> tags, String priority, LocalDateTime createdAt) {
        this.name = name;
        this.taskStatus = taskStatus;
        this.taskStages = taskStages;
        this.storyPoint = storyPoint;
        this.assignee = assignee;
        this.tags = tags;
        this.priority = priority;
        this.checked = false;
        if(createdAt != null){
            this.createdAt = createdAt;
        }
        else {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Double getStoryPoint() {
        if (storyPoint == null){
            return 0.0;
        }
        return storyPoint;
    }

    public void setStoryPoint(Double storyPoint) {
        this.storyPoint = storyPoint;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getTaskStages() {
        return taskStages;
    }

    public void setTaskStages(String taskStages) {
        this.taskStages = taskStages;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getPriorityOrder() {
        return switch (priority.toLowerCase()) {
            case "urgent" -> 4;
            case "important" -> 3;
            case "medium" -> 2;
            case "low" -> 1;
            default -> 0;
        };
    }

    private boolean selected; // Add this field

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}

