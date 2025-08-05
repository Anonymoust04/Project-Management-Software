package com.example.application.views;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.example.application.FirebaseInit;
import com.example.application.FirebaseService;
import com.example.application.entity.UserSession;
import com.example.application.views.MainView;
import com.example.application.entity.Sprint;
import com.example.application.entity.SprintBacklogUserStory;
import com.example.application.entity.SprintBoardUserStory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import static com.vaadin.flow.dom.Style.AlignSelf.START;
import com.vaadin.flow.component.dnd.EffectAllowed;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "sprint-backlog", layout = MainLayout.class)
@PageTitle("Sprint Backlog")
public class SprintBacklogView extends VerticalLayout implements HasUrlParameter<String>{

    private final UserSession userSession;


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String sprintName) {
    }

    private void setText(String format) {
    }

    private HorizontalLayout boardLayout;
    private List<SprintBacklogUserStory> userStories = new ArrayList<>();
    private String sprintId;

    private static final List<String> TAG_OPTIONS = Arrays.asList(
            "Front end", "Back end", "API", "Database", "Framework", "Testing", "UI", "UX"
    );

    private static final List<String> PRIORITY_OPTIONS = Arrays.asList(
            "Low", "Medium", "Important", "Urgent"
    );

    @Autowired
    public SprintBacklogView(UserSession userSession) throws Exception {
        this.userSession = userSession;
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        configureHeader();
        configureBoard();

        add(boardLayout);

        loadUserStories();
    }

    // THIS IS THE OLD IMPLEMENTATION

