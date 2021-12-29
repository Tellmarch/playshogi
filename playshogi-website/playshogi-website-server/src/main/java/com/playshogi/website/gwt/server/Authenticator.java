package com.playshogi.website.gwt.server;

import com.playshogi.library.database.AuthenticationResult;
import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.UserRepository;
import com.playshogi.website.gwt.shared.models.LoginResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.playshogi.library.database.AuthenticationResult.Status.*;

public enum Authenticator {
    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(Authenticator.class.getName());

    private final UserRepository users = new UserRepository(new DbConnection());
    private final Map<String, LoginResult> activeSessions = new HashMap<>();

    public LoginResult login(final String username, final String password) {
        LOGGER.log(Level.INFO, "Login for user " + username);
        AuthenticationResult authenticationResult = users.authenticateUser(username, password);
        return login(authenticationResult.getUsername(), authenticationResult);
    }

    private LoginResult login(final String username, final AuthenticationResult authenticationResult) {
        LoginResult loginResult = new LoginResult();
        AuthenticationResult.Status status = authenticationResult.getStatus();
        if (status == LOGIN_OK) {
            loginResult.setLoggedIn(true);
            loginResult.setUserName(username);
            loginResult.setUserId(authenticationResult.getUserId());
            String sessionId = UUID.randomUUID().toString();
            loginResult.setSessionId(sessionId);
            loginResult.setAdmin(authenticationResult.isAdmin());
            activeSessions.put(sessionId, loginResult);
        } else if (status == INVALID) {
            loginResult.setErrorMessage("Invalid username or password");
        } else if (status == UNAVAILABLE) {
            loginResult.setErrorMessage("An error occurred, please try again later");
        }
        return loginResult;
    }

    public LoginResult register(final String username, final String password) {
        LOGGER.log(Level.INFO, "Register user " + username);
        AuthenticationResult authenticationResult = users.registerUser(username, password);
        return login(username, authenticationResult);
    }

    public LoginResult checkSession(final String sessionId) {
        LOGGER.log(Level.INFO, "checking sessionId");
        return activeSessions.get(sessionId);
    }

    public LoginResult logout(final String sessionId) {
        LOGGER.log(Level.INFO, "Logging out sessionId");
        activeSessions.remove(sessionId);
        return new LoginResult();
    }

    public LoginResult validateAdminSession(final String sessionId) {
        LoginResult loginResult = checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn() || !loginResult.isAdmin()) {
            LOGGER.log(Level.INFO, "Admin operation rejected");
            throw new IllegalStateException("Restricted to admins");
        }

        return loginResult;
    }
}
