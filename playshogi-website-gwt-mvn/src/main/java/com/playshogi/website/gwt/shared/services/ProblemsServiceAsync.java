package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.playshogi.website.gwt.shared.models.ProblemDetails;

public interface ProblemsServiceAsync {

    void getProblemUsf(String problemId, AsyncCallback<String> callback);

    void getProblem(String problemId, AsyncCallback<ProblemDetails> callback);

    void getRandomProblem(AsyncCallback<ProblemDetails> callback);

}
