package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.playshogi.website.gwt.shared.models.*;

public interface KifuServiceAsync {

    void getKifuUsf(String sessionId, String kifuId, AsyncCallback<String> callback);

    void saveKifu(String sessionId, String kifuUsf, AsyncCallback<String> callback);

    void getGameSetKifuDetails(String sessionId, String gameSetId, AsyncCallback<KifuDetails[]> callback);

    void getPositionDetails(String sfen, String gameSetId, AsyncCallback<PositionDetails> callback);

    void analysePosition(String sessionId, String sfen, AsyncCallback<PositionEvaluationDetails> callback);

    void requestKifuAnalysis(String sessionId, String kifuUsf, AsyncCallback<AnalysisRequestStatus> callback);

    void getKifuAnalysisResults(String sessionId, String kifuUsf, AsyncCallback<AnalysisRequestResult> callback);

    void getGameCollections(String sessionId, AsyncCallback<GameCollectionDetails[]> callback);

    void saveGameCollection(String sessionId, String draftId, AsyncCallback<String> callback);

    void saveGameCollectionDetails(String sessionId, GameCollectionDetails gameCollectionDetails,
                                   AsyncCallback<Void> callback);

}