//    private void configureHeader() {
//        H2 header = new H2("Sprint Backlog");
//        header.getStyle().set("margin", "0").set("padding", "10px");
//        add(header);
//
//        // Create the Average Time Spent button
//        Button avgTimeButton = new Button("Average Time Spent");
//        avgTimeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        avgTimeButton.addClickListener(event -> {
//            // Logic for displaying average time spent
//            //openAverageTimeDialog();
//        });
//
//
//        Button barChartButton = new Button("Bar Chart");
//        barChartButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        barChartButton.addClickListener(event -> {
//            // Logic for displaying bar chart
//            openBarChartDialog();
//        });
//
//        HorizontalLayout buttonLayout = new HorizontalLayout(avgTimeButton, barChartButton);
//        buttonLayout.setSpacing(true);
//        buttonLayout.setAlignItems(Alignment.END);  // Align buttons to the right
//
//        // Create a layout for the header and buttons
//        HorizontalLayout headerLayout = new HorizontalLayout(header, buttonLayout);
//        headerLayout.setWidthFull();
//        headerLayout.setPadding(true);
//        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN); // Header on left, buttons on right
//
//        add(headerLayout);
//    }

    private void configureHeader() {
        H2 header = new H2("Sprint Backlog");
        header.getStyle().set("margin", "0").set("padding", "10px");

        HorizontalLayout headerLayout = new HorizontalLayout(header);
        headerLayout.setWidthFull();
        headerLayout.setPadding(true);
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        if (userSession.isAdmin()) {
            // Create the Average Time Spent button
            Button avgTimeButton = new Button("Average Time Spent");
            avgTimeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            avgTimeButton.addClickListener(event -> {
                // Logic for displaying average time spent
                //openAverageTimeDialog();
            });

            Button barChartButton = new Button("Bar Chart");
            barChartButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            barChartButton.addClickListener(event -> {
                // Logic for displaying bar chart
                openBarChartDialog();
            });

            HorizontalLayout buttonLayout = new HorizontalLayout(avgTimeButton, barChartButton);
            buttonLayout.setSpacing(true);
            buttonLayout.setAlignItems(Alignment.END);

            headerLayout.add(buttonLayout);
        }

        add(headerLayout);
    }


    // Method to handle bar chart logic
    private void openBarChartDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Bar Chart");

        // Content for bar chart logic (e.g., chart rendering)
        VerticalLayout dialogLayout = new VerticalLayout();
        // Add content related to the bar chart here

        Button closeButton = new Button("Close", event -> dialog.close());
        dialogLayout.add(closeButton);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void configureBoard() {
        boardLayout = new HorizontalLayout();
        boardLayout.setSizeFull();
        boardLayout.setPadding(false);
        boardLayout.setSpacing(true);

        String[] statuses = {"Not Started", "In Progress", "Completed"};
        for (String status : statuses) {
            VerticalLayout column = createStatusColumn(status);

            // Create centered header
            H3 header = new H3(status);
            header.getStyle()
                    .set("width", "100%")
                    .set("text-align", "center")
                    .set("margin-top", "10px")
                    .set("margin-bottom", "10px");

            column.addComponentAsFirst(header);
            boardLayout.add(column);
        }
    }

    private VerticalLayout createStatusColumn(String status) {
        VerticalLayout column = new VerticalLayout();
        column.setWidthFull(); //set fix width for column
        column.setHeight("100%");
        column.setPadding(false);
        column.setSpacing(false);
        column.getStyle()
                .set("background-color", getColumnColor(status))
                .set("border", "1px solid #ccc")
                .set("border-radius", "5px")
                .set("margin", "5px");



        Div storyContainer = new Div();
        storyContainer.setSizeFull();
        storyContainer.getStyle()
                .set("overflow-y", "auto")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center");


        column.add(storyContainer);

        Icon expandIcon = VaadinIcon.ANGLE_DOWN.create();
        expandIcon.getStyle().set("align-self", "center").set("margin-top", "auto");
        column.add(expandIcon);

        DropTarget<VerticalLayout> dropTarget = DropTarget.create(column);
        dropTarget.setActive(true);
        dropTarget.setDropEffect(DropEffect.MOVE);

        dropTarget.addDropListener(event -> {
            event.getDragData().ifPresent(data -> {
                if (data instanceof SprintBacklogUserStory) {
                    SprintBacklogUserStory story = (SprintBacklogUserStory) data;
                    String oldStatus = story.getStatus();
                    story.setStatus(status);
                    try {
                        // Update Firebase
                        FirebaseService.updateSprintBacklogUserStory(story);

                        // Update local state
                        userStories.removeIf(s -> s.getTaskName().equals(story.getTaskName()));
                        userStories.add(story);

                        // Update UI
                        updateBoard();
                    } catch (Exception e) {
                        // Revert changes if update fails
                        story.setStatus(oldStatus);
                        e.printStackTrace();
                    }
                }
            });
        });

        return column;
    }

    private void renderUserStories(SprintBacklogUserStory userStory){
        VerticalLayout userStoryBox = new VerticalLayout();
        userStoryBox.setWidthFull();
        userStoryBox.setPadding(true);
        userStoryBox.getStyle()
                .set("border", "1px solid #ccc")
                .set("padding", "10px")
                .set("margin-bottom", "20px")
                .set("background-color", "White");

        H2 sprintName = new H2(userStory.getTaskName());
        sprintName.getStyle().set("margin", "0");

        boardLayout.add(userStoryBox);
    }

    private MenuBar createDirectoryMenu() {
        MenuBar menuBar = new MenuBar();
        menuBar.addClassName("directory-menu");

        Icon directoryIcon = new Icon(VaadinIcon.CHEVRON_LEFT_SMALL);
        RouterLink linkToSprintBoard = new RouterLink("Back to Sprint Board", SprintBoardView.class);
        HorizontalLayout directoryLayout = new HorizontalLayout(directoryIcon, linkToSprintBoard);
        directoryLayout.setSpacing(true);
        directoryLayout.setAlignItems(Alignment.CENTER);

        MenuItem directoryMenuItem = menuBar.addItem(directoryLayout);

        return menuBar;
    }

    private String getColumnColor(String status) {
        return switch (status) {
            case "Not Started" -> "#F0F0FF";
            case "In Progress" -> "#FFFFD0";
            case "Completed" -> "#E6FFE6";
            default -> "#FFFFFF";
        };
    }

    private void loadUserStories() throws Exception {

        ArrayList<SprintBacklogUserStory> userStoriesFromDataBase = FirebaseService.getAllSprintBacklogUserStories();

        if (userStoriesFromDataBase != null) {
            userStories.addAll(userStoriesFromDataBase);
        };

        updateBoard();
    }

    private void updateBoard() {
        boardLayout.getChildren().forEach(column -> {
            if (column instanceof VerticalLayout) {
                VerticalLayout statusColumn = (VerticalLayout) column;
                Div storyContainer = (Div) statusColumn.getComponentAt(1);
                storyContainer.removeAll();

                String status = ((H3) statusColumn.getComponentAt(0)).getText();
                List<SprintBacklogUserStory> filteredStories = userStories.stream()
                        .filter(story -> story.getStatus().equalsIgnoreCase(status))
                        .toList();

                filteredStories.forEach(story -> storyContainer.add(createStoryCard(story)));
            }
        });
    }

    private HorizontalLayout createStoryCard(SprintBacklogUserStory story) {
        HorizontalLayout card = new HorizontalLayout();
        card.setWidth("90%");
        card.setSpacing(true);
        card.setPadding(true);
        card.setAlignItems(Alignment.CENTER);
        card.getStyle()
                .set("background-color", "#00CED1")
                .set("border-radius", "5px")
                .set("margin", "10px")
                .set("box-sizing", "border-box");

        Checkbox checkbox = new Checkbox();
        checkbox.getStyle().set("flex-shrink", "0"); //remain size of checkbox

        VerticalLayout storyInfo = new VerticalLayout();
        storyInfo.setPadding(false);
        storyInfo.setSpacing(false);
        storyInfo.setWidthFull();
        storyInfo.getStyle().set("overflow", "hidden"); // Hide overflow content

        H3 taskName = new H3(story.getTaskName());
        taskName.getStyle()
                .set("margin", "0")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("white-space", "nowrap");

        Span priority = new Span("Task Priority: " + story.getPriority());
        Span storyPoint = new Span("Story Point: " + story.getStoryPoint());

        // Tags
        FlexLayout tagsContainer = new FlexLayout();
        tagsContainer.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        tagsContainer.getStyle().set("margin-top", "5px");

        for (String tag : story.getTags()) {
            Span tagSpan = new Span(tag);
            tagSpan.getStyle()
                    .set("background-color", "#E0E0E0")
                    .set("border-radius", "4px")
                    .set("padding", "2px 4px")
                    .set("font-size", "0.8em")
                    .set("margin-right", "4px")
                    .set("margin-bottom", "4px");
            tagsContainer.add(tagSpan);
        }

        storyInfo.add(taskName, priority, storyPoint, tagsContainer);

        VerticalLayout actions = new VerticalLayout();
        actions.setPadding(false);
        actions.setSpacing(false);
        actions.setAlignItems(Alignment.END);

        Button editButton = new Button("Edit");
        Button viewButton = new Button("View");

        editButton.getElement().getStyle()
                .set("color", "#F0F0FF")
                .set("background-color", "#000000")
                .set("transition", "background-color 0.3s, color 0.3s");
        editButton.getElement().addEventListener("mouseover", e -> {
            editButton.getElement().getStyle()
                    .set("color", "#1676F3");
        });
        editButton.getElement().addEventListener("mouseout", e -> {
            editButton.getElement().getStyle()
                    .set("color", "#F0F0FF");
        });



        viewButton.getElement().getStyle()
                .set("color", "#F5F5DC")
                .set("background-color", "#000000")
                .set("transition", "background-color 0.3s, color 0.3s");
        viewButton.getElement().addEventListener("mouseover", e -> {
            viewButton.getElement().getStyle()
                    .set("color", "#1676F3");
        });
        viewButton.getElement().addEventListener("mouseout", e -> {
            viewButton.getElement().getStyle()
                    .set("color", "#F0F0FF");
        });

        editButton.addClickListener(event -> openEditTaskDialog(story));
        viewButton.addClickListener(event -> openViewTaskDialog(story));

        actions.add(editButton, viewButton);

        card.add(checkbox, storyInfo, actions);
        card.expand(storyInfo);

        DragSource<HorizontalLayout> cardDragSource = DragSource.create(card);
        cardDragSource.setDraggable(true);
        cardDragSource.addDragStartListener(event -> {
            event.setDragData(story);
        });

        cardDragSource.setEffectAllowed(EffectAllowed.MOVE);

        return card;
    }

    private void openViewTaskDialog(SprintBacklogUserStory userStory) {
        // Create a new dialog for viewing task details
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("View Task");

        // Create a vertical layout for organizing the dialog content
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);
        dialogLayout.setWidth("auto");

        // Create and configure read-only text field for each details
        TextField taskName = new TextField("Task Name");
        taskName.setValue(userStory.getTaskName());
        taskName.setReadOnly(true);
        taskName.setWidthFull();

        NumberField storyPoint = new NumberField("Story Point");
        storyPoint.setValue(Double.parseDouble(userStory.getStoryPoint()));
        storyPoint.setReadOnly(true);
        storyPoint.setWidthFull();

        TextField assignee = new TextField("Assignee");
        assignee.setValue(userStory.getAssignee());
        assignee.setReadOnly(true);
        assignee.setWidthFull();

        TextField tags = new TextField("Tags");
        tags.setValue(String.join(", ", userStory.getTags()));
        tags.setReadOnly(true);
        tags.setWidthFull();

        TextField priority = new TextField("Priority");
        priority.setValue(userStory.getPriority());
        priority.setReadOnly(true);
        priority.setWidthFull();

        NumberField totalLogTime = new NumberField("Total Log Time");
        totalLogTime.setValue(userStory.getTotalLogTime());
        totalLogTime.setReadOnly(true);
        totalLogTime.setWidthFull();

        TextField accumulationOfEfforts = new TextField("Accumulation of Efforts");
        accumulationOfEfforts.setValue(String.join( " + ", userStory.accumulationOfEffortStrList()));
        accumulationOfEfforts.setReadOnly(true);
        accumulationOfEfforts.setWidthFull();

        // close button for the dialog
        Button closeButton = new Button("Close", e -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Create a horizontal layout for the close button, aligned to the right
        HorizontalLayout buttonLayout = new HorizontalLayout(closeButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        // Add all components to the dialog layout
        dialogLayout.add(taskName, storyPoint, assignee, tags, priority, totalLogTime, accumulationOfEfforts, buttonLayout);
        dialog.add(dialogLayout);
        dialog.setWidth("750px");
        dialog.open();
    }
    private void openEditTaskDialog(SprintBacklogUserStory userStory) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit User Story");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);

        // Task Name
        TextField taskName = new TextField("Task Name");
        taskName.setValue(userStory.getTaskName());
        taskName.setWidthFull();
        taskName.setRequired(true);

        // Story Point
        NumberField storyPoint = new NumberField("Story Point");
        storyPoint.setValue(Double.parseDouble(userStory.getStoryPoint()));
        storyPoint.setStep(0.5);
        storyPoint.setMin(0);

        // Assignee
        TextField assignee = new TextField("Assignee");
        assignee.setValue(userStory.getAssignee());
        assignee.setWidthFull();

        // tags
        CheckboxGroup<String> tags = new CheckboxGroup<>("Tags");
        tags.setItems(TAG_OPTIONS);
        tags.setValue(new HashSet<>(userStory.getTags()));
        tags.setWidthFull();
        tags.setRequired(true);
        tags.setErrorMessage("Please select at least one tag");

        // Log Time Spent
        NumberField timeSpent = new NumberField("Time Spent");
//        storyPoint.setValue(userStory.getTotalLogTime());
//        storyPoint.setMin(0);
        timeSpent.setValue(userStory.getTotalLogTime());
        timeSpent.setMin(0);


//         ComboBox<String> priority = new ComboBox<>("Priority");
//         priority.setItems(PRIORITY_OPTIONS);
//         priority.setValue(taskItem.getPriority());
//         priority.setWidthFull();
        // priority
        ComboBox<String> priority = createPriorityComboBox(userStory.getPriority(), false);

        Button saveButton = new Button("Save", e -> {
            if (isFormValid(taskName, priority, tags)) {
                try {
                    if (!Objects.equals(userStory.getTaskName(), taskName.getValue())){
                        FirebaseService.deleteSprintBacklogUserStory(userStory.getTaskName());
                    }
                    userStory.updateDetails(taskName.getValue(), storyPoint.getValue().toString(), priority.getValue(), new ArrayList<>(tags.getValue()), assignee.getValue(), timeSpent.getValue());
                    FirebaseService.updateSprintBacklogUserStory(userStory);
                    userStories = new ArrayList<SprintBacklogUserStory>();
                    loadUserStories();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                dialog.close();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        dialogLayout.add(taskName, storyPoint, assignee, tags, priority, timeSpent, buttonLayout);
        dialog.add(dialogLayout);
        dialog.setWidth("750px");
        dialog.open();
    }

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
}
