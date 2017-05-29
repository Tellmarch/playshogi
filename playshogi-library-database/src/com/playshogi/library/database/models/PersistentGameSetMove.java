package com.playshogi.library.database.models;

public class PersistentGameSetMove {

	private final String moveUsf;
	private final int moveOccurrences;
	private final int positionId;
	private final int newPositionId;
	private final int gameSetId;
	private final int positionOccurences;
	private final int senteWins;
	private final int goteWins;

	public PersistentGameSetMove(final String moveUsf, final int moveOccurrences, final int positionId, final int newPositionId, final int gameSetId,
			final int positionOccurences, final int senteWins, final int goteWins) {
		this.moveUsf = moveUsf;
		this.moveOccurrences = moveOccurrences;
		this.positionId = positionId;
		this.newPositionId = newPositionId;
		this.gameSetId = gameSetId;
		this.positionOccurences = positionOccurences;
		this.senteWins = senteWins;
		this.goteWins = goteWins;
	}

	public String getMoveUsf() {
		return moveUsf;
	}

	public int getMoveOccurrences() {
		return moveOccurrences;
	}

	public int getPositionId() {
		return positionId;
	}

	public int getNewPositionId() {
		return newPositionId;
	}

	public int getGameSetId() {
		return gameSetId;
	}

	public int getPositionOccurences() {
		return positionOccurences;
	}

	public int getSenteWins() {
		return senteWins;
	}

	public int getGoteWins() {
		return goteWins;
	}

	@Override
	public String toString() {
		return "PersistentGameSetMove [moveUsf=" + moveUsf + ", moveOccurrences=" + moveOccurrences + ", positionId=" + positionId + ", newPositionId="
				+ newPositionId + ", gameSetId=" + gameSetId + ", positionOccurences=" + positionOccurences + ", senteWins=" + senteWins + ", goteWins="
				+ goteWins + "]";
	}

}
