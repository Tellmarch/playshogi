package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.playshogi.website.gwt.shared.models.LoginResult;

public interface LoginServiceAsync {

	void login(String username, String password, AsyncCallback<LoginResult> callback);

	void checkSession(String sessionId, AsyncCallback<LoginResult> callback);

	void logout(String sessionId, AsyncCallback<LoginResult> callback);

	void register(String username, String password, AsyncCallback<LoginResult> callback);

}
