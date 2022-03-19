package com.playshogi.website.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.website.gwt.shared.Version;
import com.playshogi.website.gwt.shared.models.LoginResult;
import com.playshogi.website.gwt.shared.services.LoginService;

import java.util.logging.Logger;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,25}$";

    private static final Logger LOGGER = Logger.getLogger(LoginServiceImpl.class.getName());

    private final Authenticator authenticator = Authenticator.INSTANCE;

    @Override
    public LoginResult login(final String username, final String password) {
        return authenticator.login(username, password);
    }

    @Override
    public LoginResult checkSession(final String sessionId) {
        return authenticator.checkSession(sessionId);
    }

    @Override
    public LoginResult logout(final String sessionId) {
        return authenticator.logout(sessionId);
    }

    @Override
    public LoginResult register(final String username, final String password) {
        if (!validateUserName(username)) {
            LOGGER.warning("Invalid username: " + username);
            return new LoginResult("Invalid username: " + username);
        }
        return authenticator.register(username, password);
    }

    public boolean validateUserName(final String username) {
        if (username == null) {
            return false;
        }

        return username.matches(USERNAME_REGEX);
    }

    @Override
    public String getVersion() {
        return Version.VERSION;
    }
}
