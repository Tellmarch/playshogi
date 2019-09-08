package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ProblemsServiceAsync {

    void getProblemUsf(String problemId, AsyncCallback<String> callback);

    void getRandomProblemUsf(AsyncCallback<String> callback);

}
