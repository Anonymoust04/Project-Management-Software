package com.example.application.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import com.example.application.FirebaseService;
import com.example.application.entity.CheckBox;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("main")
public class MainView extends VerticalLayout {

    private TextField textField = new TextField();

    private VerticalLayout verticalLayout = new VerticalLayout();
    private HashMap<CheckBox, Checkbox> visualCheckBoxList = new HashMap<>();

    ProductBacklogView productBacklogView = new ProductBacklogView();
//    SprintBoardView sprintBoardView = new SprintBoardView();

    private RouterLink productBacklogLink = new RouterLink("Go to Product Backlog", ProductBacklogView.class);
    private Button productBacklogButton = new Button(productBacklogLink);

    private RouterLink sprintBoardLink = new RouterLink("Go to Sprint Board", SprintBoardView.class);
    private Button sprintBoardButton = new Button(sprintBoardLink);


    public MainView() throws ExecutionException, InterruptedException {

        ArrayList<CheckBox> checkBoxArrayList = retreivePreviousCheckBoxes();

        Button button = new Button("Add Task");

        button.addClickListener(click -> {
            Checkbox checkbox = new Checkbox(textField.getValue());
            CheckBox checkBox1 = new CheckBox();
            checkBox1.setLabelElement(textField.getValue());
            checkbox.setValue(checkBox1.getIsChecked());
            checkbox.addValueChangeListener(event -> checkBox1.setIsChecked(event.getValue()));
            checkBoxArrayList.add(checkBox1);
            visualCheckBoxList.put(checkBox1, checkbox);
            try {
                FirebaseService.saveCheckBox(checkBox1);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            verticalLayout.add(checkbox);
            textField.setValue("");
                }
        );
        button.addClickShortcut(Key.ENTER);

        Button deleteTask = new Button("Delete", e -> {
            try {
                deleteCheckBoxes(checkBoxArrayList);
            } catch (ExecutionException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        deleteTask.addClassName("delete-button-red");
        deleteTask.addThemeVariants(ButtonVariant.LUMO_ERROR);

        add(new H1("Admin Dashboard"),
                verticalLayout,
                new HorizontalLayout(textField, button, deleteTask),
                productBacklogButton, sprintBoardButton
        );
    }

    private ArrayList<CheckBox> retreivePreviousCheckBoxes() throws ExecutionException, InterruptedException{
        ArrayList<CheckBox> checkBoxArrayList = FirebaseService.getCheckBoxDetails();

        if (checkBoxArrayList != null){ 
            for (CheckBox checkBox : checkBoxArrayList){
                Checkbox newCheckBox = new Checkbox(checkBox.getLabelElement());
                newCheckBox.setValue(checkBox.getIsChecked());
                newCheckBox.addValueChangeListener(event -> checkBox.setIsChecked(event.getValue()));
                visualCheckBoxList.put(checkBox, newCheckBox);
                verticalLayout.add(newCheckBox);
                textField.setValue("");
            }
        }

        return checkBoxArrayList;
    }

    private void deleteCheckBoxes(ArrayList<CheckBox> checkBoxArrayList) throws ExecutionException, InterruptedException {
        if (checkBoxArrayList != null){
            for (CheckBox checkBox : checkBoxArrayList){
                if (checkBox.getIsChecked()){
                    FirebaseService.deleteCheckBox(checkBox.getLabelElement());
                    checkBoxArrayList.remove(checkBox);
                    visualCheckBoxList.remove(checkBox);
                }
            }  

            verticalLayout.removeAll();

            for (Checkbox checkbox : visualCheckBoxList.values()){
                verticalLayout.add(checkbox);
            }
        }
    }
}
