package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.playshogi.website.gwt.shared.models.*;

public interface KifuServiceAsync {

    void getKifuUsf(String sessionId, String kifuId, AsyncCallback<String> callback);

    void saveKifu(String sessionId, String kifuUsf, String name, KifuDetails.KifuType type,
                  AsyncCallback<String> callback);

    void saveGameAndAddToCollection(String sessionId, String kifuUsf, String collectionId,
                                    AsyncCallback<Void> callback);

    void getGameSetKifuDetails(String sessionId, String gameSetId,
                               AsyncCallback<GameCollectionDetailsAndGames> callback);

    void getPositionDetails(String sfen, String gameSetId, AsyncCallback<PositionDetails> callback);

    void analysePosition(String sessionId, String sfen, AsyncCallback<PositionEvaluationDetails> callback);

    void requestKifuAnalysis(String sessionId, String kifuUsf, AsyncCallback<AnalysisRequestStatus> callback);

    void getKifuAnalysisResults(String sessionId, String kifuUsf, AsyncCallback<AnalysisRequestResult> callback);

    void getGameCollections(String sessionId, AsyncCallback<GameCollectionDetailsList> callback);

    void saveGameCollection(String sessionId, String draftId, GameCollectionDetails gameCollectionDetails,
                            AsyncCallback<String> callback);

    void addDraftToGameCollection(String sessionId, String draftId, String collectionId,
                                  AsyncCallback<Void> callback);

    void saveDraftCollectionKifus(String sessionId, String draftId, AsyncCallback<Void> callback);

    void updateGameCollectionDetails(String sessionId, GameCollectionDetails gameCollectionDetails,
                                     AsyncCallback<Void> callback);

    void createGameCollection(String sessionId, GameCollectionDetails gameCollectionDetails,
                              AsyncCallback<Void> callback);

    void deleteGameCollection(String sessionId, String gameSetId, AsyncCallback<Void> callback);

    void removeGameFromCollection(String sessionId, String gameId, String gameSetId, AsyncCallback<Void> callback);

    void getUserKifus(String sessionId, String userName, AsyncCallback<KifuDetails[]> callback);

    void deleteKifu(String sessionId, String kifuId, AsyncCallback<Void> callback);

    void addExistingKifuToCollection(String sessionId, String kifuId, String collectionId,
                                     AsyncCallback<Void> voidAsyncCallback);

    void getAllLessons(String sessionId, AsyncCallback<LessonDetails[]> callback);
}
