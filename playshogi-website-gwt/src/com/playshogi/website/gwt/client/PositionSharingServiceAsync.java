package com.playshogi.website.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PositionSharingServiceAsync {

	void sharePosition(String position, String key, AsyncCallback<Void> callback);

	void getPosition(String key, AsyncCallback<String> callback);

}
