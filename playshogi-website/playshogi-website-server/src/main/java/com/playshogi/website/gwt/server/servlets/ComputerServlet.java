package com.playshogi.website.gwt.server.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.playshogi.library.shogi.engine.EngineConfiguration;
import com.playshogi.library.shogi.engine.QueuedComputerPlay;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComputerServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ComputerServlet.class.getName());
    private final QueuedComputerPlay queuedComputerPlay = new QueuedComputerPlay(EngineConfiguration.NORMAL_ENGINE);

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String sessionId = request.getParameter("sessionId");
        String sfen = request.getParameter("sfen");

        if (sessionId == null || sfen == null || sessionId.isEmpty() || sfen.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.write("Error: 'sessionId' and 'sfen' parameters are required.");
            out.flush();
            return;
        }

        PrintWriter out = response.getWriter();
        try {
            String computerMove = queuedComputerPlay.playMove(sfen);

            out.write(computerMove);

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing computer move request", e);
            out.write("Error retrieving computer move: " + e.getMessage());
        } finally {
            out.flush();
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (BufferedReader reader = request.getReader();
             PrintWriter out = response.getWriter()) {

            // Parse incoming JSON
            JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
            if (jsonRequest == null || !jsonRequest.has("sessionId") || !jsonRequest.has("sfen")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("error", "'sessionId' and 'sfen' fields are required.");
                out.write(gson.toJson(error));
                return;
            }

//            String sessionId = jsonRequest.get("sessionId").getAsString();
            String sfen = jsonRequest.get("sfen").getAsString();

            // Compute move
            String computerMove = queuedComputerPlay.playMove(sfen);

            // Build JSON response
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("move", computerMove);
            out.write(gson.toJson(jsonResponse));

        } catch (JsonSyntaxException e) {
            LOGGER.log(Level.WARNING, "Invalid JSON in request", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Invalid JSON format.");
            response.getWriter().write(gson.toJson(error));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing POST request", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Internal server error: " + e.getMessage());
            response.getWriter().write(gson.toJson(error));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
