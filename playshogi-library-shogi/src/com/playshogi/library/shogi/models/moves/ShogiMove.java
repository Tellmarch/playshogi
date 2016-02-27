package com.playshogi.library.shogi.models.moves;

public class ShogiMove {

	private final boolean senteMoving;

	public ShogiMove(final boolean senteMoving) {
		this.senteMoving = senteMoving;
	}

	public boolean isSenteMoving() {
		return senteMoving;
	}

}
