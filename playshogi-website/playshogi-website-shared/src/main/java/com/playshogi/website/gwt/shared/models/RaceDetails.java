package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class RaceDetails implements Serializable {

    public enum RaceType {
        TO_THE_END,
        TIME_LIMIT,
        COMBO
    }

    public enum RaceStatus {
        PRE_RACE,
        IN_PROGRESS,
        FINISHED
    }

    public enum ProblemStatus {
        NOT_ATTEMPTED,
        ATTEMPTING,
        SOLVED,
        FAILED,
        SKIPPED
    }

    private String id;
    private String owner;
    private String[] players;
    private int[] playerScores;
    private int[] playerPositions;
    private ProblemStatus[][] playerProgresses;
    private int[] playerCombos;
    private int elapsedTimeMs;
    private int timeRemainingMs;
    private RaceType raceType;
    private RaceStatus raceStatus;

    public RaceDetails() {
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public String[] getPlayers() {
        return players;
    }

    public void setPlayers(final String[] players) {
        this.players = players;
    }

    public int[] getPlayerScores() {
        return playerScores;
    }

    public void setPlayerScores(final int[] playerScores) {
        this.playerScores = playerScores;
    }

    public int[] getPlayerPositions() {
        return playerPositions;
    }

    public void setPlayerPositions(final int[] playerPositions) {
        this.playerPositions = playerPositions;
    }

    public ProblemStatus[][] getPlayerProgresses() {
        return playerProgresses;
    }

    public void setPlayerProgresses(final ProblemStatus[][] playerProgresses) {
        this.playerProgresses = playerProgresses;
    }

    public int[] getPlayerCombos() {
        return playerCombos;
    }

    public void setPlayerCombos(final int[] playerCombos) {
        this.playerCombos = playerCombos;
    }

    public int getElapsedTimeMs() {
        return elapsedTimeMs;
    }

    public void setElapsedTimeMs(final int elapsedTimeMs) {
        this.elapsedTimeMs = elapsedTimeMs;
    }

    public int getTimeRemainingMs() {
        return timeRemainingMs;
    }

    public void setTimeRemainingMs(final int timeRemainingMs) {
        this.timeRemainingMs = timeRemainingMs;
    }

    public RaceType getRaceType() {
        return raceType;
    }

    public void setRaceType(final RaceType raceType) {
        this.raceType = raceType;
    }

    public RaceStatus getRaceStatus() {
        return raceStatus;
    }

    public void setRaceStatus(final RaceStatus raceStatus) {
        this.raceStatus = raceStatus;
    }

    @Override
    public String toString() {
        return "RaceDetails{" +
                "id='" + id + '\'' +
                ", owner='" + owner + '\'' +
                ", players=" + Arrays.toString(players) +
                ", playerScores=" + Arrays.toString(playerScores) +
                ", playerPositions=" + Arrays.toString(playerPositions) +
                ", playerProgresses=" + Arrays.deepToString(playerProgresses) +
                ", playerCombos=" + Arrays.toString(playerCombos) +
                ", elapsedTimeMs=" + elapsedTimeMs +
                ", timeRemainingMs=" + timeRemainingMs +
                ", raceType=" + raceType +
                ", raceStatus=" + raceStatus +
                '}';
    }
}
