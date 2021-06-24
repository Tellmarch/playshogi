package com.playshogi.website.gwt.shared.models;

import java.util.Arrays;

public class TournamentSeasonDetails {
    private String title;
    private TournamentCategoryDetails[] categories;

    public TournamentSeasonDetails() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public TournamentCategoryDetails[] getCategories() {
        return categories;
    }

    public void setCategories(final TournamentCategoryDetails[] categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "TournamentSeasonDetails{" +
                "title='" + title + '\'' +
                ", categories=" + Arrays.toString(categories) +
                '}';
    }
}
