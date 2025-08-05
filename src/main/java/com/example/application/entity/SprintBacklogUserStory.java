package com.example.application.entity;

import com.example.application.FirebaseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

public class SprintBacklogUserStory {
    private String sprintName;
    private String taskName;
    private String storyPoint;
    private Double storyPointRemainder;
    private String priority;
    private ArrayList<String> tags;
    private String status;
    private String assignee;
    private Double totalLogTime;
    private HashMap<String, Double> accumulationOfEffort;


    public SprintBacklogUserStory() {

    }

    public void setDetails(String sprintName, String taskName, String storyPoint, String priority, ArrayList<String> tags, String assignee) {
        this.sprintName = sprintName;
        this.taskName = taskName;
        this.storyPoint = storyPoint;
        this.storyPointRemainder = Double.parseDouble(storyPoint);
        this.priority = priority;
        this.tags = tags;
        this.assignee = assignee;
        this.status = "Not Started";
        this.totalLogTime = 0.0;
        initialiseAccumOfEffort();
    }
    public void updateDetails(String taskName, String storyPoint, String priority, ArrayList<String> tags, String assignee, Double logTime) {
        this.taskName = taskName;
        this.storyPoint = storyPoint;
        this.priority = priority;
        this.tags = tags;
        this.assignee = assignee;
        this.status = "Not Started";
        if (logTime != null) {
            Sprint sprint = obtainSprint();
            int currentDay = sprint.obtainDaysFromStart() + 1;
            Double currentStoryPoints = this.accumulationOfEffort.get(Integer.toString(currentDay));
            if ((storyPointRemainder - logTime) >= 0) {
                currentStoryPoints += logTime;
                this.totalLogTime += logTime;
                this.storyPointRemainder -= logTime;
                this.accumulationOfEffort.put(Integer.toString(currentDay), currentStoryPoints);

                HashMap<String, Double> burnDownChatData = sprint.getBurnDownChartData();
                Double currentStoryPointForChat = burnDownChatData.get(Integer.toString(currentDay)) + logTime;
                burnDownChatData.put(Integer.toString(currentDay), currentStoryPointForChat);
                sprint.setBurnDownChartData(burnDownChatData);
                updateSprint(sprint);
            };
        };
    }

    public void initialiseAccumOfEffort(){
        this.accumulationOfEffort = new HashMap<String, Double>();
        Sprint sprint = obtainSprint();
        Integer period = sprint.getSprintPeriod();
        for (int i = 1; i <= period + 1; i++){
            this.accumulationOfEffort.put(Integer.toString(i), 0.0);
        }
    }

    public String getSprintName() { return sprintName; }
    public void getSprintName(String sprintName) { this.sprintName = sprintName; }

    public Sprint obtainSprint() {
        Sprint sprint;
        try {
            sprint = FirebaseService.getSprint(sprintName);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return sprint;
    }

    public void updateSprint(Sprint sprint) {
        try {
            FirebaseService.updateSprint(sprint);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getStoryPoint(){
        return storyPoint;
    }
    public void setStoryPoint(String storyPoint){
        this.storyPoint = storyPoint;
    }

    public Double getStoryPointRemainder(){
        return storyPointRemainder;
    }
    public void setStoryPointRemainder(Double storyPointRemainder){
        this.storyPointRemainder = storyPointRemainder;
    }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public ArrayList<String> getTags(){
        return tags;
    }

    public void setTags(ArrayList<String> tags){
        this.tags = tags;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }

    public void setTotalLogTime(Double logTime){
        this.totalLogTime = logTime;
    }

    public Double getTotalLogTime(){
        return totalLogTime;
    }

    public void setAccumulationOfEffort(HashMap<String, Double>  accumulationOfEffort){
        this.accumulationOfEffort = accumulationOfEffort;
    }

    public HashMap<String, Double>  getAccumulationOfEffort(){
        return accumulationOfEffort;
    }

    public ArrayList<String> accumulationOfEffortStrList(){
        ArrayList<String> stringLst = new ArrayList<String>();

        for (Double effort: accumulationOfEffort.values()){
            stringLst.add(effort.toString());
        }

        return stringLst;
    }

}
