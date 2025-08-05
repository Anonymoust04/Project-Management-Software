package com.example.application.views;


import com.example.application.entity.SprintBacklogUserStory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.example.application.TimeSpentService;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.example.application.FirebaseService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Route("average-time-spent")
public class AverageTimeSpentView extends VerticalLayout {

    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Grid<UserTimeSpent> timeSpentGrid;

    private TimeSpentService timeSpentService;

    public AverageTimeSpentView(TimeSpentService timeSpentService) {
        this.timeSpentService = timeSpentService;

        // Initialize layout and components
        setAlignItems(Alignment.CENTER);
        setSpacing(true);

        startDatePicker = new DatePicker("Start Date");
        endDatePicker = new DatePicker("End Date");

        Button viewButton = new Button("View", event -> displayAverageTime());

        // Layout for DatePickers
        HorizontalLayout dateRangeLayout = new HorizontalLayout(startDatePicker, endDatePicker);
        add(dateRangeLayout);

        // Table/Grid to display the average time
         timeSpentGrid = new Grid<>(UserTimeSpent.class, false);
         timeSpentGrid.addColumn(UserTimeSpent::getTeamMemberName).setHeader("Team Member Name");
         timeSpentGrid.addColumn(UserTimeSpent::getAverageHoursSpent).setHeader("Average Hours Spent");

         add(timeSpentGrid);
    }

     private void displayAverageTime() {
         LocalDate startDate = startDatePicker.getValue();
         LocalDate endDate = endDatePicker.getValue();

         if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
             Notification.show("Please select a valid date range.");
             return;
         }

         // Fetch average time spent by each user between startDate and endDate
         //List<UserTimeSpent> averageTimeSpentList = fetchAverageTimeSpent(startDate, endDate);

         // Populate the grid with the data
         //timeSpentGrid.setItems(averageTimeSpentList);
     }
//    private List<UserTimeSpent> fetchAverageTimeSpent(LocalDate startDate, LocalDate endDate) {
//        // Fetch data from Firebase
//        List<SprintBacklogUserStory> userStories = FirebaseService.getAllSprintBacklogUserStories(startDate, endDate);
//
//        // Create a map to store total logged time per user
//        Map<String, Double> userLoggedHoursMap = new HashMap<>();
//
//        // Iterate over the fetched logs
//        for (SprintBacklogUserStory story : userStories) {
//            String assignee = story.getAssignee();
//            double loggedHours = story.getTotalLogTime();
//
//            // Accumulate the logged hours for each user
//            userLoggedHoursMap.put(assignee, userLoggedHoursMap.getOrDefault(assignee, 0.0) + loggedHours);
//        }
//
//        // Calculate the number of days in the selected range
//        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
//
//        // Create a list to store the average hours spent per user
//        List<UserTimeSpent> averageTimeSpentList = new ArrayList<>();
//        for (Map.Entry<String, Double> entry : userLoggedHoursMap.entrySet()) {
//            String teamMember = entry.getKey();
//            double totalHours = entry.getValue();
//            double averageHours = totalHours / totalDays;
//
//            averageTimeSpentList.add(new UserTimeSpent(teamMember, averageHours));
//        }
//
//        return averageTimeSpentList;
//    }


    // Inner class to represent data
    public static class UserTimeSpent {
        private String teamMemberName;
        private double averageHoursSpent;

        public UserTimeSpent(String teamMemberName, double averageHoursSpent) {
            this.teamMemberName = teamMemberName;
            this.averageHoursSpent = averageHoursSpent;
        }

        public String getTeamMemberName() {
            return teamMemberName;
        }

        public double getAverageHoursSpent() {
            return averageHoursSpent;
        }
    }

}

//testing again



