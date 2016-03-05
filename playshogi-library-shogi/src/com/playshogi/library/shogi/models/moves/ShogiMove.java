package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.models.Move;

public class ShogiMove implements Move {

	private final boolean senteMoving;

	public ShogiMove(final boolean senteMoving) {
		this.senteMoving = senteMoving;
	}

	public boolean isSenteMoving() {
		return senteMoving;
	}

}
