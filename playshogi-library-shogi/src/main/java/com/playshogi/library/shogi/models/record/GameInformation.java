package com.playshogi.library.shogi.models.record;

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
}
