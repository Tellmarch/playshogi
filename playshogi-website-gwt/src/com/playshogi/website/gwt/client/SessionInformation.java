package com.playshogi.website.gwt.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.UserLoggedInEvent;
import com.playshogi.website.gwt.shared.models.LoginResult;
import com.playshogi.website.gwt.shared.services.LoginService;
import com.playshogi.website.gwt.shared.services.LoginServiceAsync;

public class SessionInformation implements AsyncCallback<LoginResult> {

	private final LoginServiceAsync loginService = GWT.create(LoginService.class);

	private boolean loggedIn = false;
	private String username = null;
	private String sessionId = null;

	private final EventBus eventBus;

	@Inject
	public SessionInformation(final EventBus eventBus) {
		this.eventBus = eventBus;
		String sid = Cookies.getCookie("sid");
		if (sid != null) {
			loginService.checkSession(sid, this);
		}
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public String getUsername() {
		return username;
	}

	public String getSessionId() {
		return sessionId;
	}

	@Override
	public String toString() {
		return "SessionInformation [loggedIn=" + loggedIn + ", username=" + username + "]";
	}

	@Override
	public void onFailure(final Throwable caught) {
		GWT.log("ERROR validating session", caught);
	}

	@Override
	public void onSuccess(final LoginResult result) {
		GWT.log("Got session validation result: " + result);
		if (result != null) {
			loggedIn = result.isLoggedIn();
			sessionId = result.getSessionId();
			username = result.getUserName();
			if (loggedIn) {
				eventBus.fireEvent(new UserLoggedInEvent());
			}
		}
	}

}
