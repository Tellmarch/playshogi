package com.playshogi.website.gwt.client.ui;

import java.util.Date;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.UserLoggedInEvent;
import com.playshogi.website.gwt.client.events.UserLoggedOutEvent;
import com.playshogi.website.gwt.client.services.LoginService;
import com.playshogi.website.gwt.client.services.LoginServiceAsync;

public class LoginView extends Composite implements ClickHandler, AsyncCallback<String> {

	private final Button loginButton;

	private final LoginServiceAsync loginService = GWT.create(LoginService.class);

	private final TextBox usernameTextBox;
	private final PasswordTextBox passwordTextBox;

	private EventBus eventBus;

	public LoginView() {
		Grid grid = new Grid(2, 2);
		grid.setWidget(0, 0, new HTML("Username:"));
		usernameTextBox = new TextBox();
		grid.setWidget(0, 1, usernameTextBox);
		grid.setWidget(1, 0, new HTML("Password:"));
		passwordTextBox = new PasswordTextBox();
		grid.setWidget(1, 1, passwordTextBox);

		HorizontalPanel buttonPanel = new HorizontalPanel();

		loginButton = new Button("Login");
		loginButton.addClickHandler(this);
		buttonPanel.add(loginButton);
		buttonPanel.add(new Button("Register"));

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(grid);
		verticalPanel.add(buttonPanel);

		initWidget(verticalPanel);
	}

	@Override
	public void onClick(final ClickEvent event) {
		Object source = event.getSource();
		if (source == loginButton) {
			loginService.login(usernameTextBox.getText(), passwordTextBox.getText(), this);
		}
	}

	@Override
	public void onSuccess(final String result) {
		GWT.log("Received answer from server login service: " + result);
		if (LoginService.UNKNOWN_USERNAME.equals(result)) {
			GWT.log("Unknown username");
			eventBus.fireEvent(new UserLoggedOutEvent());
			Window.alert("Access Denied. Check your username and password.");
		} else if (LoginService.INVALID_PASSWORD.equals(result)) {
			GWT.log("Invalid password");
			Window.alert("Access Denied. Check your username and password.");
		} else if (result != null) {
			GWT.log("Correct login");
			String sessionID = result;
			final long DURATION = 1000 * 60 * 60 * 24 * 14;
			Date expires = new Date(System.currentTimeMillis() + DURATION);
			Cookies.setCookie("sid", sessionID, expires, null, "/", false);
			eventBus.fireEvent(new UserLoggedInEvent());

		} else {
			GWT.log("null result from login");
		}
	}

	@Override
	public void onFailure(final Throwable caught) {
		GWT.log("ERROR trying to login user", caught);
		Window.alert("Access Denied. Check your username and password.");
	}

	public void activate(final EventBus eventBus) {
		GWT.log("Activating login view");
		this.eventBus = eventBus;
	}

}
