package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class TournamentCategoryDetails implements Serializable {
    private String title;
    private TournamentRoundDetails[] rounds;
    private String tournamentURL;

    public TournamentCategoryDetails() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public TournamentRoundDetails[] getRounds() {
        return rounds;
    }

    public void setRounds(final TournamentRoundDetails[] rounds) {
        this.rounds = rounds;
    }

    public String getTournamentURL() {
        return tournamentURL;
    }

    public void setTournamentURL(final String tournamentURL) {
        this.tournamentURL = tournamentURL;
    }

    @Override
    public String toString() {
        return "TournamentCategoryDetails{" +
                "title='" + title + '\'' +
                ", rounds=" + Arrays.toString(rounds) +
                ", tournamentURL='" + tournamentURL + '\'' +
                '}';
    }
}
