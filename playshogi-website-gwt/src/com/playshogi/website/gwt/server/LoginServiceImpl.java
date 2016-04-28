package com.playshogi.website.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.database.Users;
import com.playshogi.website.gwt.client.services.LoginService;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

	private static final long serialVersionUID = 1L;

	private Users users;

	@Override
	public String login(final String username, final String password) {
		// TODO Auto-generated method stub
		return null;
	}

}
