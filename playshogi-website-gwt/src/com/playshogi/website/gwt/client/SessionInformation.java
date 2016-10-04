package com.playshogi.website.gwt.client;

import com.google.gwt.user.client.Cookies;

public class SessionInformation {

	private boolean loggedIn = false;
	private String username = null;
	private String sessionId = null;

	public SessionInformation() {
		String sid = Cookies.getCookie("sid");
		if (sid != null) {
			// TODO : check cookie with server
			loggedIn = true;
			username = "Tellmarch";
			sessionId = sid;
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

}
