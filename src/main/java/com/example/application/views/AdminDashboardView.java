package com.example.application.views;

import com.example.application.FirebaseService;
import com.example.application.entity.UserInfo;
import com.example.application.entity.UserSession;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.dependency.CssImport;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Route(value = "admin-dashboard", layout = MainLayout.class)
@CssImport("./styles/admin-dashboard.css")
public class AdminDashboardView extends VerticalLayout implements BeforeEnterObserver {

    private ArrayList<UserInfo> users;
    private Grid<UserInfo> grid;
    private final UserSession userSession;

    @Autowired
    public AdminDashboardView(UserSession userSession) {
        this.userSession = userSession;
        loadUsers();

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H2 header = new H2("User Management");
        add(header);

        grid = new Grid<>(UserInfo.class);
        grid.setColumns("username", "role");
        grid.addComponentColumn(this::createDeleteButton).setHeader("Actions");
        grid.setItems(users);
        grid.addClassName("user-grid");

        Button addUserButton = new Button("Add New User", new Icon(VaadinIcon.PLUS));
        addUserButton.addClickListener(e -> openAddUserDialog());
        addUserButton.addClassName("add-user-button");

        add(addUserButton, grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!userSession.isAdmin()) {
            event.forwardTo(ProductBacklogView.class);
        }
    }

    private Button createDeleteButton(UserInfo user) {
        Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
        deleteButton.addClassName("delete-button");
        deleteButton.addClickListener(e -> openDeleteConfirmDialog(user));
        return deleteButton;
    }

    private void openDeleteConfirmDialog(UserInfo user) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirm Delete");
        dialog.setText("Are you sure you want to delete user: " + user.getUsername() + "?");

        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(event -> {
            users.remove(user);
            try {
                FirebaseService.deleteUser(user.getUsername());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            grid.setItems(users);
        });

        dialog.open();
    }

    private void loadUsers() {
        try {
            users = FirebaseService.getAllUserDetails();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void openAddUserDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New User");

        VerticalLayout dialogLayout = new VerticalLayout();
        TextField usernameField = new TextField("Username");
        TextField passwordField = new TextField("Password");
        Select<String> roleSelect = new Select<>();
        roleSelect.setLabel("Role");
        roleSelect.setItems("USER", "ADMIN");
        roleSelect.setValue("USER");

        Button saveButton = new Button("Save", event -> {
            UserInfo newUser = new UserInfo();
            newUser.setUsername(usernameField.getValue());
            newUser.setPassword(passwordField.getValue());
            newUser.setRole(roleSelect.getValue());
            users.add(newUser);
            try {
                FirebaseService.setUser(newUser);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            grid.setItems(users);
            dialog.close();
        });

        dialogLayout.add(usernameField, passwordField, roleSelect, saveButton);
        dialog.add(dialogLayout);
        dialog.open();
    }
}