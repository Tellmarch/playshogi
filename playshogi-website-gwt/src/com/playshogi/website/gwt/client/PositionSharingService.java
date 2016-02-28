package com.playshogi.website.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.playshogi.library.shogi.models.position.ShogiPosition;

@RemoteServiceRelativePath("positionSharing")
public interface PositionSharingService extends RemoteService {

	ShogiPosition getPosition(String key);

	void sharePosition(ShogiPosition position, String key);

}
