package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.ui.LoginView;

public class LoginActivity extends MyAbstractActivity {

	private final LoginView loginView;

	public LoginActivity(final LoginView loginView) {
		this.loginView = loginView;
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		GWT.log("Starting login activity");
		loginView.activate(eventBus);
		containerWidget.setWidget(loginView.asWidget());
	}

}
