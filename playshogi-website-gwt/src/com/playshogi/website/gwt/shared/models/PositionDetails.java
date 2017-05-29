package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class PositionDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	private final int total;
	private final int sente_wins;
	private final int gote_wins;

	private final PositionMoveDetails[] positionMoveDetails;

	public PositionDetails(final int total, final int sente_wins, final int gote_wins, final PositionMoveDetails[] positionMoveDetails) {
		this.total = total;
		this.sente_wins = sente_wins;
		this.gote_wins = gote_wins;
		this.positionMoveDetails = positionMoveDetails;
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

	public PositionMoveDetails[] getPositionMoveDetails() {
		return positionMoveDetails;
	}

}
