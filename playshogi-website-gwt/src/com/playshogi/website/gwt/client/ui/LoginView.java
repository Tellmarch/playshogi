package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.playshogi.website.gwt.client.services.LoginService;
import com.playshogi.website.gwt.client.services.LoginServiceAsync;

public class LoginView extends Composite implements ClickHandler, AsyncCallback<Boolean> {

	private final Button loginButton;

	private final LoginServiceAsync loginService = GWT.create(LoginService.class);

	private final TextBox usernameTextBox;
	private final PasswordTextBox passwordTextBox;

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
	public void onSuccess(final Boolean result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFailure(final Throwable caught) {
		// TODO Auto-generated method stub

	}

}
