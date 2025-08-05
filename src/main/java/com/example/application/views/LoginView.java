package com.example.application.views;

import com.example.application.FirebaseService;
import com.example.application.entity.UserInfo;
import com.example.application.entity.UserSession;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Route("")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private ArrayList<UserInfo> users;
    private final UserSession userSession;

    @Autowired
    public LoginView(UserSession userSession) {
        this.userSession = userSession;
        loadUsers();

        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Div loginContainer = new Div();
        loginContainer.addClassNames("login-container");

        H1 title = new H1("Login");
        title.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.Margin.Bottom.MEDIUM, "login-title");

        TextField username = new TextField("Username");
        username.setPlaceholder("Enter your username");
        username.setWidth("100%");
        username.addClassName("login-field");

        PasswordField password = new PasswordField("Password");
        password.setPlaceholder("Enter your password");
        password.setWidth("100%");
        password.addClassName("login-field");

        Button loginButton = new Button("Login");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidth("100%");
        loginButton.addClassName("login-button");

        loginButton.addClickListener(e -> {
            UserInfo user = authenticateUser(username.getValue(), password.getValue());
            if (user != null) {
                userSession.setUser(user);
                if (userSession.isAdmin()) {
                    getUI().ifPresent(ui -> ui.navigate(AdminDashboardView.class));
                } else {
                    getUI().ifPresent(ui -> ui.navigate(ProductBacklogView.class));
                }
            } else {
                Notification.show("Invalid credentials");
            }
        });

        loginButton.addClickShortcut(Key.ENTER);

        loginContainer.add(title, username, password, loginButton);
        add(loginContainer);

        getElement().executeJs(
                "document.body.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (userSession.getUser() != null) {
            // User is already logged in, redirect to appropriate view
            if (userSession.isAdmin()) {
                event.forwardTo(AdminDashboardView.class);
            } else {
                event.forwardTo(ProductBacklogView.class);
            }
        }
    }

    private void loadUsers() {
        try {
            users = FirebaseService.getAllUserDetails();
        } catch (ExecutionException | InterruptedException e) {
            Notification.show("Error loading user data");
            throw new RuntimeException(e);
        }
    }

    private UserInfo authenticateUser(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}