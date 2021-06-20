package com.playshogi.library.shogi.models.record;

import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;

public class GameRecord {
    private final GameInformation gameInformation;
    private final GameTree gameTree;
    private final GameResult gameResult;

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

    public GameTree getGameTree() {
        return gameTree;
    }

    public GameResult getGameResult() {
        return gameResult;
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
}
