package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class GameDetails implements Serializable {

    private String id;
    private String kifuId;
    private String sente;
    private String gote;
    private String senteId;
    private String goteId;
    private String venue;
    private String date;

    public GameDetails() {
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getKifuId() {
        return kifuId;
    }

    public void setKifuId(final String kifuId) {
        this.kifuId = kifuId;
    }

    public String getSente() {
        return sente;
    }

    public void setSente(final String sente) {
        this.sente = sente;
    }

    public String getGote() {
        return gote;
    }

    public void setGote(final String gote) {
        this.gote = gote;
    }

    public String getSenteId() {
        return senteId;
    }

    public void setSenteId(final String senteId) {
        this.senteId = senteId;
    }

    public String getGoteId() {
        return goteId;
    }

    public void setGoteId(final String goteId) {
        this.goteId = goteId;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(final String venue) {
        this.venue = venue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "GameDetails{" +
                "id='" + id + '\'' +
                ", kifuId='" + kifuId + '\'' +
                ", sente='" + sente + '\'' +
                ", gote='" + gote + '\'' +
                ", senteId='" + senteId + '\'' +
                ", goteId='" + goteId + '\'' +
                ", venue='" + venue + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
