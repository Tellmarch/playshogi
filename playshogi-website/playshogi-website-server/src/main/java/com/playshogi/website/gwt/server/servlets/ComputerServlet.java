package com.playshogi.website.gwt.server.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.playshogi.website.gwt.server.services.ComputerServiceImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.playshogi.website.gwt.server.servlets.Utils.getAsStringOrNull;

public class ComputerServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ComputerServlet.class.getName());

    private final ComputerServiceImpl computerService = new ComputerServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json; charset=UTF-8");
        Reader reader = req.getReader();
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

        String action = json.get("action").getAsString();
        String sessionId = getAsStringOrNull(json, "sessionId");

        LOGGER.log(Level.INFO, "ComputerServiceServlet call: " + action);
        Object result;

        try {
            switch (action) {

                case "getComputerMove":
                    result = computerService.getComputerMove(
                            sessionId,
                            json.get("sfen").getAsString()
                    );
                    break;

                case "getBeginnerComputerMove":
                    result = computerService.getBeginnerComputerMove(
                            sessionId,
                            json.get("sfen").getAsString()
                    );
                    break;

                default:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    gson.toJson("Unknown action: " + action, resp.getWriter());
                    return;
            }

            gson.toJson(result, resp.getWriter());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error handling computer service servlet call", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            gson.toJson(error, resp.getWriter());
        }
    }
}