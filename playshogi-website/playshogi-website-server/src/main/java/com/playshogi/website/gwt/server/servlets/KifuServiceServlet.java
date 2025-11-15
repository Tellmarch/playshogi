package com.playshogi.website.gwt.server.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.playshogi.website.gwt.server.services.KifuServiceImpl;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.models.KifuSearchFilterDetails;
import com.playshogi.website.gwt.shared.models.LessonDetails;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

public class KifuServiceServlet extends HttpServlet {

    private final KifuServiceImpl kifuService = new KifuServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json; charset=UTF-8");
        Reader reader = req.getReader();
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

        String action = json.get("action").getAsString();
        Object result;

        try {
            switch (action) {

                case "saveKifu":
                    result = kifuService.saveKifu(
                            json.get("sessionId").getAsString(),
                            json.get("kifuUsf").getAsString(),
                            json.get("name").getAsString(),
                            gson.fromJson(json.get("type"), KifuDetails.KifuType.class)
                    );
                    break;

                case "saveGameAndAddToCollection":
                    kifuService.saveGameAndAddToCollection(
                            json.get("sessionId").getAsString(),
                            json.get("kifuUsf").getAsString(),
                            json.get("collectionId").getAsString()
                    );
                    result = "OK";
                    break;

                case "getKifuUsf":
                    result = kifuService.getKifuUsf(
                            json.get("sessionId").getAsString(),
                            json.get("kifuId").getAsString()
                    );
                    break;

                case "getGameSetKifuDetails":
                    result = kifuService.getGameSetKifuDetails(
                            json.get("sessionId").getAsString(),
                            json.get("gameSetId").getAsString()
                    );
                    break;

                case "getGameSetStatistics":
                    result = kifuService.getGameSetStatistics(
                            json.get("sessionId").getAsString(),
                            json.get("gameSetId").getAsString()
                    );
                    break;

                case "getGameSetKifuDetailsWithFilter":
                    result = kifuService.getGameSetKifuDetailsWithFilter(
                            json.get("sessionId").getAsString(),
                            json.get("gameSetId").getAsString(),
                            gson.fromJson(json.get("filterDetails"), KifuSearchFilterDetails.class)
                    );
                    break;

                case "getPositionDetails":
                    result = kifuService.getPositionDetails(
                            json.get("sfen").getAsString(),
                            json.get("gameSetId").getAsString()
                    );
                    break;

                case "analysePosition":
                    result = kifuService.analysePosition(
                            json.get("sessionId").getAsString(),
                            json.get("sfen").getAsString()
                    );
                    break;

                case "requestKifuAnalysis":
                    result = kifuService.requestKifuAnalysis(
                            json.get("sessionId").getAsString(),
                            json.get("kifuUsf").getAsString()
                    );
                    break;

                case "getKifuAnalysisResults":
                    result = kifuService.getKifuAnalysisResults(
                            json.get("sessionId").getAsString(),
                            json.get("kifuUsf").getAsString()
                    );
                    break;

                case "getAllGameCollections":
                    result = kifuService.getAllGameCollections(json.get("sessionId").getAsString());
                    break;

                case "getPublicGameCollections":
                    result = kifuService.getPublicGameCollections(json.get("sessionId").getAsString());
                    break;

                case "getUserGameCollections":
                    result = kifuService.getUserGameCollections(
                            json.get("sessionId").getAsString(),
                            json.get("userName").getAsString()
                    );
                    break;

                case "saveGameCollection":
                    result = kifuService.saveGameCollection(
                            json.get("sessionId").getAsString(),
                            json.get("draftId").getAsString(),
                            gson.fromJson(json.get("gameCollectionDetails"), GameCollectionDetails.class)
                    );
                    break;

                case "addDraftToGameCollection":
                    kifuService.addDraftToGameCollection(
                            json.get("sessionId").getAsString(),
                            json.get("draftId").getAsString(),
                            json.get("collectionId").getAsString()
                    );
                    result = "OK";
                    break;

                case "saveDraftCollectionKifus":
                    kifuService.saveDraftCollectionKifus(
                            json.get("sessionId").getAsString(),
                            json.get("draftId").getAsString()
                    );
                    result = "OK";
                    break;

                case "updateGameCollectionDetails":
                    kifuService.updateGameCollectionDetails(
                            json.get("sessionId").getAsString(),
                            gson.fromJson(json.get("gameCollectionDetails"), GameCollectionDetails.class)
                    );
                    result = "OK";
                    break;

                case "updateGameCollectionDetailsAdmin":
                    kifuService.updateGameCollectionDetailsAdmin(
                            json.get("sessionId").getAsString(),
                            gson.fromJson(json.get("gameCollectionDetails"), GameCollectionDetails.class)
                    );
                    result = "OK";
                    break;

                case "createGameCollection":
                    kifuService.createGameCollection(
                            json.get("sessionId").getAsString(),
                            gson.fromJson(json.get("gameCollectionDetails"), GameCollectionDetails.class)
                    );
                    result = "OK";
                    break;

                case "deleteGameCollection":
                    kifuService.deleteGameCollection(
                            json.get("sessionId").getAsString(),
                            json.get("gameSetId").getAsString()
                    );
                    result = "OK";
                    break;

                case "removeGameFromCollection":
                    kifuService.removeGameFromCollection(
                            json.get("sessionId").getAsString(),
                            json.get("gameId").getAsString(),
                            json.get("gameSetId").getAsString()
                    );
                    result = "OK";
                    break;

                case "getLessonKifus":
                    result = kifuService.getLessonKifus(
                            json.get("sessionId").getAsString(),
                            json.get("userName").getAsString()
                    );
                    break;

                case "getUserKifus":
                    result = kifuService.getUserKifus(
                            json.get("sessionId").getAsString(),
                            json.get("userName").getAsString()
                    );
                    break;

                case "deleteKifu":
                    kifuService.deleteKifu(
                            json.get("sessionId").getAsString(),
                            json.get("kifuId").getAsString()
                    );
                    result = "OK";
                    break;

                case "addExistingKifuToCollection":
                    kifuService.addExistingKifuToCollection(
                            json.get("sessionId").getAsString(),
                            json.get("kifuId").getAsString(),
                            json.get("collectionId").getAsString()
                    );
                    result = "OK";
                    break;

                case "getAllLessons":
                    result = kifuService.getAllLessons(json.get("sessionId").getAsString());
                    break;

                case "getAllPublicLessons":
                    result = kifuService.getAllPublicLessons(json.get("sessionId").getAsString());
                    break;

                case "createLesson":
                    kifuService.createLesson(
                            json.get("sessionId").getAsString(),
                            gson.fromJson(json.get("lesson"), LessonDetails.class)
                    );
                    result = "OK";
                    break;

                case "updateLesson":
                    kifuService.updateLesson(
                            json.get("sessionId").getAsString(),
                            gson.fromJson(json.get("lesson"), LessonDetails.class)
                    );
                    result = "OK";
                    break;

                case "updateKifuUsf":
                    kifuService.updateKifuUsf(
                            json.get("sessionId").getAsString(),
                            json.get("kifuId").getAsString(),
                            json.get("kifuUsf").getAsString()
                    );
                    result = "OK";
                    break;

                case "getTournament":
                    result = kifuService.getTournament(
                            json.get("sessionId").getAsString(),
                            json.get("tournamentID").getAsString()
                    );
                    break;

                default:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    gson.toJson("Unknown action: " + action, resp.getWriter());
                    return;
            }

            gson.toJson(result, resp.getWriter());

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            gson.toJson(error, resp.getWriter());
        }
    }
}
