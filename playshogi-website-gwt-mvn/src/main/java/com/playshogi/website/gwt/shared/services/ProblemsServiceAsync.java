package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import com.playshogi.website.gwt.shared.models.ProblemStatisticsDetails;
import com.playshogi.website.gwt.shared.models.SurvivalHighScore;

public interface ProblemsServiceAsync {

    void getProblem(String problemId, AsyncCallback<ProblemDetails> callback);

    void getRandomProblem(int numMoves, AsyncCallback<ProblemDetails> callback);

    void saveUserProblemAttempt(String sessionId, String problemId, boolean success, int timeMs,
                                AsyncCallback<Void> callback);

    void getProblemStatisticsDetails(String sessionId, AsyncCallback<ProblemStatisticsDetails[]> callback);

    void saveHighScore(String userName, int score, AsyncCallback<Void> callback);

    void getHighScores(AsyncCallback<SurvivalHighScore[]> callback);

}
