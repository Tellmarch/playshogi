package com.playshogi.website.gwt.server.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.playshogi.website.gwt.server.services.ProblemsServiceImpl;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import com.playshogi.website.gwt.shared.models.ProblemOptions;
import com.playshogi.website.gwt.shared.models.RaceDetails;
import com.playshogi.website.gwt.shared.services.ProblemsService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProblemsServiceServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ProblemsServiceServlet.class.getName());
    private final ProblemsService problemsService = new ProblemsServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        try (Reader reader = req.getReader()) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            String action = json.get("action").getAsString();
            Object result;

            String sessionId = Utils.getAsStringOrNull(json, "sessionId");

            switch (action) {
                // --- Problem Fetching ---
                case "getProblem":
                    if (json.has("options")) {
                        result = problemsService.getProblem(gson.fromJson(json.get("options"), ProblemOptions.class));
                    } else {
                        result = problemsService.getProblem(json.get("problemId").getAsString());
                    }
                    break;

                case "getRandomProblem":
                    if (json.has("numMoves")) {
                        result = problemsService.getRandomProblem(json.get("numMoves").getAsInt());
                    } else {
                        result = problemsService.getRandomProblem();
                    }
                    break;

                // --- Stats & High Scores ---
                case "saveUserProblemAttempt":
                    problemsService.saveUserProblemAttempt(
                            sessionId,
                            json.get("problemId").getAsString(),
                            json.get("success").getAsBoolean(),
                            json.get("timeMs").getAsInt()
                    );
                    result = "OK";
                    break;

                case "saveHighScore":
                    problemsService.saveHighScore(
                            json.get("userName").getAsString(),
                            json.get("score").getAsInt()
                    );
                    result = "OK";
                    break;

                case "getHighScores":
                    result = problemsService.getHighScores();
                    break;

                case "saveCollectionTime":
                    problemsService.saveCollectionTime(
                            sessionId,
                            json.get("collectionId").getAsString(),
                            json.get("timeMs").getAsInt(),
                            json.get("complete").getAsBoolean(),
                            json.get("solved").getAsInt()
                    );
                    result = "OK";
                    break;

                case "getProblemStatisticsDetails":
                    result = problemsService.getProblemStatisticsDetails(sessionId);
                    break;

                // --- Collection Management ---
                case "saveProblemsCollection":
                    result = problemsService.saveProblemsCollection(
                            sessionId,
                            json.get("draftId").getAsString(),
                            gson.fromJson(json.get("details"), ProblemCollectionDetails.class)
                    );
                    break;

                case "addDraftToProblemCollection":
                    problemsService.addDraftToProblemCollection(
                            sessionId,
                            json.get("draftId").getAsString(),
                            json.get("collectionId").getAsString()
                    );
                    result = "OK";
                    break;

                case "getAllProblemCollections":
                    result = problemsService.getAllProblemCollections(sessionId);
                    break;

                case "getPublicProblemCollections":
                    result = problemsService.getPublicProblemCollections(sessionId);
                    break;

                case "getUserProblemCollections":
                    result = problemsService.getUserProblemCollections(
                            sessionId,
                            json.get("userName").getAsString()
                    );
                    break;

                case "getProblemCollection":
                    result = problemsService.getProblemCollection(
                            sessionId,
                            json.get("collectionId").getAsString(),
                            json.get("includeHiddenProblems").getAsBoolean()
                    );
                    break;

                case "getLearnFromMistakeProblemCollection":
                    result = problemsService.getLearnFromMistakeProblemCollection(
                            sessionId,
                            json.get("gameCollectionId").getAsString()
                    );
                    break;

                case "deleteProblemCollection":
                    problemsService.deleteProblemCollection(
                            sessionId,
                            json.get("problemSetId").getAsString(),
                            json.get("alsoDeleteKifus").getAsBoolean()
                    );
                    result = "OK";
                    break;

                case "updateProblemCollectionDetails":
                    problemsService.updateProblemCollectionDetails(
                            sessionId,
                            gson.fromJson(json.get("problemCollectionDetails"), ProblemCollectionDetails.class)
                    );
                    result = "OK";
                    break;

                case "updateProblemCollectionDetailsAdmin":
                    problemsService.updateProblemCollectionDetailsAdmin(
                            sessionId,
                            gson.fromJson(json.get("problemCollectionDetails"), ProblemCollectionDetails.class)
                    );
                    result = "OK";
                    break;

                case "createProblemCollection":
                    problemsService.createProblemCollection(
                            sessionId,
                            gson.fromJson(json.get("details"), ProblemCollectionDetails.class)
                    );
                    result = "OK";
                    break;

                case "saveProblemAndAddToCollection":
                    problemsService.saveProblemAndAddToCollection(
                            sessionId,
                            json.get("usf").getAsString(),
                            json.get("collectionId").getAsString()
                    );
                    result = "OK";
                    break;

                case "removeProblemFromCollection":
                    problemsService.removeProblemFromCollection(
                            sessionId,
                            json.get("problemId").getAsString(),
                            json.get("collectionId").getAsString()
                    );
                    result = "OK";
                    break;

                case "addExistingKifuToProblemCollection":
                    problemsService.addExistingKifuToProblemCollection(
                            sessionId,
                            json.get("kifuId").getAsString(),
                            json.get("collectionId").getAsString()
                    );
                    result = "OK";
                    break;

                case "swapProblemsInCollection":
                    problemsService.swapProblemsInCollection(
                            sessionId,
                            json.get("collectionId").getAsString(),
                            json.get("firstProblemId").getAsString(),
                            json.get("secondProblemId").getAsString()
                    );
                    result = "OK";
                    break;

                // --- Logic / Admin Tasks ---
                case "convertGameCollection":
                    problemsService.convertGameCollection(
                            sessionId,
                            json.get("gameCollectionId").getAsString()
                    );
                    result = "OK";
                    break;

                case "createCollectionsByDifficulty":
                    problemsService.createCollectionsByDifficulty(sessionId);
                    result = "OK";
                    break;

                // --- Race System ---
                case "createRace":
                    result = problemsService.createRace(
                            sessionId,
                            json.get("collectionId").getAsString(),
                            RaceDetails.RaceType.valueOf(json.get("raceType").getAsString())
                    );
                    break;

                case "getRaceDetails":
                    result = problemsService.getRaceDetails(
                            sessionId,
                            json.get("raceId").getAsString()
                    );
                    break;

                case "waitForRaceUpdate":
                    result = problemsService.waitForRaceUpdate(
                            sessionId,
                            json.get("raceId").getAsString()
                    );
                    break;

                case "joinRace":
                    problemsService.joinRace(sessionId, json.get("raceId").getAsString());
                    result = "OK";
                    break;

                case "withdrawFromRace":
                    problemsService.withdrawFromRace(sessionId,
                            json.get("raceId").getAsString());
                    result = "OK";
                    break;

                case "startRace":
                    problemsService.startRace(sessionId, json.get("raceId").getAsString());
                    result = "OK";
                    break;

                case "reportUserProgressInRace":
                    problemsService.reportUserProgressInRace(
                            sessionId,
                            json.get("raceId").getAsString(),
                            json.get("problemId").getAsString(),
                            RaceDetails.ProblemStatus.valueOf(json.get("status").getAsString())
                    );
                    result = "OK";
                    break;

                case "reportBadProblem":
                    problemsService.reportBadProblem(
                            sessionId,
                            json.get("kifuId").getAsString(),
                            json.get("problemId").getAsString(),
                            json.get("collectionId").getAsString(),
                            json.get("reason").getAsString(),
                            json.get("comment").getAsString()
                    );
                    result = "OK";
                    break;

                default:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    gson.toJson("Unknown action: " + action, resp.getWriter());
                    return;
            }

            // Write successful result (or empty if void)
            gson.toJson(result, resp.getWriter());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing the method", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            gson.toJson(error, resp.getWriter());
        }
    }
}