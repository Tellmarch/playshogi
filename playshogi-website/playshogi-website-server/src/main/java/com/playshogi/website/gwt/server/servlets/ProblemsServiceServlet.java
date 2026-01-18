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

public class ProblemsServiceServlet extends HttpServlet {

    private final ProblemsService problemsService = new ProblemsServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        try (Reader reader = req.getReader()) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            String action = json.get("action").getAsString();
            Object result;

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
                            json.get("sessionId").getAsString(),
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
                            json.get("sessionId").getAsString(),
                            json.get("collectionId").getAsString(),
                            json.get("timeMs").getAsInt(),
                            json.get("complete").getAsBoolean(),
                            json.get("solved").getAsInt()
                    );
                    result = "OK";
                    break;

                case "getProblemStatisticsDetails":
                    result = problemsService.getProblemStatisticsDetails(json.get("sessionId").getAsString());
                    break;

                // --- Collection Management ---
                case "saveProblemsCollection":
                    result = problemsService.saveProblemsCollection(
                            json.get("sessionId").getAsString(),
                            json.get("draftId").getAsString(),
                            gson.fromJson(json.get("details"), ProblemCollectionDetails.class)
                    );
                    break;

                case "addDraftToProblemCollection":
                    problemsService.addDraftToProblemCollection(
                            json.get("sessionId").getAsString(),
                            json.get("draftId").getAsString(),
                            json.get("collectionId").getAsString()
                    );
                    result = "OK";
                    break;

                case "getAllProblemCollections":
                    result = problemsService.getAllProblemCollections(json.get("sessionId").getAsString());
                    break;

                case "getPublicProblemCollections":
                    result = problemsService.getPublicProblemCollections(json.get("sessionId").getAsString());
                    break;

                case "getUserProblemCollections":
                    result = problemsService.getUserProblemCollections(
                            json.get("sessionId").getAsString(),
                            json.get("userName").getAsString()
                    );
                    break;

                case "getProblemCollection":
                    result = problemsService.getProblemCollection(
                            json.get("sessionId").getAsString(),
                            json.get("collectionId").getAsString(),
                            json.get("includeHiddenProblems").getAsBoolean()
                    );
                    break;

                case "getLearnFromMistakeProblemCollection":
                    result = problemsService.getLearnFromMistakeProblemCollection(
                            json.get("sessionId").getAsString(),
                            json.get("gameCollectionId").getAsString()
                    );
                    break;

                case "deleteProblemCollection":
                    problemsService.deleteProblemCollection(
                            json.get("sessionId").getAsString(),
                            json.get("problemSetId").getAsString(),
                            json.get("alsoDeleteKifus").getAsBoolean()
                    );
                    result = "OK";
                    break;

                case "updateProblemCollectionDetails":
                    problemsService.updateProblemCollectionDetails(
                            json.get("sessionId").getAsString(),
                            gson.fromJson(json.get("problemCollectionDetails"), ProblemCollectionDetails.class)
                    );
                    result = "OK";
                    break;

                case "updateProblemCollectionDetailsAdmin":
                    problemsService.updateProblemCollectionDetailsAdmin(
                            json.get("sessionId").getAsString(),
                            gson.fromJson(json.get("problemCollectionDetails"), ProblemCollectionDetails.class)
                    );
                    result = "OK";
                    break;

                case "createProblemCollection":
                    problemsService.createProblemCollection(
                            json.get("sessionId").getAsString(),
                            gson.fromJson(json.get("details"), ProblemCollectionDetails.class)
                    );
                    result = "OK";
                    break;

                case "saveProblemAndAddToCollection":
                    problemsService.saveProblemAndAddToCollection(
                            json.get("sessionId").getAsString(),
                            json.get("usf").getAsString(),
                            json.get("collectionId").getAsString()
                    );
                    result = "OK";
                    break;

                case "removeProblemFromCollection":
                    problemsService.removeProblemFromCollection(
                            json.get("sessionId").getAsString(),
                            json.get("problemId").getAsString(),
                            json.get("collectionId").getAsString()
                    );
                    result = "OK";
                    break;

                case "addExistingKifuToProblemCollection":
                    problemsService.addExistingKifuToProblemCollection(
                            json.get("sessionId").getAsString(),
                            json.get("kifuId").getAsString(),
                            json.get("collectionId").getAsString()
                    );
                    result = "OK";
                    break;

                case "swapProblemsInCollection":
                    problemsService.swapProblemsInCollection(
                            json.get("sessionId").getAsString(),
                            json.get("collectionId").getAsString(),
                            json.get("firstProblemId").getAsString(),
                            json.get("secondProblemId").getAsString()
                    );
                    result = "OK";
                    break;

                // --- Logic / Admin Tasks ---
                case "convertGameCollection":
                    problemsService.convertGameCollection(
                            json.get("sessionId").getAsString(),
                            json.get("gameCollectionId").getAsString()
                    );
                    result = "OK";
                    break;

                case "createCollectionsByDifficulty":
                    problemsService.createCollectionsByDifficulty(json.get("sessionId").getAsString());
                    result = "OK";
                    break;

                // --- Race System ---
                case "createRace":
                    result = problemsService.createRace(
                            json.get("sessionId").getAsString(),
                            json.get("collectionId").getAsString(),
                            RaceDetails.RaceType.valueOf(json.get("raceType").getAsString())
                    );
                    break;

                case "getRaceDetails":
                    result = problemsService.getRaceDetails(
                            json.get("sessionId").getAsString(),
                            json.get("raceId").getAsString()
                    );
                    break;

                case "waitForRaceUpdate":
                    result = problemsService.waitForRaceUpdate(
                            json.get("sessionId").getAsString(),
                            json.get("raceId").getAsString()
                    );
                    break;

                case "joinRace":
                    problemsService.joinRace(json.get("sessionId").getAsString(), json.get("raceId").getAsString());
                    result = "OK";
                    break;

                case "withdrawFromRace":
                    problemsService.withdrawFromRace(json.get("sessionId").getAsString(),
                            json.get("raceId").getAsString());
                    result = "OK";
                    break;

                case "startRace":
                    problemsService.startRace(json.get("sessionId").getAsString(), json.get("raceId").getAsString());
                    result = "OK";
                    break;

                case "reportUserProgressInRace":
                    problemsService.reportUserProgressInRace(
                            json.get("sessionId").getAsString(),
                            json.get("raceId").getAsString(),
                            json.get("problemId").getAsString(),
                            RaceDetails.ProblemStatus.valueOf(json.get("status").getAsString())
                    );
                    result = "OK";
                    break;

                case "reportBadProblem":
                    problemsService.reportBadProblem(
                            json.get("sessionId").getAsString(),
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
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            gson.toJson(error, resp.getWriter());
        }
    }
}