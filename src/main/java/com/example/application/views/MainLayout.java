package com.example.application.views;

import com.example.application.entity.UserSession;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class MainLayout extends AppLayout {

    private final UserSession userSession;

    @Autowired
    public MainLayout(UserSession userSession) {
        this.userSession = userSession;
        createHeader();
        createDrawer();
    }

    private void createHeader() {

        Button logoutButton = createLogoutButton();

        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("drawer-toggle");

        HorizontalLayout header = new HorizontalLayout(toggle, logoutButton);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);
    }

    private void createDrawer() {
        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);

        tabs.add(createTab(VaadinIcon.CLIPBOARD, "Product Backlog", ProductBacklogView.class));
        tabs.add(createTab(VaadinIcon.TASKS, "Sprint Board", SprintBoardView.class));
//        tabs.add(createTab(VaadinIcon.LIST, "Sprint Backlog", SprintBacklogView.class));

        if (userSession.isAdmin()) {
            tabs.add(createTab(VaadinIcon.USERS, "Admin Dashboard", AdminDashboardView.class));
        }

        addToDrawer(tabs);
    }

    private Tab createTab(VaadinIcon viewIcon, String viewName, Class<? extends com.vaadin.flow.component.Component> navigationTarget) {
        Icon icon = viewIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("margin-inline-start", "var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs)");

        RouterLink link = new RouterLink();
        link.add(icon, new Span(viewName));
        link.setRoute(navigationTarget);
        link.setTabIndex(-1);

        return new Tab(link);
    }

    private Button createLogoutButton() {
        Button logoutButton = new Button("Logout", new Icon(VaadinIcon.SIGN_OUT));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        logoutButton.addClassName("logout-button");
        logoutButton.getStyle().set("margin-left", "auto");
        logoutButton.addClickListener(e -> logout());
        return logoutButton;
    }

    private void logout() {
        userSession.setUser(null);
        UI current = UI.getCurrent();
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        if (vaadinSession != null) {
            vaadinSession.close();
        }
        if (current != null) {
            current.getPage().setLocation("/");
        }
    }
}