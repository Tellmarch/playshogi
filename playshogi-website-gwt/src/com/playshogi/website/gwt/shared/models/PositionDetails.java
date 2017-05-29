package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class PositionDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	private int total;
	private int sente_wins;
	private int gote_wins;

	private PositionMoveDetails[] positionMoveDetails;

	public PositionDetails() {
	}

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

	public void setTotal(final int total) {
		this.total = total;
	}

	public void setSente_wins(final int sente_wins) {
		this.sente_wins = sente_wins;
	}

	public void setGote_wins(final int gote_wins) {
		this.gote_wins = gote_wins;
	}

	public void setPositionMoveDetails(final PositionMoveDetails[] positionMoveDetails) {
		this.positionMoveDetails = positionMoveDetails;
	}

	@Override
	public String toString() {
		return "PositionDetails [total=" + total + ", sente_wins=" + sente_wins + ", gote_wins=" + gote_wins + ", positionMoveDetails="
				+ Arrays.toString(positionMoveDetails) + "]";
	}

}
