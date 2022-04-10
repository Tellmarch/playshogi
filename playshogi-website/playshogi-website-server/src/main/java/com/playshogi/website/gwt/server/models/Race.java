package com.playshogi.website.gwt.server.models;

import com.playshogi.website.gwt.shared.models.RaceDetails;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Race {

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

    public static class UserProgress {
        private final Map<String, ProblemStatus> problemStatuses = new ConcurrentHashMap<>(); // problem ID
        private int score;
        private int combo;

        public Map<String, ProblemStatus> getProblemStatuses() {
            return problemStatuses;
        }

        public int getScore() {
            return score;
        }

        public void setScore(final int score) {
            this.score = score;
        }

        public int getCombo() {
            return combo;
        }

        public void setCombo(final int combo) {
            this.combo = combo;
        }
    }

    private final String id;

    private RaceType raceType;
    private RaceStatus status = RaceStatus.PRE_RACE;
    private final User owner;

    private final List<String> problemIds;
    private final List<User> participants = new CopyOnWriteArrayList<>();
    private final Map<User, UserProgress> userProgresses = new ConcurrentHashMap<>();

    private Date startTime;

    public Race(final String id, final RaceType raceType, final User owner, final List<String> problemIds) {
        this.id = id;
        this.raceType = raceType;
        this.owner = owner;
        this.problemIds = new ArrayList<>(problemIds);
    }

    public String getId() {
        return id;
    }

    public RaceType getRaceType() {
        return raceType;
    }

    public void setRaceType(final RaceType raceType) {
        this.raceType = raceType;
    }

    public RaceStatus getStatus() {
        return status;
    }

    public void setStatus(final RaceStatus status) {
        this.status = status;
    }

    public User getOwner() {
        return owner;
    }

    public List<String> getProblemIds() {
        return problemIds;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public Map<User, UserProgress> getUserProgresses() {
        return userProgresses;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }

    public RaceDetails toRaceDetails() {
        RaceDetails raceDetails = new RaceDetails();
        raceDetails.setId(id);
        raceDetails.setRaceType(RaceDetails.RaceType.valueOf(raceType.name()));
        raceDetails.setRaceStatus(RaceDetails.RaceStatus.valueOf(status.name()));
        raceDetails.setPlayers(participants.stream().map(User::getUserName).toArray(String[]::new));
        raceDetails.setOwner(owner.getUserName());
        raceDetails.setPlayerProgresses(getPlayerProgresses());
        if (startTime != null) {
            raceDetails.setElapsedTimeMs((int) (System.currentTimeMillis() - startTime.getTime()));
        }
        // TODO fill the rest
        return raceDetails;
    }

    private RaceDetails.ProblemStatus[][] getPlayerProgresses() {
        RaceDetails.ProblemStatus[][] problemStatuses = new RaceDetails.ProblemStatus[participants.size()][];
        int userIndex = 0;
        for (User participant : participants) {
            UserProgress userProgress = userProgresses.get(participant);
            problemStatuses[userIndex] = new RaceDetails.ProblemStatus[problemIds.size()];
            int problemIndex = 0;
            for (String problemId : problemIds) {
                ProblemStatus problemStatus = userProgress.getProblemStatuses().getOrDefault(problemId,
                        ProblemStatus.NOT_ATTEMPTED);
                problemStatuses[userIndex][problemIndex++] = RaceDetails.ProblemStatus.valueOf(problemStatus.name());
            }
            userIndex++;
        }

        return problemStatuses;
    }
}
