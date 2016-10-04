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
import com.playshogi.website.gwt.shared.services.PositionSharingService;

public class PositionSharingServiceImpl extends RemoteServiceServlet implements PositionSharingService {

	private static final long serialVersionUID = 1L;

	private final Map<String, ShogiPosition> positions = new ConcurrentHashMap<>();
	private final Map<String, Set<FutureResult>> positionListeners = new ConcurrentHashMap<>();

	private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();

	@Override
	public String getPosition(final String key) {
		System.out.println("someone requested position " + key);
		return SfenConverter.toSFEN(positions.get(key));
	}

	@Override
	public void sharePosition(final String position, final String key) {
		System.out.println("someone share position " + position + " as " + key);
		positions.put(key, SfenConverter.fromSFEN(position));
	}

	@Override
	public String getNextMove(final String key) {
		System.out.println("someone is waiting for the next move in " + key);
		FutureResult futureResult = new FutureResult();

		getListeners(key).add(futureResult);
		try {
			return (String) futureResult.get();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Set<FutureResult> getListeners(final String key) {
		if (!positionListeners.containsKey(key)) {
			positionListeners.put(key, new HashSet<>());
		}
		return positionListeners.get(key);
	}

	@Override
	public void playMove(final String key, final String move) {
		System.out.println("someone played move " + move + " in " + key);
		if (positions.containsKey(key)) {
			ShogiPosition position = positions.get(key);
			ShogiMove shogiMove = UsfMoveConverter.fromUsfString(move, position);
			shogiRulesEngine.playMoveInPosition(position, shogiMove);
			for (FutureResult futureResult : getListeners(key)) {
				System.out.println("setting a future in " + key);
				futureResult.set(move);
			}
			getListeners(key).clear();
		}
	}
}
