package com.playshogi.website.gwt.server;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.PositionSharingService;

public class PositionSharingServiceImpl extends RemoteServiceServlet implements PositionSharingService {

	private static final long serialVersionUID = 1L;

	private final Map<String, ShogiPosition> positions = new ConcurrentHashMap<>();
	private final Map<String, Set<FutureResult>> positionListeners = new ConcurrentHashMap<>();

	private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();

	@Override
	public String getPosition(final String key) {
		return SfenConverter.toSFEN(positions.get(key));
	}

	@Override
	public void sharePosition(final String position, final String key) {
		positions.put(key, SfenConverter.fromSFEN(position));
		positionListeners.put(key, new HashSet<>());
	}

	@Override
	public String getNextMove(final String key) {
		FutureResult futureResult = new FutureResult();
		positionListeners.get(key).add(futureResult);
		try {
			return (String) futureResult.get();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void playMove(final String key, final String move) {
		if (positions.containsKey(move)) {
			ShogiPosition position = positions.get(key);
			ShogiMove shogiMove = UsfMoveConverter.fromUsfString(move, position);
			shogiRulesEngine.playMoveInPosition(position, shogiMove);
			Set<FutureResult> set = positionListeners.get(key);
			for (FutureResult futureResult : set) {
				futureResult.set(move);
			}
			set.clear();
		}
	}
}
