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

    void getAllGameCollections(String sessionId, AsyncCallback<GameCollectionDetails[]> callback);

    void getPublicGameCollections(String sessionId, AsyncCallback<GameCollectionDetails[]> callback);

    void getUserGameCollections(String sessionId, String userName, AsyncCallback<GameCollectionDetails[]> callback);

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

    void getLessonKifus(String sessionId, String userName, AsyncCallback<KifuDetails[]> callback);

    void getUserKifus(String sessionId, String userName, AsyncCallback<KifuDetails[]> callback);

    void deleteKifu(String sessionId, String kifuId, AsyncCallback<Void> callback);

    void addExistingKifuToCollection(String sessionId, String kifuId, String collectionId,
                                     AsyncCallback<Void> voidAsyncCallback);

    void getAllLessons(String sessionId, AsyncCallback<LessonDetails[]> callback);

    void getAllPublicLessons(String sessionId, AsyncCallback<LessonDetails[]> callback);

    void createLesson(String sessionId, LessonDetails lesson, AsyncCallback<Void> callback);

    void updateLesson(String sessionId, LessonDetails lesson, AsyncCallback<Void> callback);

    void updateKifuUsf(String sessionId, String kifuId, String kifuUsf, AsyncCallback<Void> callback);

    void getTournament(String sessionID, String tournamentID, AsyncCallback<TournamentDetails> callback);
}
