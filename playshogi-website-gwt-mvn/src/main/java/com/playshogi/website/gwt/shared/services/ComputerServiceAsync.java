package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ComputerServiceAsync {

    void getComputerMove(String sessionId, String sfen, AsyncCallback<String> callback);

}
