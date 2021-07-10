package com.playshogi.library.shogi.models.record;

import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;

public class GameRecord {
    private GameInformation gameInformation;
    private GameTree gameTree;
    private GameResult gameResult;

    public GameRecord() {
        this(new GameInformation(), new GameTree(), null);
    }

    public GameRecord(final GameInformation gameInformation, final GameTree gameTree, final GameResult gameResult) {
        this.gameInformation = gameInformation;
        this.gameTree = gameTree;
        this.gameResult = gameResult;
    }

    public GameInformation getGameInformation() {
        return gameInformation;
    }

    public void setGameInformation(final GameInformation gameInformation) {
        this.gameInformation = gameInformation;
    }

    public GameTree getGameTree() {
        return gameTree;
    }

    public void setGameTree(final GameTree gameTree) {
        this.gameTree = gameTree;
    }

    public GameResult getGameResult() {
        return gameResult;
    }

    public void setGameResult(final GameResult gameResult) {
        this.gameResult = gameResult;
    }

    public ReadOnlyShogiPosition getInitialPosition() {
        return gameTree.getInitialPosition();
    }

    @Override
    public String toString() {
        return "GameRecord{" +
                "gameInformation=" + gameInformation +
                ", gameResult=" + gameResult +
                '}';
    }

    public boolean isEmpty() {
        return (gameInformation == null || gameInformation.equals(new GameInformation())) &&
                (gameResult == null || gameResult == GameResult.UNKNOWN) &&
                gameTree.getInitialPosition().isDefaultStartingPosition() &&
                gameTree.getMainVariationLength() == 0;
    }
}
