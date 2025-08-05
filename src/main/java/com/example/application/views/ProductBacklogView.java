package com.example.application.views;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.UUID;

//import org.hibernate.validator.constraints.UUID;

import com.example.application.entity.*;

import com.vaadin.flow.router.RouterLink;

import com.example.application.FirebaseService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Product Backlog")
@Route(value = "product-backlog", layout = MainLayout.class)
@CssImport("./styles/product-backlog-styles.css")
public class ProductBacklogView extends VerticalLayout{

    private VerticalLayout taskList;
    private List<TaskItem> tasks = new ArrayList<>();
    private Random random = new Random();
    private String currentSortMethod = "Recent";

    private SprintBoardView sprintBoardView;


    private Dictionary<TaskItem, Task> task_N_idDict = new Hashtable<>();

    private static final List<String> TAG_OPTIONS = Arrays.asList(
            "Front end", "Back end", "API", "Database", "Framework", "Testing", "UI", "UX"
    );
    private static final List<String> PRIORITY_OPTIONS = Arrays.asList(
            "Low", "Medium", "Important", "Urgent"
    );

    //sprint2
    //sorting
    private boolean isAscending = true;

    // filtering
    private Set<String> selectedFilters = new HashSet<>();
    private List<TaskItem> filteredTasks = new ArrayList<>();



    public ProductBacklogView() {
//        add(new H2("Product Backlog"));
        addClassName("product-backlog-view");
        setSpacing(false);
        setPadding(false);
        setSizeFull();

        filteredTasks = new ArrayList<>();
    }

    @PostConstruct
    private void init() throws ExecutionException, InterruptedException {
        configureHeader();
        configureContent();
    }


    private void configureHeader() {
        Div headerDiv = new Div();
        headerDiv.addClassName("product-backlog-header");
        headerDiv.setWidthFull();

        H2 headerText = new H2("Product Backlog");
        headerText.addClassName("product-backlog-header-text");

        headerDiv.add(headerText);
        add(headerDiv);
    }

    private void applyFilters() {
        if (selectedFilters.isEmpty()) {
            filteredTasks = new ArrayList<>(tasks);
        } else {
            String selectedTag = selectedFilters.iterator().next(); // Get the single selected tag
            filteredTasks = tasks.stream()
                    .filter(task -> task.getTags().contains(selectedTag))
                    .collect(Collectors.toList());
        }
        refreshTaskList();
    }

    private void configureContent() throws ExecutionException, InterruptedException {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(true);
        content.setSpacing(true);

        configureTaskLabels(content);
        configureTaskList(content);
        configureActionButtons(content);

        add(content);
        retriveRecentSortingMethod();
        retrievePreviousTasks();
        retrieveFilters();
    }

    private MenuBar createFilterMenu() {
        MenuBar filterMenu = new MenuBar();
        MenuItem filterItem = filterMenu.addItem(new Icon(VaadinIcon.FILTER));
        filterItem.getElement().setAttribute("aria-label", "Filter tasks");

        // Make the icon more visible
        filterItem.getElement().getStyle().set("color", "var(--lumo-primary-color)");


        SubMenu filterSubMenu = filterItem.getSubMenu();


        filterSubMenu.addItem("Clear Filters", e -> {
            selectedFilters.clear();
            applyFilters();
        });

//        filterSubMenu.addItem(new Hr()); // Add a separator

        VerticalLayout tagLayout = new VerticalLayout();
        tagLayout.setSpacing(false);
        tagLayout.setPadding(false);

        List<Checkbox> checkboxes = new ArrayList<>();

        for (String tag : TAG_OPTIONS) {
            Checkbox checkbox = new Checkbox(tag);
            checkbox.addValueChangeListener(event -> {
                if (event.getValue()) {
                    // Uncheck all other checkboxes
                    checkboxes.stream()
                            .filter(cb -> cb != checkbox)
                            .forEach(cb -> cb.setValue(false));

                    selectedFilters.clear();
                    selectedFilters.add(tag);
                } else {
                    selectedFilters.remove(tag);
                }
                applyFilters();
            });
            checkboxes.add(checkbox);
            tagLayout.add(checkbox);
        }

        filterSubMenu.addItem(tagLayout);

        return filterMenu;
    }

