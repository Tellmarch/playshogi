package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class PositionMoveDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String move;
	private final int total;
	private final int sente_wins;
	private final int gote_wins;

	public PositionMoveDetails(final String move, final int total, final int sente_wins, final int gote_wins) {
		this.move = move;
		this.total = total;
		this.sente_wins = sente_wins;
		this.gote_wins = gote_wins;
	}

	public String getMove() {
		return move;
	}

	public int getTotal() {
		return total;
	}

	public int getSente_wins() {
		return sente_wins;
	}

	public int getGote_wins() {
		return gote_wins;
	}

}
