package com.playshogi.website.gwt.client.ui;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginView extends Composite {

	public LoginView() {
		HorizontalPanel loginPanel = new HorizontalPanel();
		loginPanel.add(new HTML("Username:"));
		loginPanel.add(new TextBox());

		HorizontalPanel passwordPanel = new HorizontalPanel();
		passwordPanel.add(new HTML("Password:"));
		passwordPanel.add(new PasswordTextBox());

		HorizontalPanel buttonPanel = new HorizontalPanel();

		buttonPanel.add(new Button("Login"));
		buttonPanel.add(new Button("Register"));

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(loginPanel);
		verticalPanel.add(passwordPanel);
		verticalPanel.add(buttonPanel);
		initWidget(verticalPanel);
	}

}
