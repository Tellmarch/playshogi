package com.playshogi.library.models.record;

public class GameRecord {
	private final GameInformation gameInformation;
	private final GameTree gameTree;
	private final GameResult gameResult;

	public GameRecord(final GameInformation gameInformation, final GameTree gameTree, final GameResult gameResult) {
		this.gameInformation = gameInformation;
		this.gameTree = gameTree;
		this.gameResult = gameResult;
	}

	public GameInformation getGameInformation() {
		return gameInformation;
	}

	public GameTree getGameTree() {
		return gameTree;
	}

	public GameResult getGameResult() {
		return gameResult;
	}

}
