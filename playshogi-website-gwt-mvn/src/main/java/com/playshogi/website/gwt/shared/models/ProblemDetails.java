package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class ProblemDetails implements Serializable {

    private String id;
    private int kifuId;
    private Integer numMoves;
    private int elo;
    private String pbType;
    private String usf;
    private String[] tags;

    public ProblemDetails() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getKifuId() {
        return kifuId;
    }

    public void setKifuId(int kifuId) {
        this.kifuId = kifuId;
    }

    public Integer getNumMoves() {
        return numMoves;
    }

    public void setNumMoves(Integer numMoves) {
        this.numMoves = numMoves;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public String getPbType() {
        return pbType;
    }

    public void setPbType(String pbType) {
        this.pbType = pbType;
    }

    public String getUsf() {
        return usf;
    }

    public void setUsf(String usf) {
        this.usf = usf;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "ProblemDetails{" +
                "id=" + id +
                ", kifuId=" + kifuId +
                ", numMoves=" + numMoves +
                ", elo=" + elo +
                ", pbType='" + pbType + '\'' +
                ", usf='" + usf + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
