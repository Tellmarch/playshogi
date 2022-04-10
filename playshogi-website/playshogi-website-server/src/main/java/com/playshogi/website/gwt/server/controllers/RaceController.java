package com.playshogi.website.gwt.server.controllers;

import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.ProblemSetRepository;
import com.playshogi.library.database.models.PersistentProblemInCollection;
import com.playshogi.library.database.models.PersistentProblemSet;
import com.playshogi.website.gwt.server.models.Race;
import com.playshogi.website.gwt.server.models.User;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public enum RaceController {
    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(RaceController.class.getName());

    private final ProblemSetRepository problemSetRepository = new ProblemSetRepository(new DbConnection());

    private final Map<String, Race> races = new ConcurrentHashMap<>(); // Race ID

    public String createRace(final User user, final String collectionId, final Race.RaceType raceType) {

        if (collectionId == null) {
            throw new IllegalArgumentException("Invalid collectionId");
        }

        PersistentProblemSet problemSet = problemSetRepository.getProblemSetById(Integer.parseInt(collectionId));
        if (problemSet == null) {
            throw new IllegalArgumentException("Invalid collectionId");
        }
        List<PersistentProblemInCollection> problems =
                problemSetRepository.getProblemsFromProblemSet(problemSet.getId(), false);

        List<String> problemIds =
                problems.stream().map(p -> String.valueOf(p.getProblem().getId())).collect(Collectors.toList());

        String id = UUID.randomUUID().toString();

        Race race = new Race(id, raceType, user, problemIds);

        races.put(id, race);
        joinRace(user, id);

        return id;
    }

    public Race getRace(final String raceId) {
        Race race = races.get(raceId);
        if (race == null) {
            throw new IllegalStateException("Race does not exist.");
        }
        return race;
    }

    public void joinRace(final User user, final String raceId) {
        Race race = races.get(raceId);
        if (race == null) {
            throw new IllegalStateException("Race does not exist.");
        }
        if (race.getStatus() == Race.RaceStatus.FINISHED) {
            throw new IllegalStateException("Cannot join: the race is already over.");
        }
        if (race.getParticipants().contains(user)) {
            throw new IllegalStateException("User has already joined the race.");
        }

        race.getParticipants().add(user);
        race.getUserProgresses().put(user, new Race.UserProgress());
        fireRaceUpdate(raceId);
    }

    public void withdrawFromRace(final User user, final String raceId) {
        Race race = races.get(raceId);
        if (race == null) {
            throw new IllegalStateException("Race does not exist.");
        }
        if (race.getStatus() != Race.RaceStatus.PRE_RACE) {
            throw new IllegalStateException("Cannot withdraw: the race is already started.");
        }
        if (!race.getParticipants().contains(user)) {
            throw new IllegalStateException("User has not joined the race.");
        }

        race.getParticipants().remove(user);
        race.getUserProgresses().remove(user);
        fireRaceUpdate(raceId);
    }

    public void startRace(final User user, final String raceId) {
        Race race = races.get(raceId);
        if (race == null) {
            throw new IllegalStateException("Race does not exist.");
        }
        if (!user.equals(race.getOwner())) {
            throw new IllegalStateException("User is not allowed to start the race.");
        }
        if (!(race.getStatus() == Race.RaceStatus.PRE_RACE)) {
            throw new IllegalStateException("Race is not in a state where it can be started");
        }

        race.setStatus(Race.RaceStatus.IN_PROGRESS);
        race.setStartTime(new Date());
        fireRaceUpdate(raceId);
    }

    public void reportUserProgress(final User user, final String raceId, final String problemId,
                                   final Race.ProblemStatus problemStatus) {
        Race race = races.get(raceId);
        if (race == null) {
            throw new IllegalStateException("Race does not exist.");
        }
        if (race.getStatus() != Race.RaceStatus.IN_PROGRESS) {
            throw new IllegalStateException("Race is not in progress.");
        }
        Race.UserProgress userProgress = race.getUserProgresses().get(user);
        if (userProgress == null) {
            throw new IllegalStateException("User is not part of the race.");
        }

        userProgress.getProblemStatuses().put(problemId, problemStatus);
        fireRaceUpdate(raceId);
    }

    public Object getRaceMonitor(final String raceId) {
        Race race = races.get(raceId);
        if (race == null) {
            throw new IllegalStateException("Race does not exist.");
        }
        return race;
    }

    private void fireRaceUpdate(final String raceId) {
        Object raceMonitor = getRaceMonitor(raceId);
        synchronized (raceMonitor) {
            raceMonitor.notifyAll();
        }
    }

    public Race waitForRaceUpdate(final String raceId) {
        Object raceMonitor = getRaceMonitor(raceId);
        synchronized (raceMonitor) {
            try {
                raceMonitor.wait(10000);
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Interrupted", e);
            }
        }
        return races.get(raceId);
    }
}
