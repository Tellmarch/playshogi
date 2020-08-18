package com.playshogi.website.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.website.gwt.shared.Version;
import com.playshogi.website.gwt.shared.models.LoginResult;
import com.playshogi.website.gwt.shared.services.LoginService;

import java.util.logging.Logger;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

    private static final Logger LOGGER = Logger.getLogger(LoginServiceImpl.class.getName());

    private static final long serialVersionUID = 1L;

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
        return authenticator.register(username, password);
    }

    @Override
    public String getVersion() {
        return Version.VERSION;
    }
}
