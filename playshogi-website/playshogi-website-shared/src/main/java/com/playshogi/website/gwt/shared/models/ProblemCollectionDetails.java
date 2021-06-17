package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class ProblemCollectionDetails implements Serializable {
    private String id;
    private String name;
    private String description;
    private String visibility;
    private int numProblems;

    private int difficulty;
    private String[] leaderboardNames;
    private String[] leaderboardScores;
    private String[] tags;

    private String userHighScore;
    private int userSolved;

    public ProblemCollectionDetails() {
    }

    public ProblemCollectionDetails(final String name, final String description, final String visibility,
                                    final int difficulty, final String[] tags) {
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.difficulty = difficulty;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(final String visibility) {
        this.visibility = visibility;
    }

    public int getNumProblems() {
        return numProblems;
    }

    public void setNumProblems(final int numProblems) {
        this.numProblems = numProblems;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(final int difficulty) {
        this.difficulty = difficulty;
    }

    public String[] getLeaderboardNames() {
        return leaderboardNames;
    }

    public void setLeaderboardNames(final String[] leaderboardNames) {
        this.leaderboardNames = leaderboardNames;
    }

    public String[] getLeaderboardScores() {
        return leaderboardScores;
    }

    public void setLeaderboardScores(final String[] leaderboardScores) {
        this.leaderboardScores = leaderboardScores;
    }

    public String getUserHighScore() {
        return userHighScore;
    }

    public void setUserHighScore(final String userHighScore) {
        this.userHighScore = userHighScore;
    }

    public int getUserSolved() {
        return userSolved;
    }

    public void setUserSolved(final int userSolved) {
        this.userSolved = userSolved;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(final String[] tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "ProblemCollectionDetails{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", visibility='" + visibility + '\'' +
                ", numProblems=" + numProblems +
                ", difficulty=" + difficulty +
                ", leaderboardNames=" + Arrays.toString(leaderboardNames) +
                ", leaderboardScores=" + Arrays.toString(leaderboardScores) +
                ", tags=" + Arrays.toString(tags) +
                ", userHighScore='" + userHighScore + '\'' +
                ", userSolved=" + userSolved +
                '}';
    }
}
