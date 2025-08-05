package com.example.application.views;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//import org.hibernate.validator.constraints.UUID;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.example.application.FirebaseService;
import com.example.application.entity.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;

@CssImport("./styles/sprint-board-styles.css")

@Route(value = "sprint-board", layout = MainLayout.class)
@PageTitle("Sprint Board")
public class SprintBoardView extends VerticalLayout {

    private List<Sprint> sprints = new ArrayList<>();
    private VerticalLayout sprintContainer;
    private HorizontalLayout userStoriesDisplay;
    private List<Sprint> deletedSprints = new ArrayList<>(); //list to store deleted sprint
    private Map<Sprint, Integer> originalPositions = new HashMap<>();

    private Button notStartedButton;
    private Button completedButton;
    private Checkbox activeToggle;
    private Span activeLabel;
    private final UserSession userSession;

    private ArrayList<Sprint> activeSprint = new ArrayList<Sprint>();


    private List<String> sprintColors = Arrays.asList(
            "#FFFFE0",  // Pastel Yellow
            "#E6E6FA",  // Pastel Purple
            "#98FB98",  // Pastel Green
            "#E0FFFF"   // Pastel Blue
    );
    private int colorIndex = 0;

    @Autowired
    public SprintBoardView(UserSession userSession) {
        this.userSession = userSession;
        addClassName("sprint-board-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureHeader();
        configureSprintSection();
        configureContent();
        initializeStatusControls();
    }

    private void configureHeader() {
        H2 headerText = new H2("Sprint Board");
        headerText.addClassName("sprint-board-header-text");

        Div headerDiv = new Div(headerText);
        headerDiv.addClassName("sprint-board-header");
        headerDiv.setWidthFull();

        add(headerDiv);
    }

    private void configureContent(){
        VerticalLayout content = new VerticalLayout();
        content.addClassName("sprint-board-content");
        content.setSizeFull(); // Full height and width
        content.setPadding(true);
        content.setSpacing(true);

        // Add sections to content
        content.add(sprintContainer);

        // Load previous data
        try {
            retrievePreviousSprints();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace(); // Log the error
            Notification.show("Failed to retrieve previous data.");
        }

        add(content); // Add to the view
        expand(content); // Expand the content layout
    }

    private void configureSprintSection() {
        // Initialize the sprint container
        sprintContainer = new VerticalLayout();
        sprintContainer.addClassName("sprint-container");
        sprintContainer.setWidthFull(); // Ensure full width
        sprintContainer.setHeight("100%");
        // Set additional styles if needed
        sprintContainer.getStyle()
                .set("border", "1px solid #ddd") // Optional: Add border for visual separation
                .set("padding", "10px") // Optional: Add padding
                .set("margin-bottom", "10px"); // Optional: Add margin at the bottom

        // Title for the Sprint section
        H2 sprintHeader = new H2("Sprint");
        sprintContainer.add(sprintHeader); // Add the title to the sprint container

        // Create New Sprint button
        Button createSprintButton = new Button("Create New Sprint", e -> openCreateSprintDialog());
        createSprintButton.addClassName("create-sprint-button");

        // Delete button
        Button deleteButton = new Button("Delete", e -> {
            try {
                deleteSprints();
            } catch (ExecutionException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        deleteButton.addClassName("delete-button-red");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        // Undo button
        Button undoButton = new Button("Undo", e -> undoDeleteSprints());
        undoButton.addClassName("undo-button-blue");
        undoButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Wrap the buttons in a HorizontalLayout for positioning
        HorizontalLayout buttonWrapper = new HorizontalLayout(createSprintButton, deleteButton, undoButton);
        buttonWrapper.setWidthFull();
        buttonWrapper.setJustifyContentMode(JustifyContentMode.START);

        // Add the button wrapper to the sprint container
        sprintContainer.add(buttonWrapper);


    }

//    private MenuBar createDirectoryMenu() {
//        MenuBar menuBar = new MenuBar();
//        menuBar.addClassName("directory-menu");
//
//        Icon directoryIcon = new Icon(VaadinIcon.BULLETS);
//        Span directoryLabel = new Span(" Directory");
//        HorizontalLayout directoryLayout = new HorizontalLayout(directoryIcon, directoryLabel);
//        directoryLayout.setSpacing(true);
//        directoryLayout.setAlignItems(Alignment.CENTER);
//
//        MenuItem directoryMenuItem = menuBar.addItem(directoryLayout);
//        SubMenu directorySubMenu = directoryMenuItem.getSubMenu();
//
//        // sorting logic (sprint2)
//        directorySubMenu.addItem(new RouterLink("Admin Dashboard", AdminDashboardView.class));
//
//        directorySubMenu.addItem(new RouterLink("Product Backlog", ProductBacklogView.class));
//
//        return menuBar;
//    }

    private void openCreateSprintDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Create New Sprint");

        VerticalLayout dialogLayout = new VerticalLayout();
        TextField sprintName = new TextField("Sprint Name");
        DatePicker startDate = new DatePicker("Start Date");
        DatePicker endDate = new DatePicker("End Date");


        // Role dropdown
        ComboBox<String> roleDropdown = new ComboBox<>("Role");
        roleDropdown.setItems("Scrum Master", "Product Owner", "Team Member");
        roleDropdown.setPlaceholder("Select Role");

        // Name input field
        TextField nameField = new TextField("Name");

        // List to store roles and names
        List<String> rolesAndNames = new ArrayList<>();

        // Button to add role and name
        Button addRoleButton = new Button("Add Role", e -> {
            String role = roleDropdown.getValue();
            String name = nameField.getValue();
            if (role != null && !name.isEmpty()) {
                if ((role.equals("Scrum Master") || role.equals("Product Owner")) &&
                        rolesAndNames.stream().anyMatch(rn -> rn.startsWith(role))) {
                    Notification.show("Only one " + role + " allowed per sprint.");
                } else {
                    rolesAndNames.add(role + ": " + name);
                    roleDropdown.clear();
                    nameField.clear();
                    Notification.show("Added " + role + ": " + name);
                }
            } else {
                Notification.show("Please select a role and enter a name.");
            }
        });


        Button saveButton = new Button("Save", e -> {
            if (isValidSprintInput(sprintName.getValue(), startDate.getValue(), endDate.getValue()) && !rolesAndNames.isEmpty()) {
                createSprint(sprintName.getValue(),
                        startDate.getValue().toString(),
                        endDate.getValue().toString(),
                        rolesAndNames);
                dialog.close();
            } else {
                Notification.show("Please fill in all fields and add at least one role.");
            }
        });

        dialogLayout.add(sprintName, startDate, endDate, roleDropdown, nameField, addRoleButton, saveButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private boolean isValidSprintInput(String name, java.time.LocalDate start, java.time.LocalDate end) {
        if (name.isEmpty() || start == null || end == null) {
            Notification.show("Please fill in all fields");
            return false;
        }
        if (end.isBefore(start)) {
            Notification.show("End date must be after start date");
            return false;
        }
        return true;
    }

    private void retrievePreviousSprints() throws ExecutionException, InterruptedException {
        ArrayList<Sprint> sprintLst = FirebaseService.getAllSprintDetails();

        if (sprintLst != null){
            for (Sprint sprint : sprintLst){
                if (sprint.isActive() && activeSprint.isEmpty()){
                    activeSprint.add(sprint);
                }
                sprints.add(sprint);
                renderSprint(sprint);
            }
        }
    }

    private void createSprint(String name, String startDate, String endDate, List<String> rolesAndNames) {
        Sprint newSprint = new Sprint();
        newSprint.setDetails(name, startDate, endDate);
        newSprint.setColor(getNextColor());
        newSprint.setActive(false); // Set initial state to inactive
        newSprint.setRolesAndNames(rolesAndNames);

        sprints.add(newSprint);
        renderSprint(newSprint);
        try {
            FirebaseService.saveSprint(newSprint);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getNextColor() {
        String color = sprintColors.get(colorIndex);
        colorIndex = (colorIndex + 1) % sprintColors.size();
        return color;
    }

    private void initializeStatusControls() {
        notStartedButton = new Button("Not Started");
        notStartedButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        completedButton = new Button("Completed");
        completedButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        activeToggle = new Checkbox();
        activeLabel = new Span();
        activeLabel.setWidth("80px");
    }

    // roles scrollable
    private HorizontalLayout createScrollableRolesContainer(Sprint sprint) {
        HorizontalLayout container = new HorizontalLayout();
        container.setWidthFull();
        container.setHeight("30px"); // Adjust as needed
        container.setSpacing(false);
        container.setPadding(false);

        Button leftArrow = new Button("<", e -> scrollRoles(container, -1));
        styleArrowButton(leftArrow);

        Div rolesScroller = new Div();
        rolesScroller.setWidthFull();
        rolesScroller.setHeight("100%");
        rolesScroller.getStyle()
                .set("display", "flex")
                .set("overflow-x", "hidden")
                .set("flex-wrap", "nowrap");

        for (String roleAndName : sprint.getRolesAndNames()) {
            rolesScroller.add(createRoleComponent(roleAndName));
        }

        Button rightArrow = new Button(">", e -> scrollRoles(container, 1));
        styleArrowButton(rightArrow);

        container.add(leftArrow, rolesScroller, rightArrow);
        container.expand(rolesScroller);

        return container;
    }

    private void scrollRoles(HorizontalLayout container, int direction) {
        Div rolesScroller = (Div) container.getComponentAt(1);
        double scrollAmount = direction * 50;  // Adjusted for smaller components
        rolesScroller.getElement().executeJs(
                "this.scrollLeft += $0", scrollAmount);
    }

    private Span createRoleComponent(String roleAndName) {
        String[] parts = roleAndName.split(":");
        String role = parts[0].trim();
        String name = parts.length > 1 ? parts[1].trim() : "";

        Span roleSpan = new Span();
        roleSpan.getStyle()
                .set("background-color", getRoleColor(role))
                .set("color", "#333")  // Dark gray text for better contrast
                .set("padding", "3px 6px")
                .set("margin-right", "5px")
                .set("border-radius", "3px")
                .set("font-size", "0.8em")
                .set("white-space", "nowrap")
                .set("display", "inline-block")
                .set("border", "1px solid #BED8FF");  // Light border for definition


        Span roleLabel = new Span(role + ": ");
        roleLabel.getStyle().set("font-weight", "bold");

        Span nameLabel = new Span(name);

        roleSpan.add(roleLabel, nameLabel);
        return roleSpan;
    }

    private String getRoleColor(String role) {
        return "#E6F3FF";  // Light blue color
    }


    private void renderSprint(Sprint sprint) {
        VerticalLayout sprintBox = new VerticalLayout();
        sprintBox.setWidthFull();
        sprintBox.setPadding(true);
        sprintBox.getStyle()
                .set("border", "1px solid #ccc")
                .set("padding", "10px")
                .set("margin-bottom", "20px")
                .set("background-color", sprint.getColor());

        // Sprint header with name and checkbox
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(Alignment.CENTER);

        Checkbox sprintCheckbox = new Checkbox();
        sprintCheckbox.setValue(sprint.isChecked());
        sprintCheckbox.addValueChangeListener(event -> sprint.setChecked(event.getValue()));

        H2 sprintName = new H2(sprint.getName());
        sprintName.getStyle().set("margin", "0");
        String sprintName2 = sprint.getName();

        // Create a button with the task name as its label, for viewing the task
        Button sprintBacklogButton = new Button(sprint.getName(), e -> {
            if (sprint.isActive()) {
                getUI().ifPresent(ui -> ui.navigate(SprintBacklogView.class, sprintName2));
            }
            else{
                Notification.show("The sprint is not active", 3000, Notification.Position.MIDDLE);
            };
        });

        sprintBacklogButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        // Adjust the button's style
        sprintBacklogButton.getStyle()
                .set("text-align", "left")
                .set("padding-left", "0")
                .set("margin-right", "auto");
        // Add a click listener to the button that opens a dialog to view the task details when clicked

        // Add edit button
        Button editButton = new Button("Edit", new Icon(VaadinIcon.EDIT));
        editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        editButton.addClickListener(e -> {
            if (sprint.isNotStarted()) {
                openEditSprintDialog(sprint);
            }
            else{
                Notification.show("Can only edit sprint when the sprint is not started.", 3000, Notification.Position.MIDDLE);
            }
        });

        headerLayout.add(sprintCheckbox, sprintBacklogButton, editButton);

        // Status controls
        HorizontalLayout statusControlsLayout = new HorizontalLayout();
        statusControlsLayout.setWidthFull();
        statusControlsLayout.setJustifyContentMode(JustifyContentMode.END);
        statusControlsLayout.setAlignItems(Alignment.CENTER);

        Button notStartedButton = new Button("Not Started");
        Button activeButton = new Button("Active");
        Button completedButton = new Button("Completed");

        // Apply styles to make buttons more visible
        Stream.of(notStartedButton, activeButton, completedButton).forEach(button -> {
            button.addThemeVariants(ButtonVariant.LUMO_SMALL);
            button.getStyle()
                    .set("margin", "0 5px")
                    .set("border", "1px solid #ccc")
                    .set("border-radius", "4px");
        });

        // Set initial button states
        updateButtonStates(sprint, notStartedButton, activeButton, completedButton);


        // Add click listeners
        notStartedButton.addClickListener(event -> updateSprintStatus(sprint, Sprint.SprintStatus.NOT_STARTED, notStartedButton, activeButton, completedButton));
        activeButton.addClickListener(event -> updateSprintStatus(sprint, Sprint.SprintStatus.ACTIVE, notStartedButton, activeButton, completedButton));
        completedButton.addClickListener(event -> updateSprintStatus(sprint, Sprint.SprintStatus.COMPLETED, notStartedButton, activeButton, completedButton));

        statusControlsLayout.add(notStartedButton, activeButton, completedButton);


        // Combine header and status controls
        VerticalLayout topSection = new VerticalLayout(headerLayout, statusControlsLayout);
        topSection.setPadding(false);
        topSection.setSpacing(false);

        // Sprint details
        Div startDateContainer = new Div();
        startDateContainer.getElement().setProperty("innerHTML", "<strong>Start Date:</strong> " + sprint.getStartDateAsString());

        Div endDateContainer = new Div();
        endDateContainer.getElement().setProperty("innerHTML", "<strong>End Date:</strong> " + sprint.getEndDateAsString());




        // Add scrollable roles container
        HorizontalLayout rolesContainer = createScrollableRolesContainer(sprint);

        HorizontalLayout userStoriesContainer = createScrollableUserStoriesContainer(sprint);
        userStoriesContainer.getStyle().set("margin-top", "15px");

        // Add a button to open the burndown chart dialog
            Button burndownChartButton = new Button("View Burndown Chart");
            burndownChartButton.getStyle().set("margin-top", "10px");
            burndownChartButton.addClickListener(e -> {
                if (sprint.isActive()) {
                    viewBurnDownChart(sprint);
                } else {
                    Notification.show("Unable to show burndown chart when the sprint is not active.", 3000, Notification.Position.MIDDLE);
                }
            });

            sprintBox.add(burndownChartButton);



        sprintBox.add(topSection, startDateContainer, endDateContainer, rolesContainer, userStoriesContainer);
        sprintContainer.add(sprintBox);
    }

    private void viewBurnDownChart(Sprint sprint){
        Dialog chartDialog = new Dialog();
        chartDialog.setHeaderTitle("Burn Down Chart");
        chartDialog.setWidth("900px");

        HorizontalLayout chartDialogLayout = new HorizontalLayout();
        chartDialogLayout.setWidthFull();
        chartDialogLayout.setPadding(true);
        chartDialogLayout.setSpacing(true);

        Chart chart = new Chart(ChartType.LINE);

        Configuration configuration = chart.getConfiguration();

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle("Story Points");

        XAxis xAxis = configuration.getxAxis();
        xAxis.setTitle("Days");

        Legend legend = configuration.getLegend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setVerticalAlign(VerticalAlign.MIDDLE);
        legend.setAlign(HorizontalAlign.RIGHT);

        PlotOptionsSeries plotOptionsSeries = new PlotOptionsSeries();
        plotOptionsSeries.setPointStart(1);
        plotOptionsSeries.setPointInterval(sprint.getSprintPeriod());
        configuration.setPlotOptions(plotOptionsSeries);

        DataSeries series = new DataSeries();
        series.setPlotOptions(new PlotOptionsLine());
        series.setName("Actual Velocity");
        ArrayList<Double> burndownChartData = sprint.obtainBurndownChartData();
        for (int i = 0; i < burndownChartData.size(); i++) {
            series.add(new DataSeriesItem(i + 1, burndownChartData.get(i)));
        }

        configuration.addSeries(new ListSeries("Ideal Velocity ", sprint.getTotalStoryPoints(), 0));
        configuration.addSeries(series);
        chartDialogLayout.add(chart);
        chartDialog.add(chartDialogLayout);
        chartDialog.open();
    }

    private enum SprintStatus {
        NOT_STARTED, ACTIVE, COMPLETED
    }

    private void openEditSprintDialog(Sprint sprint) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Sprint");

        // Set a wider width for the dialog
        dialog.setWidth("700px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setWidthFull();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        TextField sprintName = new TextField("Sprint Name");
        sprintName.setValue(sprint.getName());
        sprintName.setWidthFull();


        DatePicker startDate = new DatePicker("Start Date");
        DatePicker endDate = new DatePicker("End Date");

        startDate.setValue(LocalDate.parse(sprint.getStartDate()));
        endDate.setValue(LocalDate.parse(sprint.getEndDate()));

        startDate.setWidthFull();
        endDate.setWidthFull();

        // Convert String to LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            startDate.setValue(LocalDate.parse(sprint.getStartDate()));
            endDate.setValue(LocalDate.parse(sprint.getEndDate()));
            //startDate.setValue(start.toLocalDate());
            //endDate.setValue(end.toLocalDate());
        } catch (DateTimeParseException e) {
            Notification.show("Error parsing dates. Using current date.", 3000, Notification.Position.MIDDLE);
            LocalDate currentDate = LocalDate.now();
            startDate.setValue(currentDate);
            endDate.setValue(currentDate);
        }

        // Role dropdown and name input for editing existing roles
        ComboBox<String> roleDropdown = new ComboBox<>("Role");
        roleDropdown.setItems("Scrum Master", "Product Owner", "Team Member");
        TextField nameField = new TextField("Name");
        nameField.setWidthFull();

        // List to store roles and names
        List<String> rolesAndNames = new ArrayList<>(sprint.getRolesAndNames());

        // Component to display current roles and names
        VerticalLayout rolesLayout = new VerticalLayout();
        refreshRolesLayout(rolesLayout, rolesAndNames);

        Button addRoleButton = new Button("Add/Update Role", e -> {
            String role = roleDropdown.getValue();
            String name = nameField.getValue();
            if (role != null && !name.isEmpty()) {
                String roleAndName = role + ": " + name;
                rolesAndNames.removeIf(rn -> rn.startsWith(role + ":"));
                rolesAndNames.add(roleAndName);
                refreshRolesLayout(rolesLayout, rolesAndNames);
                roleDropdown.clear();
                nameField.clear();
            }
        });

        Button saveButton = new Button("Save", e -> {
            if (isValidSprintInput(sprintName.getValue(), startDate.getValue(), endDate.getValue()) && !rolesAndNames.isEmpty()) {
                updateSprint(sprint, sprintName.getValue(),
                        startDate.getValue().toString(),
                        endDate.getValue().toString(),
                        rolesAndNames);
                dialog.close();
                refreshSprintBoard();
            } else {
                Notification.show("Please fill in all fields and ensure at least one role is assigned.");
            }
        });

        dialogLayout.add(sprintName, startDate, endDate, roleDropdown, nameField, addRoleButton, rolesLayout, saveButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void refreshRolesLayout(VerticalLayout rolesLayout, List<String> rolesAndNames) {
        rolesLayout.removeAll();
        for (String roleAndName : rolesAndNames) {
            HorizontalLayout roleLayout = new HorizontalLayout();
            roleLayout.add(new Span(roleAndName));
            Button removeButton = new Button("Remove", e -> {
                rolesAndNames.remove(roleAndName);
                refreshRolesLayout(rolesLayout, rolesAndNames);
            });
            removeButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            roleLayout.add(removeButton);
            rolesLayout.add(roleLayout);
        }
    }

    private void updateSprint(Sprint sprint, String name, String startDate, String  endDate, List<String> rolesAndNames) {
        sprint.setDetails(name, startDate, endDate);
        sprint.setRolesAndNames(rolesAndNames);
        try {
            FirebaseService.updateSprint(sprint);
            Notification.show("Sprint updated successfully", 3000, Notification.Position.MIDDLE);
        } catch (ExecutionException | InterruptedException e) {
            Notification.show("Error updating sprint: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }

    private void updateSprintStatus(Sprint sprint, Sprint.SprintStatus newStatus, Button notStartedButton, Button activeButton, Button completedButton) {



        if (!sprint.isActive() && newStatus == Sprint.SprintStatus.ACTIVE) {

            if(LocalDate.now().isBefore(LocalDate.parse(sprint.getStartDate())) || LocalDate.now().isAfter(LocalDate.parse(sprint.getEndDate()))){
                Notification.show("Unable to activate sprint as the sprint period is not started or the sprint is already ended.", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (activeSprint.isEmpty()) {
                activeSprint.add(sprint);
                Sprint sprintActive = activeSprint.getFirst();
                sprintActive.initialiseBurnDownChat();

                try {
                    FirebaseService.updateSprint(sprintActive);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                for (SprintBoardUserStory userStory : sprintActive.getUserStories()) {

                    assert userStory != null;
                    SprintBacklogUserStory newBacklogUserStory = new SprintBacklogUserStory();
                    newBacklogUserStory.setDetails(sprintActive.getName(), userStory.getUserStory(), userStory.getUserStoryPoint(), userStory.getPriority(), userStory.getTags(), userStory.getAssignee());

                    if (newBacklogUserStory.getTaskName() != null) {
                        try {
                            FirebaseService.saveSprintBacklogUserStory(newBacklogUserStory);
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            else{
                Notification.show("A sprint called \"" + activeSprint.getFirst().getName() + "\" has been started, can't have more than 1 active sprint.", 3000, Notification.Position.MIDDLE);
                return;
            }
        }
        else if (sprint.isActive() && newStatus != Sprint.SprintStatus.ACTIVE  && !activeSprint.isEmpty()){
            try {
                FirebaseService.clearAllSprintBacklogUserStories();
                activeSprint = new ArrayList<Sprint>();
                sprint.removeBurnDownChat();
                FirebaseService.updateSprint(sprint);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        else if (newStatus != Sprint.SprintStatus.ACTIVE && !activeSprint.isEmpty()){
            try {
                FirebaseService.clearAllSprintBacklogUserStories();
                sprint.removeBurnDownChat();
                FirebaseService.updateSprint(sprint);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            activeSprint = new ArrayList<Sprint>();
        }

        updateStatusDetails(sprint, newStatus, notStartedButton, activeButton, completedButton);

    }

    private void updateStatusDetails(Sprint sprint, Sprint.SprintStatus newStatus, Button notStartedButton, Button activeButton, Button completedButton) {
        sprint.setStatus(newStatus);

        // Update UI
        updateButtonStates(sprint, notStartedButton, activeButton, completedButton);

        // Update in database
        try {
            FirebaseService.updateSprint(sprint);
        } catch (ExecutionException | InterruptedException e) {
            Notification.show("Error updating sprint: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }

    private void updateButtonStates(Sprint sprint, Button notStartedButton, Button activeButton, Button completedButton) {
        // Enable all buttons first
        notStartedButton.setEnabled(true);
        activeButton.setEnabled(true);
        completedButton.setEnabled(true);

        // Disable and highlight the current state
        if (sprint.isNotStarted()) {
            notStartedButton.setEnabled(false);
            notStartedButton.getStyle().set("background-color", "#e0e0e0");
        } else if (sprint.isActive()) {
            activeButton.setEnabled(false);
            activeButton.getStyle().set("background-color", "#e0e0e0");
        } else if (sprint.isCompleted()) {
            completedButton.setEnabled(false);
            completedButton.getStyle().set("background-color", "#e0e0e0");
        }

        // Reset background color for enabled buttons
        notStartedButton.getStyle().set("background-color", notStartedButton.isEnabled() ? "white" : "#e0e0e0");
        activeButton.getStyle().set("background-color", activeButton.isEnabled() ? "white" : "#e0e0e0");
        completedButton.getStyle().set("background-color", completedButton.isEnabled() ? "white" : "#e0e0e0");
    }

    private HorizontalLayout createScrollableUserStoriesContainer(Sprint sprint) {
        HorizontalLayout container = new HorizontalLayout();
        container.setWidthFull();
        container.setHeight("220px"); // Adjust as needed
        container.setSpacing(false);
        container.setPadding(false);

        Button leftArrow = new Button("<", e -> scrollUserStories(container, -1));
        styleArrowButton(leftArrow);

        Div storiesScroller = new Div();
        storiesScroller.setWidthFull();
        storiesScroller.setHeight("100%");
        storiesScroller.getStyle()
                .set("display", "flex")
                .set("overflow-x", "hidden")
                .set("flex-wrap", "nowrap");

        for (SprintBoardUserStory story : sprint.getUserStories()) {
            storiesScroller.add(createUserStoryComponent(sprint, story));
        }

        Button rightArrow = new Button(">", e -> scrollUserStories(container, 1));
        styleArrowButton(rightArrow);

        container.add(leftArrow, storiesScroller, rightArrow);
        container.expand(storiesScroller);

        return container;
    }


    private void styleArrowButton(Button button) {
        button.getStyle()
                .set("background", "transparent")
                .set("border", "none")
                .set("color", "#888")
                .set("font-size", "20px")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease")
                .set("padding", "5px")
                .set("align-self", "center");

        // Hover effect
        button.getElement().executeJs(
                "this.addEventListener('mouseover', () => {" +
                        "  this.style.background = '#ddd';" +
                        "  this.style.color = '#333';" +
                        "});" +
                        "this.addEventListener('mouseout', () => {" +
                        "  this.style.background = 'transparent';" +
                        "  this.style.color = '#888';" +
                        "});"
        );
    }


    private void scrollUserStories(HorizontalLayout container, int direction) {
        Div storiesScroller = (Div) container.getComponentAt(1);
        double scrollAmount = direction * 150; // Adjust based on card width
        storiesScroller.getElement().executeJs(
                "this.scrollLeft += $0", scrollAmount);
    }

    /**
    private void openAddUserStoryDialog(Sprint sprint) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add User Story");

        VerticalLayout dialogLayout = new VerticalLayout();
        TextField storyTitle = new TextField("User Story");
        TextField storyDescription = new TextField("Story Point");

        ComboBox<String> statusComboBox = new ComboBox<>("Tags");
        statusComboBox.setItems("To Do", "In Progress", "Done"); // Define the status options
        statusComboBox.setPlaceholder("Select Status");

        Button saveButton = new Button("Save", e -> {
            if (!storyTitle.isEmpty() && !storyDescription.isEmpty()) {
                String storyId = UUID.randomUUID().toString();
                SprintBoardUserStory newStory = new SprintBoardUserStory();
                newStory.setDetails(storyTitle.getValue(), storyDescription.getValue(), storyId, statusComboBox.getValue(), sprint.getName());
                sprint.addUserStory(newStory);
                renderUserStory(sprint, newStory);
                dialog.close();
                try {
                    FirebaseService.updateSprint(sprint);
                    refreshSprintBoard();
                } catch (ExecutionException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                Notification.show("Please fill in all fields");
            }
        });

        dialogLayout.add(storyTitle, storyDescription, saveButton);
        dialog.add(dialogLayout);
        dialog.open();
    }
     */

    private void renderUserStory(Sprint sprint, SprintBoardUserStory story) {
        sprintContainer.getChildren()
                .filter(component -> component instanceof VerticalLayout)
                .map(component -> (VerticalLayout) component)
                .filter(layout -> layout.getComponentAt(0) instanceof HorizontalLayout
                        && ((HorizontalLayout) layout.getComponentAt(0)).getComponentAt(0) instanceof H2
                        && ((H2) ((HorizontalLayout) layout.getComponentAt(0)).getComponentAt(0)).getText().equals(sprint.getName()))
                .findFirst()
                .ifPresent(sprintBox -> {
                    // Find the user stories container (it should be the last component in the sprint box)
                    HorizontalLayout userStoriesContainer = (HorizontalLayout) sprintBox.getComponentAt(sprintBox.getComponentCount() - 1);
                    Div storiesScroller = (Div) userStoriesContainer.getComponentAt(1);
                    storiesScroller.add(createUserStoryComponent(sprint, story));
                    // Ensure the UI updates
                    userStoriesContainer.getElement().executeJs("this.scrollLeft = this.scrollWidth");
                });
    }

    private Div createUserStoryComponent(Sprint sprint, SprintBoardUserStory story) {
        Div storyLayout = new Div();
        storyLayout.setWidth("200px"); // Fixed width
        storyLayout.setHeight("180px"); // Fixed height
        storyLayout.getStyle()
                .set("border", "1px solid #ddd")
                .set("padding", "10px")
                .set("margin-right", "10px")
                .set("flex-shrink", "0") // Prevent shrinking
                .set("overflow", "hidden") //
                .set("background-color", "#1bd1bb"); // user story box colour

        H3 userStoryTitle = new H3(story.getUserStory());
        userStoryTitle.getStyle().set("margin-top", "0");

        Paragraph assignee = new Paragraph("Story Points: " + story.getUserStoryPoint());
        Paragraph tags = new Paragraph("Tags: " + String.join(", ", story.getTags()));
        Paragraph priority = new Paragraph("Priority: " + story.getPriority());

        storyLayout.add(userStoryTitle, assignee, tags, priority);
        return storyLayout;
    }

    private void setupUserStoriesDisplay() {
        userStoriesDisplay.getStyle()
                .set("display", "flex") // Flex layout
                .set("flex-direction", "row") // Row direction
                .set("flex-wrap", "wrap") // Wrap if needed
                .set("overflow-x", "auto") // Horizontal scrolling
                .set("margin", "0");
    }

    private void showDeleteConfirmationDialog(Sprint sprint, SprintBoardUserStory userStory) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setWidth("300px");

        // Create confirmation message
        Div message = new Div();
        message.setText("Are you sure you want to delete this user story: " + userStory.getUserStory() + "?");

        // Create buttons for confirmation
        Button confirmButton = new Button("Yes", event -> {
            deleteUserStory(sprint, userStory); // Call your method to delete the user story
            Notification.show("User story deleted", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            confirmationDialog.close();
        });

        Button cancelButton = new Button("No", event -> {
            confirmationDialog.close();
        });

        // Add components to the dialog
        confirmationDialog.add(message, confirmButton, cancelButton);
        confirmationDialog.open(); // Show the dialog
    }

    // Method to delete the user story (implement as per your logic)
    private void deleteUserStory(Sprint sprint, SprintBoardUserStory userStory) {
        try {
            sprint.deleteUserStory(userStory);
            // Call the delete method with the user story ID
            FirebaseService.updateSprint(sprint); // Assuming userStory has a method getId()

            // Refresh the backlog display after deletion
            refreshSprintBoard();
        } catch (ExecutionException | InterruptedException e) {
            // Handle any exceptions, such as showing an error notification
            Notification.show("Error deleting user story: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }

    private void deleteSprints() throws ExecutionException, InterruptedException {
        List<Sprint> sprintsToDelete = sprints.stream()
                .filter(Sprint::isChecked)
                .collect(Collectors.toList());

        if (sprintsToDelete.isEmpty()) {
            Notification.show("No sprints selected for deletion", 3000, Notification.Position.MIDDLE);
            return;
        }

        long sprintContainerChildrenCount = sprintContainer.getChildren().count();
        if (sprintContainerChildrenCount > 0) {
            // Store the original positions before deletion
            for (Sprint sprint : sprintsToDelete) {
                FirebaseService.deleteSprint(sprint.getName());
                originalPositions.put(sprint, sprints.indexOf(sprint));
            }

            deletedSprints.addAll(sprintsToDelete);
            sprints.removeAll(sprintsToDelete);
            refreshSprintBoard();
            Notification.show(sprintsToDelete.size() + " sprint(s) deleted", 3000, Notification.Position.MIDDLE);
        } else {
            Notification.show("No sprints to delete", 3000, Notification.Position.MIDDLE);
        }
    }

    private void undoDeleteSprints() {
        if (deletedSprints.isEmpty()) {
            Notification.show("No sprints to undo", 3000, Notification.Position.MIDDLE);
            return;
        }

        for (Sprint restoredSprint : deletedSprints) {
            Integer originalIndex = originalPositions.get(restoredSprint);
            if (originalIndex != null && originalIndex <= sprints.size()) {
                sprints.add(originalIndex, restoredSprint);
            } else {
                // If the original position is not available, add to the end
                sprints.add(restoredSprint);
            }
            try {
                FirebaseService.saveSprint(restoredSprint);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        deletedSprints.clear();
        originalPositions.clear();
        refreshSprintBoard();
        Notification.show("Deleted sprints restored", 3000, Notification.Position.MIDDLE);
    }

    public void refreshSprintBoard() {
        // Store the first child (which should be the H2 title)
        com.vaadin.flow.component.Component title = sprintContainer.getComponentAt(0);

        // Store the second child (which should be the HorizontalLayout with buttons)
        Component buttonWrapper = sprintContainer.getComponentAt(1);

        // Clear all other components
        sprintContainer.removeAll();

        // Add back the title and buttons
        sprintContainer.add(title, buttonWrapper);

        // Re-render all sprints
        for (Sprint sprint : sprints) {
            renderSprint(sprint);
        }
    }
}