    private void configureTaskLabels(VerticalLayout content) {
        HorizontalLayout headerLabels = new HorizontalLayout();
        headerLabels.setWidthFull();
        headerLabels.setPadding(true);
        headerLabels.setSpacing(true);

        Span taskLabel = createHeaderLabel("Task");

        Span storyPointsLabel = createHeaderLabel("Story Point");

        HorizontalLayout tagsLayout = new HorizontalLayout();
        Span tagsLabel = createHeaderLabel("Tags");
        MenuBar filterMenu = createFilterMenu();

        filterMenu.getStyle().set("margin-left", "5px"); // Add some space between the label and the icon

        tagsLayout.add(tagsLabel, filterMenu);
        tagsLayout.setAlignItems(Alignment.CENTER);

        Span prioritiesLabel = createHeaderLabel("Task Priorities");

        //placeholder for action
        Span actionPlaceHolder = new Span();
        actionPlaceHolder.setWidth("100px");

        headerLabels.add(taskLabel, storyPointsLabel, tagsLayout, prioritiesLabel, actionPlaceHolder);


        // Adjust the positioning of the labels
        taskLabel.getStyle().set("flex-grow", "1");
        storyPointsLabel.getStyle().set("flex-basis", "50px").set("flex-shrink", "0");
        tagsLayout.getStyle().set("flex-basis", "200px");
        tagsLayout.getStyle().set("flex-shrink", "0");
        prioritiesLabel.getStyle().set("flex-basis", "100px");
        prioritiesLabel.getStyle().set("flex-shrink", "0");

        content.add(headerLabels);
    }

    private Span createHeaderLabel(String text) {
        Span label = new Span(text);
        label.getStyle().set("color", "var(--lumo-secondary-text-color)");
        return label;
    }

    private void configureTaskList(VerticalLayout content) {
        taskList = new VerticalLayout();
        taskList.setSpacing(true);
        taskList.setPadding(false);
        taskList.setSizeFull();
        content.add(taskList);
        content.setFlexGrow(1, taskList);
    }

    private void configureActionButtons(VerticalLayout content) {
        Button moveToSprint = new Button("Move to Sprint", e -> moveTaskToSprint());
        moveToSprint.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button addTask = new Button("Add", e -> openAddTaskDialog());
        addTask.addClassName("add-button-green");
        addTask.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button deleteTask = new Button("Delete", e -> {
            try {
                deleteCheckedTasks();
            } catch (ExecutionException | InterruptedException ex) {
                Notification.show("Error deleting tasks: " + ex.getMessage(),
                        3000, Notification.Position.MIDDLE);
            }
        });
        deleteTask.addClassName("delete-button-red");
        deleteTask.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button trackHistoryButton = new Button("Track History", new Icon(VaadinIcon.CLOCK));
        trackHistoryButton.addClickListener(e -> showHistoryForSelectedTasks());

        HorizontalLayout rightButtonLayout = new HorizontalLayout(addTask, deleteTask);
        rightButtonLayout.setJustifyContentMode(JustifyContentMode.END);

        MenuBar sortMenu = createSortMenu();

        HorizontalLayout buttonLayout = new HorizontalLayout(sortMenu, rightButtonLayout, trackHistoryButton, moveToSprint);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        content.add(buttonLayout);
    }

