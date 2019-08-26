package com.playshogi.website.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.database.AuthenticationResult;
import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.UserRepository;
import com.playshogi.website.gwt.shared.models.LoginResult;
import com.playshogi.website.gwt.shared.services.LoginService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

    private static final Logger LOGGER = Logger.getLogger(LoginServiceImpl.class.getName());

    private static final long serialVersionUID = 1L;

    private final UserRepository users = new UserRepository(new DbConnection());
    private final Map<String, LoginResult> activeSessions = new HashMap<>();

    @Override
    public LoginResult login(final String username, final String password) {
        LOGGER.log(Level.INFO, "Login for user " + username);
        AuthenticationResult authenticationResult = users.authenticateUser(username, password);
        LoginResult loginResult = new LoginResult();
        if (authenticationResult == AuthenticationResult.LOGIN_OK) {
            loginResult.setLoggedIn(true);
            loginResult.setUserName(username);
            String sessionId = UUID.randomUUID().toString();
            loginResult.setSessionId(sessionId);
            activeSessions.put(sessionId, loginResult);
        } else if (authenticationResult == AuthenticationResult.UNKNOWN) {
            loginResult.setErrorMessage("Invalid username or password");
        } else if (authenticationResult == AuthenticationResult.UNAVAILABLE) {
            loginResult.setErrorMessage("An error occurred, please try again later");
        }
        return loginResult;
    }

    @Override
    public LoginResult checkSession(final String sessionId) {
        LOGGER.log(Level.INFO, "checking sessionId");
        return activeSessions.get(sessionId);
    }

    @Override
    public LoginResult logout(final String sessionId) {
        LOGGER.log(Level.INFO, "Logging out sessionId");
        activeSessions.remove(sessionId);
        return new LoginResult();
    }

    @Override
    public LoginResult register(final String username, final String password) {
        // TODO Auto-generated method stub
        return null;
    }
}
