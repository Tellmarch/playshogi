package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class KifuSearchFilterDetails implements Serializable {

    private String partialPositionSearchSfen;
    private String playerName;
    private String gameResult;

    public KifuSearchFilterDetails() {
    }

    public String getPartialPositionSearchSfen() {
        return partialPositionSearchSfen;
    }

    public void setPartialPositionSearchSfen(final String partialPositionSearchSfen) {
        this.partialPositionSearchSfen = partialPositionSearchSfen;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }

    public String getGameResult() {
        return gameResult;
    }

    public void setGameResult(final String gameResult) {
        this.gameResult = gameResult;
    }

    @Override
    public String toString() {
        return "KifuSearchFilterDetails{" +
                "partialPositionSearchSfen='" + partialPositionSearchSfen + '\'' +
                ", playerName='" + playerName + '\'' +
                ", gameResult='" + gameResult + '\'' +
                '}';
    }
}
