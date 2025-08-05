package com.example.application.entity;

import java.util.ArrayList;
import java.util.Objects;

public class Task {

    private Integer taskId;
    private String taskAssignee;
    private String taskName;
    private String taskStatus;
    private String taskStages;
    private Double taskStoryPoint;
    private ArrayList<String> taskTagList = new ArrayList<>();
    private String taskPriority;
    private String taskCreatedAt;

    private ArrayList<ChangeHistory> historyLogLst;


    public void setDetails(String taskAssignee, String taskName, String taskStatus, String taskStages, Double taskStoryPoint, ArrayList<String> taskTagList, String taskPriority, String taskCreatedAt){
        this.taskAssignee = taskAssignee;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskStages = taskStages;
        this.taskStoryPoint = taskStoryPoint;
        this.taskTagList = taskTagList;
        this.taskPriority = taskPriority;
        this.taskCreatedAt = taskCreatedAt;
        this.historyLogLst = new ArrayList<>();
        ChangeHistory createHistory = new ChangeHistory();
        createHistory.setDetails(taskAssignee, taskName, "Created", "", "");
        createHistory.setTimestamp(taskCreatedAt);
        this.historyLogLst.add(createHistory);
    }

    public void updateDetails(String taskAssignee, String taskName, String taskStatus, String taskStages, Double taskStoryPoint, ArrayList<String> taskTagList, String taskPriority){
        this.setTaskAssignee(taskAssignee);
        this.setTaskName(taskName);
        this.setTaskStatus(taskStatus);
        this.setTaskStages(taskStages);
        this.setTaskStoryPoint(taskStoryPoint);
        this.setTaskTagList(taskTagList);
        this.setTaskPriority(taskPriority);
    }

    public void setTaskId(Integer newTaskId){
        this.taskId = newTaskId;
    }

    public Integer getTaskId(){
        return taskId;
    }

    public void setTaskName(String taskName){
        if (this.taskName == null){
            this.taskName = taskName;
            return;
        }
        if (!Objects.equals(this.taskName, taskName)){
            createRecord(taskAssignee, taskName, "Task Name", this.taskName, taskName);
            this.taskName= taskName;
        }
    }

    public String getTaskName(){
        return taskName;
    }

    public void setTaskAssignee(String taskAssignee){
        if (this.taskAssignee == null){
            this.taskAssignee = taskAssignee;
            return;
        }
        if (!Objects.equals(this.taskAssignee, taskAssignee)){
            createRecord(taskAssignee, taskName, "Task Assignee", this.taskAssignee, taskAssignee);
            this.taskAssignee = taskAssignee;
        }
    }

    public String getTaskAssignee(){
        return taskAssignee;
    }

    public void setTaskStatus(String taskStatus){
        if (this.taskStatus == null){
            this.taskStatus = taskAssignee;
            return;
        }
        if (!Objects.equals(this.taskStatus, taskStatus)){
            createRecord(taskAssignee, taskName, "Task Details", this.taskStatus, taskStatus);
            this.taskStatus = taskStatus;
        }
    }

    public String getTaskStatus(){
        return taskStatus;
    }

    public void setTaskStages(String taskStages){
        if (this.taskStages == null){
            this.taskStages = taskStages;
            return;
        }
        if (!Objects.equals(this.taskStages, taskStages)){
            createRecord(taskAssignee, taskName, "Task Stages", this.taskStages, taskStages);
            this.taskStages = taskStages;
        }
    }

    public String getTaskStages(){
        return taskStages;
    }

    public void setTaskStoryPoint(Double taskStoryPoint){
        if (this.taskStoryPoint == null){
            this.taskStoryPoint = taskStoryPoint;
            return;
        }
        if (!Objects.equals(this.taskStoryPoint, taskStoryPoint)){
            createRecord(taskAssignee, taskName, "Task Story Point", this.taskStoryPoint != null ? this.taskStoryPoint.toString() : null, taskStoryPoint.toString());
            this.taskStoryPoint = taskStoryPoint;
        }
    }

    public Double getTaskStoryPoint(){
        return taskStoryPoint;
    }

    public void setTaskTagList(ArrayList<String> taskTagList){
        if (this.taskTagList == null || this.taskTagList.isEmpty()){
            this.taskTagList = taskTagList;
            return;
        }

        if (!Objects.equals(this.taskTagList, taskTagList)){
            createRecord(taskAssignee, taskName, "Task Tags", this.taskTagList.toString(), taskTagList.toString());
            this.taskTagList = taskTagList;
        }
    }

    public void addTaskTagList(String tag){
        taskTagList.add(tag);
    }

    public ArrayList<String> getTaskTagList(){
        return taskTagList;
    }


    public void setTaskPriority(String taskPriority){
        if (this.taskPriority == null){
            this.taskPriority = taskPriority;
            return;
        }

        if (!Objects.equals(this.taskPriority, taskPriority)){
            createRecord(taskAssignee, taskName, "Task Tags", this.taskPriority, taskPriority);
            this.taskPriority = taskPriority;
        }
    }

    public String getTaskPriority(){
        return taskPriority;
    }

    public String getTaskCreatedAt(){
        return taskCreatedAt;
    }

    public void createRecord(String user, String taskName, String fieldChanged, String oldValue, String newValue){
        ChangeHistory changeHistory = new ChangeHistory();
        changeHistory.setDetails(user, taskName, fieldChanged, oldValue, newValue);
        this.addHistoryLogLst(changeHistory);
    }

    public void addHistoryLogLst(ChangeHistory changeHistory){
        if (this.historyLogLst == null){
            this.historyLogLst = new ArrayList<ChangeHistory>();
        }
        this.historyLogLst.add(changeHistory);
    }

    public void setHistoryLogLst(ArrayList<ChangeHistory> historyLogLst){
        if (this.historyLogLst == null || this.historyLogLst.isEmpty()){
            this.historyLogLst = historyLogLst;
        }
    }

    public ArrayList<ChangeHistory> getHistoryLogLst(){
        return historyLogLst;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj){
            return true;
        }
        if(!(obj instanceof Task other)){
            return false;
        }
        return Objects.equals(taskId, other.taskId);
    }
}
