package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class GameCollectionStatisticsDetails implements Serializable {
    private GameCollectionDetails details;

    // ratings
    private String[] ratingType;
    private Date[] ratingDates;
    private Integer[][] ratingValues;

    public GameCollectionStatisticsDetails() {
    }

    public GameCollectionStatisticsDetails(final GameCollectionDetails details, final String[] ratingType,
                                           final Date[] ratingDates, final Integer[][] ratingValues) {
        this.details = details;
        this.ratingType = ratingType;
        this.ratingDates = ratingDates;
        this.ratingValues = ratingValues;
    }

    public GameCollectionDetails getDetails() {
        return details;
    }

    public void setDetails(final GameCollectionDetails details) {
        this.details = details;
    }

    public String[] getRatingType() {
        return ratingType;
    }

    public void setRatingType(final String[] ratingType) {
        this.ratingType = ratingType;
    }

    public Date[] getRatingDates() {
        return ratingDates;
    }

    public void setRatingDates(final Date[] ratingDates) {
        this.ratingDates = ratingDates;
    }

    public Integer[][] getRatingValues() {
        return ratingValues;
    }

    public void setRatingValues(final Integer[][] ratingValues) {
        this.ratingValues = ratingValues;
    }

    @Override
    public String toString() {
        return "GameCollectionStatisticsDetails{" +
                "details=" + details +
                ", ratingType=" + Arrays.toString(ratingType) +
                ", ratingDates=" + Arrays.toString(ratingDates) +
                ", ratingValues=" + Arrays.toString(ratingValues) +
                '}';
    }
}
