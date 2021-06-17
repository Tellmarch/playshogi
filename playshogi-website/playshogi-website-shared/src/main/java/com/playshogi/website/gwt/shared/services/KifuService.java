package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.playshogi.website.gwt.shared.models.*;

@RemoteServiceRelativePath("kifus")
public interface KifuService extends RemoteService {

    String saveKifu(String sessionId, String kifuUsf, String name, KifuDetails.KifuType type);

    void saveGameAndAddToCollection(String sessionId, String kifuUsf, String collectionId);

    String getKifuUsf(String sessionId, String kifuId);

    GameCollectionDetailsAndGames getGameSetKifuDetails(String sessionId, String gameSetId);

    PositionDetails getPositionDetails(String sfen, String gameSetId);

    PositionEvaluationDetails analysePosition(String sessionId, String sfen);

    AnalysisRequestStatus requestKifuAnalysis(String sessionId, String kifuUsf);

    AnalysisRequestResult getKifuAnalysisResults(String sessionId, String kifuUsf);

    GameCollectionDetailsList getGameCollections(String sessionId);

    String saveGameCollection(String sessionId, String draftId, GameCollectionDetails gameCollectionDetails);

    void updateGameCollectionDetails(String sessionId, GameCollectionDetails gameCollectionDetails);

    void createGameCollection(String sessionId, GameCollectionDetails gameCollectionDetails);

    void deleteGameCollection(String sessionId, String gameSetId);

    void removeGameFromCollection(String sessionId, String gameId, String gameSetId);

    KifuDetails[] getUserKifus(String sessionId, String userName);

    void deleteKifu(String sessionId, String kifuId);

    void addExistingKifuToCollection(String sessionId, String kifuId, String collectionId);

    LessonDetails[] getAllLessons(String sessionId);

}
