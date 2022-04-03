package com.playshogi.website.gwt.server.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RaceController {

    private enum RaceType {
        TO_THE_END,
        TIME_LIMIT,
        COMBO
    }

    private enum RaceStatus {
        PRE_RACE,
        IN_PROGRESS,
        FINISHED
    }

    private static class UserProgress {

    }

    private static class RaceInformation {
        private String id;

        private RaceType raceType;
        private RaceStatus status;

        private List<String> problemIds;
        private List<String> participants;
        private Map<String, UserProgress> userProgresses;

        private Date startTime;

        public String getId() {
            return id;
        }

        public RaceType getRaceType() {
            return raceType;
        }

        public RaceStatus getStatus() {
            return status;
        }

        public List<String> getProblemIds() {
            return problemIds;
        }

        public List<String> getParticipants() {
            return participants;
        }

        public Map<String, UserProgress> getUserProgresses() {
            return userProgresses;
        }

        public Date getStartTime() {
            return startTime;
        }
    }

    private final Map<String, RaceInformation> raceDetails = new ConcurrentHashMap<>();

    public String createRace(final String username, final String collectionId, final RaceType raceType) {
        String id = UUID.randomUUID().toString();

        RaceInformation details = new RaceInformation();

        raceDetails.put(id, details);

        return id;
    }

    public void joinRace(final String username, final String raceId) {
        RaceInformation details = raceDetails.get(raceId);
        if (details.getStatus() == RaceStatus.FINISHED) {
            throw new IllegalStateException("Cannot join: the race is already over.");
        }
        if (details.getParticipants().contains(username)) {
            throw new IllegalStateException("User has already joined the race.");
        }
        details.getParticipants().add(username);
    }

    public void startRace(final String username, final String raceId) {

    }

}
