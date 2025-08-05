package com.example.application.entity;

import com.google.cloud.firestore.annotation.Exclude;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Sprint {
    private String name;

    private Double totalStoryPoints = 0.0;

    private List<SprintBoardUserStory> userStories = new ArrayList<>();
    private String color;
    private boolean checked; // to track if the sprint is checked

    private boolean notStarted = true;
    private boolean active = false;
    private boolean completed = false;

    private List<String> rolesAndNames;

    private String startDate;
    private String endDate;

    private HashMap<String, Double> burnDownChartData = new HashMap<String, Double>();


    public enum SprintStatus {
        NOT_STARTED, ACTIVE, COMPLETED
    }


    public void setDetails(String name, String startDate, String endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        setStatus(SprintStatus.NOT_STARTED); // Default to not started

    }

    public boolean isNotStarted() {
        return notStarted;
    }

    public void setNotStarted(boolean notStarted) {
        this.notStarted = notStarted;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setStatus(SprintStatus status) {
        this.notStarted = status == SprintStatus.NOT_STARTED;
        this.active = status == SprintStatus.ACTIVE;
        this.completed = status == SprintStatus.COMPLETED;
    }

    public void setRolesAndNames(List<String> rolesAndNames) {
        this.rolesAndNames = rolesAndNames;
    }

    public List<String> getRolesAndNames() {
        return rolesAndNames != null ? rolesAndNames : new ArrayList<>();
    }





    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStartDateAsString() {
        return startDate != null ? startDate.toString() : "";
    }

    public String getEndDateAsString() {
        return endDate != null ? endDate.toString() : "";
    }

    public String getStartDate(){
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate(){
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void addUserStory(SprintBoardUserStory story) { userStories.add(story); }
    public List<SprintBoardUserStory> getUserStories() { return userStories; }

    public void deleteUserStory(SprintBoardUserStory userStory){
        userStories.remove(userStory);
    }

    public void setColor(String color) { this.color = color; }
    public String getColor() { return color; }


    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    public Double getTotalStoryPoints(){
        return totalStoryPoints;
    }

    public void setTotalStoryPoints(Double totalStoryPoints){
        this.totalStoryPoints = totalStoryPoints;
    }

    public void initialiseBurnDownChat(){
        for (SprintBoardUserStory userStory: userStories){
            totalStoryPoints += Double.parseDouble(userStory.getUserStoryPoint());
        }

        Integer sprintPeriod = getSprintPeriod();
        for(int i = 1; i <= sprintPeriod + 1; i ++){
            burnDownChartData.put(Integer.toString(i), 0.0);
        }
    }

    public void removeBurnDownChat(){
        burnDownChartData = new HashMap<String, Double>();
    }

    public void setBurnDownChartData(HashMap<String, Double> burnDownChartData){
        this.burnDownChartData = burnDownChartData;
    }

    public HashMap<String, Double> getBurnDownChartData(){
        return burnDownChartData;
    }

    public Integer obtainDaysFromStart(){
        String startDateList = new ArrayList<String>(Arrays.asList(this.startDate.split("-"))).getLast();
        int currentDate = LocalDate.now().getDayOfMonth();
        return currentDate - Integer.parseInt(startDateList);
    }


    public Integer getSprintPeriod(){
        List<String> startDateList = new ArrayList<String>(Arrays.asList(this.startDate.split("-")));
        List<String> endDateList = new ArrayList<String>(Arrays.asList(this.endDate.split("-")));

        return Integer.parseInt(endDateList.getLast()) - Integer.parseInt(startDateList.getLast());
    }

    public ArrayList<Double> obtainBurndownChartData(){
        ArrayList<Double> burndownChartDataLst = new ArrayList<Double>();
        Double totalStoryPointsAmt = totalStoryPoints;
        Double userPointsAccum = 0.0;
        Integer currentDay = obtainDaysFromStart() + 1;
        Integer day = 1;
        System.out.println(currentDay);
        System.out.println(day);
        for (Double storyPointsAchieved: burnDownChartData.values()){
            userPointsAccum += storyPointsAchieved;
            if (totalStoryPointsAmt - userPointsAccum <= 0){
                burndownChartDataLst.add(0.0);
            }
            else {
                burndownChartDataLst.add(totalStoryPointsAmt - userPointsAccum);
            };

            if (day.equals(currentDay)){
                break;
            }
            day++;
        }

        return burndownChartDataLst;
    }

}
