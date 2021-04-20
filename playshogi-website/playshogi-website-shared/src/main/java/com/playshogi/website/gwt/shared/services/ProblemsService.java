package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import com.playshogi.website.gwt.shared.models.ProblemOptions;
import com.playshogi.website.gwt.shared.models.ProblemStatisticsDetails;
import com.playshogi.website.gwt.shared.models.SurvivalHighScore;

@RemoteServiceRelativePath("problems")
public interface ProblemsService extends RemoteService {

    ProblemDetails getProblem(ProblemOptions options);

    ProblemDetails getProblem(String problemId);

    ProblemDetails getRandomProblem();

    ProblemDetails getRandomProblem(int numMoves);

    void saveUserProblemAttempt(String sessionId, String problemId, boolean success, int timeMs);

    void saveHighScore(String userName, int score);

    SurvivalHighScore[] getHighScores();

    ProblemStatisticsDetails[] getProblemStatisticsDetails(String sessionId);

    String saveProblemsCollection(String sessionId, String draftId);

}
