package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import com.playshogi.website.gwt.shared.models.ProblemStatisticsDetails;

@RemoteServiceRelativePath("problems")
public interface ProblemsService extends RemoteService {

    String getProblemUsf(String problemId);

    ProblemDetails getProblem(String problemId);

    ProblemDetails getRandomProblem();

    ProblemDetails getRandomProblem(int numMoves);

    void saveUserProblemAttempt(String sessionId, String problemId, boolean success, int timeMs);

    ProblemStatisticsDetails[] getProblemStatisticsDetails(String sessionId);

}
