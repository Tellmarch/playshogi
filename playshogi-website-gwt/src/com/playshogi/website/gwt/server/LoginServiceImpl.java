package com.playshogi.website.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.Users;
import com.playshogi.website.gwt.shared.services.LoginService;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

	private static final long serialVersionUID = 1L;

	private final Users users = new Users(new DbConnection());

	@Override
	public String login(final String username, final String password) {
		return users.authenticateUser(username, password).name();
	}

}
