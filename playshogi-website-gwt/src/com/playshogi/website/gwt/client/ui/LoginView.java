package com.playshogi.website.gwt.client.ui;

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

public class LoginView extends Composite implements ClickHandler {

	public LoginView() {

		Grid grid = new Grid(2, 2);
		grid.setWidget(0, 0, new HTML("Username:"));
		grid.setWidget(0, 1, new TextBox());
		grid.setWidget(1, 0, new HTML("Password:"));
		grid.setWidget(1, 1, new PasswordTextBox());

		HorizontalPanel buttonPanel = new HorizontalPanel();

		Button loginButton = new Button("Login");
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
		// TODO Auto-generated method stub

	}

}
