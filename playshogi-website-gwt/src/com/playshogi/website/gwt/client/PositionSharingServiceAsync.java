package com.playshogi.website.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public interface PositionSharingServiceAsync {

	void sharePosition(ShogiPosition position, String key, AsyncCallback<Void> callback);

	void getPosition(String key, AsyncCallback<ShogiPosition> callback);

}
