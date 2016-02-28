package com.playshogi.website.gwt.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.PositionSharingService;

public class PositionSharingServiceImpl extends RemoteServiceServlet implements PositionSharingService {

	private static final long serialVersionUID = 1L;

	private final Map<String, ShogiPosition> positions = new ConcurrentHashMap<>();

	@Override
	public ShogiPosition getPosition(final String key) {
		return positions.get(key);
	}

	@Override
	public void sharePosition(final ShogiPosition position, final String key) {
		positions.put(key, position);
	}

}
