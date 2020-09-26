package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.playshogi.website.gwt.shared.models.*;

@RemoteServiceRelativePath("kifus")
public interface KifuService extends RemoteService {

    String saveKifu(String sessionId, String kifuUsf);

    String getKifuUsf(String sessionId, String kifuId);

    KifuDetails[] getGameSetKifuDetails(String sessionId, String gameSetId);

    PositionDetails getPositionDetails(String sfen, String gameSetId);

    PositionEvaluationDetails analysePosition(String sessionId, String sfen);

    AnalysisRequestStatus requestKifuAnalysis(String sessionId, String kifuUsf);

    AnalysisRequestResult getKifuAnalysisResults(String sessionId, String kifuUsf);

    GameCollectionDetails[] getGameCollections(String sessionId);

    String saveGameCollection(String sessionId, String draftId);

    void saveGameCollectionDetails(String sessionId, GameCollectionDetails gameCollectionDetails);

}
