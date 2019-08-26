package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.models.PositionDetails;

@RemoteServiceRelativePath("kifus")
public interface KifuService extends RemoteService {

    String saveKifu(String sessionId, String kifuUsf);

    String getKifuUsf(String sessionId, String kifuId);

    KifuDetails[] getAvailableKifuDetails(String sessionId);

    PositionDetails getPositionDetails(String sfen, int gameSetId);

}
