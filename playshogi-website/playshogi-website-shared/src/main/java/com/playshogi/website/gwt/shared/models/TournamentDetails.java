package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class TournamentDetails implements Serializable {
    private String title;
    private String description;
    private String organizer;
    private TournamentSeasonDetails[] seasons;

    public TournamentDetails() {
    }

    public TournamentDetails(final String title, final String description, final String organizer,
                             final TournamentSeasonDetails[] seasons) {
        this.title = title;
        this.description = description;
        this.organizer = organizer;
        this.seasons = seasons;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getOrganizer() {
        return organizer;
    }

    public TournamentSeasonDetails[] getSeasons() {
        return seasons;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setOrganizer(final String organizer) {
        this.organizer = organizer;
    }

    public void setSeasons(final TournamentSeasonDetails[] seasons) {
        this.seasons = seasons;
    }

    @Override
    public String toString() {
        return "TournamentDetails{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", organizer='" + organizer + '\'' +
                ", seasons=" + Arrays.toString(seasons) +
                '}';
    }


}