    private void moveTaskToSprint() {
        List<TaskItem> selectedTasks = tasks.stream()
                .filter(TaskItem::isChecked)
                .collect(Collectors.toList());

        if (selectedTasks.isEmpty()) {
            Notification.show("Please select tasks to move.", 3000, Notification.Position.MIDDLE);
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Move to Sprint");

        VerticalLayout dialogLayout = new VerticalLayout();
        ComboBox<String> sprintSelector = new ComboBox<>("Select Sprint");

        try {
            List<String> sprintNames = FirebaseService.getAllSprintNames();
            if (sprintNames.isEmpty()) {
                Notification.show("No sprints available. Please create a sprint first.",
                        3000, Notification.Position.MIDDLE);
                dialog.close();
                return;
            }
            sprintSelector.setItems(sprintNames);
        } catch (Exception e) {
            String errorMessage = "Error fetching sprints: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            Notification.show(errorMessage, 5000, Notification.Position.MIDDLE);
            dialog.close();
            return;
        }

        Button moveButton = new Button("Move", e -> {
            String selectedSprint = sprintSelector.getValue();
            if (selectedSprint == null) {
                Notification.show("Please select a sprint.", 3000, Notification.Position.MIDDLE);
                return;
            }
            moveTasksToSprint(selectedTasks, selectedSprint);
            dialog.close();
        });

        dialogLayout.add(sprintSelector, moveButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void moveTasksToSprint(List<TaskItem> tasksToMove, String sprintName) {
        int successCount = 0;
        for (TaskItem task : tasksToMove) {
            Task taskObj = task_N_idDict.get(task);

            SprintBoardUserStory userStory = new SprintBoardUserStory();
            userStory.setDetails(
                    task.getName(),
                    task.getAssignee(),
                    UUID.randomUUID().toString(),
                    task.getStoryPoint().toString(),  // Default status
                    "NOT STARTED",
                    sprintName,
                    new ArrayList<>(task.getTags()),
                    task.getPriority()
            );

            try {
                FirebaseService.deleteProductBacklogTask(taskObj.getTaskId());
                FirebaseService.addUserStoryToSprint(sprintName, userStory);
                task_N_idDict.remove(task);
                tasks.remove(task);
                successCount++;
            } catch (ExecutionException | InterruptedException ex) {
                Notification.show("Error moving task '" + task.getName() + "': " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
            }
        }

        applyFilters(); // Refresh the Product Backlog view
        if (sprintBoardView != null) {
            sprintBoardView.refreshSprintBoard(); // Refresh the Sprint Board view
        }

        if (successCount > 0) {
            Notification.show(successCount + " task(s) successfully moved to " + sprintName,
                    3000, Notification.Position.MIDDLE);
        }
        if (successCount < tasksToMove.size()) {
            Notification.show((tasksToMove.size() - successCount) + " task(s) failed to move. Check the error messages.",
                    5000, Notification.Position.MIDDLE);
        }
    }


    private MenuBar createSortMenu() {
        MenuBar menuBar = new MenuBar();
        menuBar.addClassName("sort-menu");

        Icon sortIcon = new Icon(VaadinIcon.SORT);
        Span sortLabel = new Span("Sort");
        HorizontalLayout sortLayout = new HorizontalLayout(sortIcon, sortLabel);
        sortLayout.setSpacing(false);
        sortLayout.setAlignItems(Alignment.CENTER);

        MenuItem sortMenuItem = menuBar.addItem(sortLayout);
        SubMenu sortSubMenu = sortMenuItem.getSubMenu();

        // sorting logic (sprint2)
        sortSubMenu.addItem("Priority (Low to High)", e -> {
            currentSortMethod = "Priority";
            isAscending = true;
            sortTasks();
        });
        sortSubMenu.addItem("Priority (High to Low)", e -> {
            currentSortMethod = "Priority";
            isAscending = false;
            sortTasks();
        });
        sortSubMenu.addItem("Recent (Oldest First)", e -> {
            currentSortMethod = "Recent";
            isAscending = true;
            sortTasks();
        });
        sortSubMenu.addItem("Recent (Newest First)", e -> {
            currentSortMethod = "Recent";
            isAscending = false;
            sortTasks();
        });

        return menuBar;
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
//        directorySubMenu.addItem(new RouterLink("Sprint Board", SprintBoardView.class));
//
//        return menuBar;
//    }

    private void retrievePreviousTasks() throws ExecutionException, InterruptedException {
        ArrayList<Task> previousTaskList = FirebaseService.getAllProductBacklogDetails();

        for (Task task: previousTaskList){
            if (task.getTaskName() != null) {
                Set<String> tagSet = new HashSet<>(task.getTaskTagList());
                TaskItem taskItem = new TaskItem(task.getTaskName(), task.getTaskStatus(), task.getTaskStages(), task.getTaskStoryPoint(), task.getTaskAssignee(), tagSet, task.getTaskPriority(), LocalDateTime.parse(task.getTaskCreatedAt()));
                taskItem.setId(task.getTaskId());
                tasks.add(taskItem);
                task_N_idDict.put(taskItem, task);
            }
        }
        filteredTasks = new ArrayList<>(tasks);
        refreshTaskList();
    }

    private void retriveRecentSortingMethod() throws ExecutionException, InterruptedException{
        if (FirebaseService.getSortWay() != null){
            SortWay sortWay = FirebaseService.getSortWay();
            currentSortMethod = sortWay.getCurrentSortMethod();
            isAscending = sortWay.getIsAscending();
        }
    }

    private void retrieveFilters() throws ExecutionException, InterruptedException {
        Filter filter = FirebaseService.getFilter();
        if (filter != null && !filter.getFilterList().isEmpty()) {
            selectedFilters = new HashSet<>();
            selectedFilters.add(filter.getFilterList().get(0)); // Add only the first filter
            applyFilters();
        }
    }

    private void sortTasks() {
        Comparator<TaskItem> comparator;
        comparator = switch (currentSortMethod) {
            case "Priority" -> Comparator.comparingInt(TaskItem::getPriorityOrder);
            default -> Comparator.comparing(TaskItem::getCreatedAt);
        };
        // isAscending ? "ascending" : "descending";
        tasks.sort(isAscending ? comparator : comparator.reversed());
        try {
            updateIdForTasks();
        } catch (ExecutionException | InterruptedException e) {
        }

        SortWay sortMethod = new SortWay();
        sortMethod.setDetails(currentSortMethod, isAscending);
        try {
            FirebaseService.saveSortWay(sortMethod);
        } catch (InterruptedException | ExecutionException e1) {
        }

        applyFilters();
        Notification.show("Sorted by " + currentSortMethod, 3000, Notification.Position.BOTTOM_START);
    }

    private void updateIdForTasks() throws ExecutionException, InterruptedException{
        Integer newTaskId = 0;
        FirebaseService.clearProductBacklogTask();
        for (TaskItem task:tasks){
            Task correspondingProductBacklog = task_N_idDict.get(task);
            task.setId(newTaskId);
            correspondingProductBacklog.setTaskId(newTaskId);
            FirebaseService.saveDetails(correspondingProductBacklog);
            task_N_idDict.put(task, correspondingProductBacklog);
            newTaskId++;
        }
    }

    private void openAddTaskDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New Task");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);

        // + Task Name
        TextField taskName = new TextField("Task Name");
        taskName.setWidthFull();
        taskName.setRequired(true); // required in the UI

        // + Task Status
        TextArea taskStatus = new TextArea("Task Status");
        taskStatus.getStyle().set("font-weight", "bold");
        taskStatus.setWidthFull();

        // Task Stages
        TextArea taskStages = new TextArea("Task Stages");
        taskStages.setWidthFull();

        // + Story Point
        NumberField storyPoint = new NumberField("Story Point");
        storyPoint.setStep(0.5);
        storyPoint.setMin(0);

        // + Assignee
        TextField assignee = new TextField("Assignee");
        assignee.setWidthFull();

        // Tags
        CheckboxGroup<String> tags = new CheckboxGroup<>("Tags");
        tags.getStyle().set("font-weight", "bold");
        tags.setItems(TAG_OPTIONS);
        tags.setWidthFull();
        tags.setRequired(true);  // Set tags as required
        tags.setErrorMessage("Please select at least one tag");

        // Priority
//        ComboBox<String> priority = new ComboBox<>("Priority");
//        priority.setItems(PRIORITY_OPTIONS); // dropdown selection for priority
//        priority.setWidthFull(); // occupy full width of the container
//        priority.setRequired(true); // required in the UI
        ComboBox<String> priority = createPriorityComboBox(null, true);

        Button saveButton = new Button("Save", e -> {
            try {
                addTask(
                        taskName.getValue(),
                        taskStatus.getValue(),
                        taskStages.getValue(),
                        storyPoint.getValue(),
                        assignee.getValue(),
                        tags.getValue(),
                        priority.getValue()
                );
            } catch (ExecutionException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            dialog.close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        dialogLayout.add(taskName, taskStatus, taskStages, storyPoint, assignee, tags, priority, buttonLayout);
        dialog.add(dialogLayout);
        dialog.setWidth("750px");
        dialog.open();
    }

    private boolean isFormValid(TextField taskName, ComboBox<String> priority, CheckboxGroup<String> tags) {
        boolean isValid = true;
        if (taskName.isEmpty()) {
            taskName.setInvalid(true);
            isValid = false;
        }
        if (priority.isEmpty()) {
            priority.setInvalid(true);
            isValid = false;
        }
        if (tags.isEmpty()) {
            tags.setInvalid(true);
            isValid = false;
        }
        return isValid;
    }


    private void addTask(String taskName, String taskStatus, String taskStages, Double storyPoint, String assignee, Set<String> tags, String priority) throws ExecutionException, InterruptedException {
        TaskItem taskItem = new TaskItem(taskName, taskStatus, taskStages, storyPoint, assignee, tags, priority, null);
        Task newTask = new Task();
        newTask.setDetails(assignee, taskName, taskStatus, taskStages, storyPoint, new ArrayList<>(tags), priority, (taskItem.getCreatedAt().toString()));
        task_N_idDict.put(taskItem, newTask);
        tasks.add(taskItem);
        sortTasks();
        applyFilters();
    }

    // helper method to set up the priority ComboBox in both dialog setup methods
    private ComboBox<String> createPriorityComboBox(String initialValue, boolean required) {
        ComboBox<String> priority = new ComboBox<>("Priority");
        priority.setItems(PRIORITY_OPTIONS);
        priority.setWidthFull();
        priority.setRequired(required);
        if (initialValue != null) {
            priority.setValue(initialValue);
        }
        return priority;
    }

    private void renderTask(TaskItem taskItem) {
        // Create main layout
        HorizontalLayout taskLayout = new HorizontalLayout();
        taskLayout.setWidthFull();
        taskLayout.setAlignItems(Alignment.CENTER);
        taskLayout.setPadding(true);
        taskLayout.setSpacing(true);

        // Set background color and rounded corners for the task item
        taskLayout.getStyle().set("background-color", getRandomColor());
        taskLayout.getStyle().set("border-radius", "var(--lumo-border-radius-m)");

        // Add checkbox for task selection
        Checkbox checkbox = new Checkbox();
        checkbox.setValue(taskItem.isSelected());
        checkbox.addValueChangeListener(event -> taskItem.setChecked(event.getValue()));

        // Create a button with the task name as its label, for viewing the task
        Button taskNameButton = new Button(taskItem.getName());
        taskNameButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        // Adjust the button's style
        taskNameButton.getStyle()
                .set("text-align", "left")
                .set("padding-left", "0")
                .set("margin-right", "auto");
        // Add a click listener to the button that opens a dialog to view the details of the task when clicked
        taskNameButton.addClickListener(e -> openViewTaskDialog(taskItem));

        // Story Point
        Span storyPointSpan = new Span(taskItem.getStoryPoint().toString());

        // Create layout for tags
        FlexLayout tagLayout = new FlexLayout();
        tagLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        tagLayout.setWidth("200px");

        taskItem.getTags().forEach(tag -> {
            Span tagSpan = new Span(tag);
            tagSpan.addClassName("tag");
            tagSpan.getStyle()
                    .set("margin", "2px")
                    .set("padding", "2px 5px")
                    .set("background-color", "black")
                    .set("color", "white")
                    .set("border-radius", "var(--lumo-border-radius-s)")
                    .set("font-size", "var(--lumo-font-size-s)");
            tagLayout.add(tagSpan);
        });


        // priority display
        Span prioritySpan = new Span(taskItem.getPriority());
        if (taskItem.getPriority() != null){
        prioritySpan.addClassName("priority-" + taskItem.getPriority().toLowerCase());}
        prioritySpan.setWidth("100px");

        // Add edit button
        Button editButton = new Button("Edit", e -> openEditTaskDialog(taskItem));
        editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        editButton.setWidth("100px");

        // Add all components to the task layout
        taskLayout.add(checkbox, taskNameButton, storyPointSpan, tagLayout, prioritySpan, editButton);
        // Add the task layout to the main task list
        taskList.add(taskLayout);
    }

    private void openEditTaskDialog(TaskItem taskItem) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Task");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);

        // Task Name
        TextField taskName = new TextField("Task Name");
        taskName.setValue(taskItem.getName());
        taskName.setWidthFull();
        taskName.setRequired(true);

        // Task Status
        TextArea taskStatus = new TextArea("Task Status");
        taskStatus.setValue(taskItem.getTaskStatus());
        taskStatus.setWidthFull();

        // Task Stages
        TextArea taskStages = new TextArea("Task Stages");
        taskStages.setValue(taskItem.getTaskStages());
        taskStages.setWidthFull();

        // Story Point
        NumberField storyPoint = new NumberField("Story Point");
        storyPoint.setValue(taskItem.getStoryPoint());
        storyPoint.setStep(0.5);
        storyPoint.setMin(0);

        // Assignee
        TextField assignee = new TextField("Assignee");
        assignee.setValue(taskItem.getAssignee());
        assignee.setWidthFull();

        // tags
        CheckboxGroup<String> tags = new CheckboxGroup<>("Tags");
        tags.setItems(TAG_OPTIONS);
        tags.setValue(taskItem.getTags());
        tags.setWidthFull();
        tags.setRequired(true);
        tags.setErrorMessage("Please select at least one tag");

//         ComboBox<String> priority = new ComboBox<>("Priority");
//         priority.setItems(PRIORITY_OPTIONS);
//         priority.setValue(taskItem.getPriority());
//         priority.setWidthFull();
        // priority
        ComboBox<String> priority = createPriorityComboBox(taskItem.getPriority(), false);

        Button saveButton = new Button("Save", e -> {
            if (isFormValid(taskName, priority, tags)) {
                try {
                    updateTask(taskItem,
                            taskName.getValue(),
                            taskStatus.getValue(),
                            taskStages.getValue(),
                            storyPoint.getValue(),
                            assignee.getValue(),
                            tags.getValue(),
                            priority.getValue());
                } catch (ExecutionException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                dialog.close();
                refreshTaskList();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        dialogLayout.add(taskName, taskStatus, taskStages, storyPoint, assignee, tags, priority, buttonLayout);
        dialog.add(dialogLayout);
        dialog.setWidth("750px");
        dialog.open();
    }

    private void openViewTaskDialog(TaskItem taskItem) {
        // Create a new dialog for viewing the details of the task
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("View Task");

        // Create a vertical layout for organizing the dialog content
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);
        dialogLayout.setWidth("auto");

        // Create and configure read-only text field for each details
        TextField taskName = new TextField("Task Name");
        taskName.setValue(taskItem.getName());
        taskName.setReadOnly(true);
        taskName.setWidthFull();

        TextArea taskStatus = new TextArea("Task Status");
        taskStatus.setValue(taskItem.getTaskStatus());
        taskStatus.setReadOnly(true);
        taskStatus.setWidthFull();

        TextArea taskStages = new TextArea("Task Stages");
        taskStages.setValue(taskItem.getTaskStages());
        taskStages.setReadOnly(true);
        taskStages.setWidthFull();

        NumberField storyPoint = new NumberField("Story Point");
        storyPoint.setValue(taskItem.getStoryPoint());
        storyPoint.setReadOnly(true);
        storyPoint.setWidthFull();

        TextField assignee = new TextField("Assignee");
        assignee.setValue(taskItem.getAssignee());
        assignee.setReadOnly(true);
        assignee.setWidthFull();

        TextField tags = new TextField("Tags");
        tags.setValue(String.join(", ", taskItem.getTags()));
        tags.setReadOnly(true);
        tags.setWidthFull();

        TextField priority = new TextField("Priority");
        priority.setValue(taskItem.getPriority());
        priority.setReadOnly(true);
        priority.setWidthFull();

        // close button for the dialog
        Button closeButton = new Button("Close", e -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Create a horizontal layout for the close button, aligned to the right
        HorizontalLayout buttonLayout = new HorizontalLayout(closeButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        // Add all components to the dialog layout
        dialogLayout.add(taskName, taskStatus, taskStages, storyPoint, assignee, tags, priority, buttonLayout);
        dialog.add(dialogLayout);
        dialog.setWidth("750px");
        dialog.open();
    }

    private void updateTask(TaskItem taskItem, String name, String taskStatus, String taskStages, Double storyPoint, String assignee, Set<String> tags, String priority) throws ExecutionException, InterruptedException {
        Task task = task_N_idDict.get(taskItem);
        task.updateDetails(assignee, name, taskStatus, taskStages, storyPoint, new ArrayList<>(tags), priority);
        FirebaseService.updateProductBacklogTask(task);
        task_N_idDict.put(taskItem, task);
        taskItem.setName(name);
        taskItem.setTaskStatus(taskStatus);
        taskItem.setTaskStages(taskStages);
        taskItem.setStoryPoint(storyPoint);
        taskItem.setAssignee(assignee);
        taskItem.setTags(tags);
        taskItem.setPriority(priority);
        sortTasks();
        applyFilters();
    }

    private void deleteCheckedTasks() throws ExecutionException, InterruptedException {
        List<TaskItem> tasksToRemove = tasks.stream()
                .filter(TaskItem::isChecked)
                .toList();

        if (tasksToRemove.isEmpty()) {
            Notification.show("No tasks selected for deletion", 3000, Notification.Position.MIDDLE);
            return;
        }

        for (TaskItem task: tasksToRemove){
            Task productBacklogTask = task_N_idDict.get(task);
            FirebaseService.deleteProductBacklogTask(productBacklogTask.getTaskId());
            task_N_idDict.remove(task);
        }

        tasks.removeAll(tasksToRemove);
        applyFilters();
        Notification.show(tasksToRemove.size() + " task(s) deleted", 3000, Notification.Position.MIDDLE);
    }

    private void refreshTaskList() {
        taskList.removeAll();
        filteredTasks.forEach(this::renderTask);
    }

    private String getRandomColor() {
        String[] colors = {
                "rgba(255, 255, 224, 0.5)",  // Light yellow
                "rgba(173, 216, 230, 0.5)",  // Light blue
                "rgba(230, 230, 250, 0.5)"   // Light purple
        };
        return colors[random.nextInt(colors.length)];
    }

    private void showHistoryForSelectedTasks() {
        List<TaskItem> selectedTask = tasks.stream()
                .filter(TaskItem::isChecked)
                .collect(Collectors.toList());

        if (selectedTask.isEmpty()) {
            Notification.show("No tasks selected", 3000, Notification.Position.MIDDLE);
            return;
        }
        else if (selectedTask.size() != 1){
            Notification.show("Can only select one task", 3000, Notification.Position.MIDDLE);
            return;
        }

        List<ChangeHistory> history = fetchHistoryForTasks(selectedTask.getFirst());

        if (history == null || history.isEmpty()){
            Notification.show("Error, no history log produced.", 3000, Notification.Position.MIDDLE);
            return;
        }

        showHistoryDialog(history);
    }

    private List<ChangeHistory> fetchHistoryForTasks(TaskItem task) {
        // This method should call your backend API to fetch the history
        // For now, we'll return a dummy list
        Task productBacklogTask = task_N_idDict.get(task);
        return productBacklogTask.getHistoryLogLst(); // Replace with actual fetching logic
    }

    private void showHistoryDialog(List<ChangeHistory> history) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Task History");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);

        Grid<ChangeHistory> grid = new Grid<>(ChangeHistory.class);
        grid.setItems(history);

        grid.setColumns("timestamp", "user", "taskName", "fieldChanged", "oldValue", "newValue");

        dialogLayout.add(grid);

        Button closeButton = new Button("Close", e -> dialog.close());
        dialogLayout.add(closeButton);

        dialog.add(dialogLayout);
        dialog.setWidth("1300px");
        dialog.setHeight("600px");
        dialog.open();
    }

    public ProductBacklogView(SprintBoardView sprintBoardView) {
        if (sprintBoardView == null) {
            System.err.println("Error: SprintBoardView is null during ProductBacklogView initialization.");
        }
        this.sprintBoardView = sprintBoardView; // Initialize the reference
    }

}
