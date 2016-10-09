package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.SessionInformation;

@Singleton
public class LoginView extends Composite implements ClickHandler {

	private final Button loginButton;

	private final TextBox usernameTextBox;
	private final PasswordTextBox passwordTextBox;

	@Inject SessionInformation sessionInformation;

	private final VerticalPanel loginPanel;
	private final HTML infoBox;

	public LoginView() {
		GWT.log("Creating login view");

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

		loginPanel = new VerticalPanel();
		loginPanel.add(grid);
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
			sessionInformation.login(usernameTextBox.getText(), passwordTextBox.getText());
		}
	}

}
