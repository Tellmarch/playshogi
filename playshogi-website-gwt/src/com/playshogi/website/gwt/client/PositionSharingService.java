package com.playshogi.website.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("positionSharing")
public interface PositionSharingService extends RemoteService {

	String getPosition(String key);

	void sharePosition(String position, String key);

}
