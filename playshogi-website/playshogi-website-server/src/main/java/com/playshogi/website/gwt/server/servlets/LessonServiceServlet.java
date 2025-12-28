package com.playshogi.website.gwt.server.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.playshogi.library.database.models.CampaignLessonNode;
import com.playshogi.library.database.models.LessonChapterDto;
import com.playshogi.website.gwt.server.services.LessonServiceImpl;
import com.playshogi.website.gwt.server.services.UserServiceImpl;
import com.playshogi.website.gwt.shared.models.LessonDetails;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.playshogi.website.gwt.server.servlets.Utils.getAsIntegerOrNull;
import static com.playshogi.website.gwt.server.servlets.Utils.getAsStringOrNull;

public class LessonServiceServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LessonServiceServlet.class.getName());

    private final LessonServiceImpl lessonService = new LessonServiceImpl();
    private final UserServiceImpl userService = new UserServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json; charset=UTF-8");
        Reader reader = req.getReader();
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

        String action = json.get("action").getAsString();
        String sessionId = getAsStringOrNull(json, "sessionId");

        LOGGER.log(Level.INFO, "LessonServiceServlet call: " + action);
        Object result;

        try {
            switch (action) {

                // -------------------------
                // Campaign graph
                // -------------------------
                case "getCampaign":
                    result = lessonService.getCampaign(
                            sessionId,
                            json.get("campaignId").getAsString()
                    );
                    break;

                // -------------------------
                // Lesson CRUD
                // -------------------------

                case "getLesson":
                    result = lessonService.getLesson(sessionId, json.get("lessonId").getAsString());
                    break;

                case "getAllLessons":
                    result = lessonService.getAllLessons(sessionId);
                    break;

                case "saveLesson":
                    result = lessonService.createLesson(
                            sessionId,
                            gson.fromJson(json.get("lesson"), LessonDetails.class));
                    break;

                case "updateLesson":
                    lessonService.updateLesson(
                            sessionId,
                            gson.fromJson(json.get("lesson"), LessonDetails.class));
                    result = "OK";
                    break;

                // -------------------------
                // Modify campaign graph
                // -------------------------
                case "addLessonToCampaign":
                    lessonService.addLessonToCampaign(
                            sessionId,
                            json.get("campaignId").getAsString(),
                            json.get("lessonId").getAsString(),
                            json.get("x").getAsInt(),
                            json.get("y").getAsInt()
                    );
                    result = "OK";
                    break;

                case "deleteCampaignLesson":
                    lessonService.deleteCampaignLesson(
                            sessionId,
                            json.get("campaignId").getAsString(),
                            json.get("lessonId").getAsString()
                    );
                    result = "OK";
                    break;

                case "updateCampaignNode":
                    lessonService.updateCampaignNode(
                            sessionId,
                            json.get("campaignId").getAsString(),
                            gson.fromJson(json.get("node"), CampaignLessonNode.class)
                    );
                    result = "OK";
                    break;

                case "setPrerequisites":
                    lessonService.setPrerequisites(
                            sessionId,
                            json.get("campaignId").getAsString(),
                            json.get("lessonId").getAsString(),
                            Arrays.asList(gson.fromJson(json.get("prerequisites"), String[].class))
                    );
                    result = "OK";
                    break;

                case "addChapter":
                    // Get the LessonChapterDTO from the 'chapter' field in the incoming JSON
                    LessonChapterDto chapterToAdd = gson.fromJson(json.get("chapter"),
                            LessonChapterDto.class);
                    lessonService.addChapter(sessionId, chapterToAdd);
                    result = "OK";
                    break;

                case "modifyChapter":
                    // Get the LessonChapterDTO from the 'chapter' field
                    LessonChapterDto chapterToModify = gson.fromJson(json.get("chapter"),
                            LessonChapterDto.class);
                    lessonService.modifyChapter(sessionId, chapterToModify);
                    result = "OK";
                    break;

                case "deleteChapter":
                    // Get the chapter ID as a string from the 'chapterId' field
                    String chapterIdToDelete = json.get("chapterId").getAsString();
                    lessonService.deleteChapter(sessionId, chapterIdToDelete);
                    result = "OK";
                    break;

                case "getChaptersForLesson":
                    // Get the lesson ID as a string from the 'lessonId' field
                    String lessonIdToFetch = json.get("lessonId").getAsString();
                    // The service method returns a List<LessonChapter> object
                    result = lessonService.getChaptersForLesson(sessionId, lessonIdToFetch);
                    break;

                case "swapChapterOrder":
                    // Get the two chapter IDs to swap
                    String chapterId1 = json.get("chapterId1").getAsString();
                    String chapterId2 = json.get("chapterId2").getAsString();
                    lessonService.swapChapterOrder(sessionId, chapterId1, chapterId2);
                    result = "OK";
                    break;

                case "saveLessonProgress":
                    String lessonId = json.get("lessonId").getAsString();
                    int timeMs = json.get("timeMs").getAsInt();
                    boolean complete = json.get("complete").getAsBoolean();
                    int percentage = json.get("percentage").getAsInt();
                    Integer rating = getAsIntegerOrNull(json, "rating");
                    userService.saveLessonProgress(sessionId, lessonId, timeMs, complete, percentage, rating);
                    result = "OK";
                    break;

                default:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    gson.toJson("Unknown action: " + action, resp.getWriter());
                    return;
            }

            gson.toJson(result, resp.getWriter());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error handling lesson servlet call", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            gson.toJson(error, resp.getWriter());
        }
    }
}