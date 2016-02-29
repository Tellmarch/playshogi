package com.playshogi.website.gwt.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.website.gwt.client.PositionSharingService;

public class PositionSharingServiceImpl extends RemoteServiceServlet implements PositionSharingService {

	private static final long serialVersionUID = 1L;

	private final Map<String, String> positions = new ConcurrentHashMap<>();

	@Override
	public String getPosition(final String key) {
		return positions.get(key);
	}

	@Override
	public void sharePosition(final String position, final String key) {
		positions.put(key, position);
	}

}
