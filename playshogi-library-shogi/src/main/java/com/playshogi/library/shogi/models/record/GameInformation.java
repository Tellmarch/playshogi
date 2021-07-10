package com.playshogi.library.shogi.models.record;

import java.util.Objects;

public class GameInformation {

    private String black; // Sente or Shitate
    private String white; // Gote or Uwate
    private String date;
    private String location;
    private String event;
    private String opening;

    public GameInformation() {
    }

    public String getBlack() {
        return black;
    }

    public void setBlack(final String black) {
        this.black = black;
    }

    public String getWhite() {
        return white;
    }

    public void setWhite(final String white) {
        this.white = white;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getOpening() {
        return opening;
    }

    public void setOpening(final String opening) {
        this.opening = opening;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(final String event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "GameInformation{" +
                "black='" + black + '\'' +
                ", white='" + white + '\'' +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                ", event='" + event + '\'' +
                ", opening='" + opening + '\'' +
                '}';
    }

    public String getSummaryString() {
        return black + " vs " + white + ", " + date;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameInformation that = (GameInformation) o;
        return Objects.equals(black, that.black) && Objects.equals(white, that.white) && Objects.equals(date,
                that.date) && Objects.equals(location, that.location) && Objects.equals(event, that.event)
                && Objects.equals(opening, that.opening);
    }

    @Override
    public int hashCode() {
        return Objects.hash(black, white, date, location, event, opening);
    }
}
