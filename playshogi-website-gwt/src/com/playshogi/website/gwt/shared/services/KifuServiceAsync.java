package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.playshogi.website.gwt.shared.models.KifuDetails;

public interface KifuServiceAsync {

	void getKifuUsf(String sessionId, String kifuId, AsyncCallback<String> callback);

	void saveKifu(String sessionId, String kifuUsf, AsyncCallback<String> callback);

	void getAvailableKifuDetails(String sessionId, AsyncCallback<KifuDetails[]> callback);

}
