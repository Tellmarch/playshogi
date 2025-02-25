package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.playshogi.website.gwt.shared.models.*;

@RemoteServiceRelativePath("problems")
public interface ProblemsService extends RemoteService {

    ProblemDetails getProblem(ProblemOptions options);

    ProblemDetails getProblem(String problemId);

    ProblemDetails getRandomProblem();

    ProblemDetails getRandomProblem(int numMoves);

    void saveUserProblemAttempt(String sessionId, String problemId, boolean success, int timeMs);

    void saveHighScore(String userName, int score);

    SurvivalHighScore[] getHighScores();

    void saveCollectionTime(String sessionId, String collectionId, int timeMs, boolean complete, int solved);

    ProblemStatisticsDetails[] getProblemStatisticsDetails(String sessionId);

    String saveProblemsCollection(String sessionId, String draftId, ProblemCollectionDetails details);

    void addDraftToProblemCollection(String sessionId, String draftId, String collectionId);

    ProblemCollectionDetails[] getAllProblemCollections(String sessionId);

    ProblemCollectionDetails[] getPublicProblemCollections(String sessionId);

    ProblemCollectionDetails[] getUserProblemCollections(String sessionId, String userName);

    ProblemCollectionDetailsAndProblems getProblemCollection(String sessionId, String collectionId,
                                                             boolean includeHiddenProblems);

    ProblemCollectionDetailsAndProblems getLearnFromMistakeProblemCollection(String sessionId, String gameCollectionId);

    void deleteProblemCollection(String sessionId, String problemSetId, boolean alsoDeleteKifus);

    void updateProblemCollectionDetails(String sessionId, ProblemCollectionDetails problemCollectionDetails);

    void updateProblemCollectionDetailsAdmin(String sessionId, ProblemCollectionDetails problemCollectionDetails);

    void createProblemCollection(String sessionId, ProblemCollectionDetails details);

    void saveProblemAndAddToCollection(String sessionId, String usf, String collectionId);

    void removeProblemFromCollection(String sessionId, String problemId, String collectionId);

    void addExistingKifuToProblemCollection(String sessionId, String kifuId, String collectionId);

    void convertGameCollection(String sessionId, String gameCollectionId);

    void createCollectionsByDifficulty(String sessionId);

    void swapProblemsInCollection(String sessionId, String collectionId, String firstProblemId, String secondProblemId);

    String createRace(String sessionId, String collectionId, RaceDetails.RaceType raceType);

    RaceDetails getRaceDetails(String sessionId, String raceId);

    RaceDetails waitForRaceUpdate(String sessionId, String raceId);

    void joinRace(String sessionId, String raceId);

    void withdrawFromRace(String sessionId, String raceId);

    void startRace(String sessionId, String raceId);

    void reportUserProgressInRace(String sessionId, String raceId, String problemId, RaceDetails.ProblemStatus status);

    void reportBadProblem(String sessionId, String kifuId, String problemId, String collectionId, String reason,
                          String comment);
}
