package com.playshogi.website.gwt.server.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.playshogi.website.gwt.server.services.LessonServiceImpl;
import com.playshogi.website.gwt.shared.models.LessonDetails;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

public class LessonServiceServlet extends HttpServlet {

    private final LessonServiceImpl lessonService = new LessonServiceImpl();
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

                // -------------------------
                // Campaign graph
                // -------------------------
                case "getCampaign":
                    result = lessonService.getCampaign(
                            json.get("sessionId").getAsString(),
                            json.get("campaignId").getAsInt()
                    );
                    break;

                // -------------------------
                // Lesson CRUD
                // -------------------------

                case "getAllLessons":
                    result = lessonService.getAllLessons(json.get("sessionId").getAsString());
                    break;

                case "saveLesson":
                    result = lessonService.createLesson(
                            json.get("sessionId").getAsString(),
                            gson.fromJson(json.get("lesson"), LessonDetails.class));
                    break;

                case "updateLesson":
                    lessonService.updateLesson(
                            json.get("sessionId").getAsString(),
                            gson.fromJson(json.get("lesson"), LessonDetails.class));
                    result = "OK";
                    break;

                // -------------------------
                // Modify campaign graph
                // -------------------------
                case "addLessonToCampaign":
                    result = lessonService.addLessonToCampaign(
                            json.get("sessionId").getAsString(),
                            json.get("campaignId").getAsInt(),
                            json.get("lessonId").getAsInt(),
                            json.get("x").getAsInt(),
                            json.get("y").getAsInt()
                    );
                    break;

                case "deleteCampaignLesson":
                    lessonService.deleteCampaignLesson(
                            json.get("sessionId").getAsString(),
                            json.get("campaignId").getAsInt(),
                            json.get("lessonId").getAsInt()
                    );
                    result = "OK";
                    break;

                case "setPrerequisites":
                    lessonService.setPrerequisites(
                            json.get("sessionId").getAsString(),
                            json.get("campaignId").getAsInt(),
                            json.get("lessonId").getAsInt(),
                            Arrays.asList(gson.fromJson(json.get("prerequisites"), Integer[].class))
                    );
                    result = "OK";
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