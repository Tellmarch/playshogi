package com.playshogi.website.gwt.client.events;

import com.playshogi.library.shogi.models.moves.ShogiMove;

public class MovePlayedEvent {
	private final ShogiMove move;

	public MovePlayedEvent(final ShogiMove move) {
		this.move = move;
	}

	public ShogiMove getMove() {
		return move;
	}

}
