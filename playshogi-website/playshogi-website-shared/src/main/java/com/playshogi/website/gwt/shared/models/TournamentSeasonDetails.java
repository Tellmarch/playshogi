package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class TournamentSeasonDetails implements Serializable {
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
