package com.playshogi.library.database.models;

public class PersistentGameSetPos {

	private final int positionId;
	private final int gameSetId;
	private final int total;
	private final int senteWins;
	private final int goteWins;

	public PersistentGameSetPos(final int positionId, final int gameSetId, final int total, final int senteWins, final int goteWins) {
		this.positionId = positionId;
		this.gameSetId = gameSetId;
		this.total = total;
		this.senteWins = senteWins;
		this.goteWins = goteWins;
	}

	public int getPositionId() {
		return positionId;
	}

	public int getGameSetId() {
		return gameSetId;
	}

	public int getTotal() {
		return total;
	}

	public int getSenteWins() {
		return senteWins;
	}

	public int getGoteWins() {
		return goteWins;
	}

	@Override
	public String toString() {
		return "PersistentGameSetPos [positionId=" + positionId + ", gameSetId=" + gameSetId + ", total=" + total + ", senteWins=" + senteWins + ", goteWins="
				+ goteWins + "]";
	}

}
