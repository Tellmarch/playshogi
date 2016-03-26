package com.playshogi.website.gwt.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PositionSharingServiceAsync {

	void sharePosition(String position, String key, AsyncCallback<Void> callback);

	void getPosition(String key, AsyncCallback<String> callback);

	void getNextMove(String key, AsyncCallback<String> callback);

	void playMove(String key, String move, AsyncCallback<Void> callback);

}
