package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.SessionInformation;

@Singleton
public class LoginView extends Composite implements ClickHandler, KeyUpHandler {

    private final Button loginButton;
    private final Button registerButton;

    private final TextBox usernameTextBox;
    private final PasswordTextBox passwordTextBox;

    @Inject
    SessionInformation sessionInformation;

    private final VerticalPanel loginPanel;
    private final HTML infoBox;

    public LoginView() {
        GWT.log("Creating login view");

        Grid grid = new Grid(2, 2);
        grid.setWidget(0, 0, new HTML("Username:"));
        usernameTextBox = new TextBox();
        usernameTextBox.addKeyUpHandler(this);
        grid.setWidget(0, 1, usernameTextBox);
        grid.setWidget(1, 0, new HTML("Password:"));
        passwordTextBox = new PasswordTextBox();
        passwordTextBox.addKeyUpHandler(this);
        grid.setWidget(1, 1, passwordTextBox);

        HorizontalPanel buttonPanel = new HorizontalPanel();

        loginButton = new Button("Login");
        loginButton.addClickHandler(this);
        buttonPanel.add(loginButton);
        registerButton = new Button("Register");
        registerButton.addClickHandler(this);
        buttonPanel.add(registerButton);

        loginPanel = new VerticalPanel();
        loginPanel.add(grid);
        loginPanel.add(new HTML("By registering, you agree to be bound by our <a href=\"terms.html\" " +
                "target=\"_blank\">Terms of Service and Privacy Policy</a>.</br>"));
        loginPanel.add(buttonPanel);

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(loginPanel);
        infoBox = new HTML("");
        verticalPanel.add(infoBox);

        initWidget(verticalPanel);
    }

    public void setLoginVisible(final boolean visible) {
        loginPanel.setVisible(visible);
    }

    public void setInfoText(final String text) {
        infoBox.setHTML(text);
    }

    @Override
    public void onClick(final ClickEvent event) {
        Object source = event.getSource();
        if (source == loginButton) {
            login();
        }
        if (source == registerButton) {
            register();
        }
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            login();
        }
    }

    private void login() {
        sessionInformation.login(usernameTextBox.getText(), passwordTextBox.getText());
    }

    private void register() {
        sessionInformation.register(usernameTextBox.getText(), passwordTextBox.getText());
    }

}
