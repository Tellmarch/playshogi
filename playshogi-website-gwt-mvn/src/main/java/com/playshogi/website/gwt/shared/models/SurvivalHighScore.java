package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class SurvivalHighScore implements Serializable {

    private String name;
    private int score;

    public SurvivalHighScore() {
    }

    public SurvivalHighScore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "SurvivalHighScore{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
