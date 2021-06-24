package com.playshogi.website.gwt.shared.models;

import java.util.Arrays;

public class TournamentRoundDetails {
    private String title;
    private GameCollectionDetails gameDetails;
    private GameDetails[] games;
    private AnalysisRequestResult[] analysis;
    private String[] kifuUSF;

    public TournamentRoundDetails() {
    }

    public String getTitle() {
        return title;
    }

    public GameCollectionDetails getGameDetails() {
        return gameDetails;
    }

    public GameDetails[] getGames() {
        return games;
    }

    public AnalysisRequestResult[] getAnalysis() {
        return analysis;
    }

    public String[] getKifuUSF() {
        return kifuUSF;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setGameDetails(final GameCollectionDetails gameDetails) {
        this.gameDetails = gameDetails;
    }

    public void setGames(final GameDetails[] games) {
        this.games = games;
    }

    public void setAnalysis(final AnalysisRequestResult[] analysis) {
        this.analysis = analysis;
    }

    public void setKifuUSF(final String[] kifuUSF) {
        this.kifuUSF = kifuUSF;
    }

    @Override
    public String toString() {
        return "TournamentRoundDetails{" +
                "title='" + title + '\'' +
                ", gameDetails=" + gameDetails +
                ", games=" + Arrays.toString(games) +
                ", analysis=" + Arrays.toString(analysis) +
                ", kifuUSF=" + Arrays.toString(kifuUSF) +
                '}';
    }
}
