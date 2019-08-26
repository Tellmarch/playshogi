package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.playshogi.website.gwt.shared.models.LoginResult;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {

    LoginResult login(String username, String password);

    LoginResult checkSession(String sessionId);

    LoginResult logout(String sessionId);

    LoginResult register(String username, String password);

}
