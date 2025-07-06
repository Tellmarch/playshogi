package com.playshogi.website.gwt.server.servlets;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.svg.SVGConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.engine.EngineConfiguration;
import com.playshogi.library.shogi.engine.QueuedComputerPlay;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class ComputerServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ComputerServlet.class.getName());
    private final QueuedComputerPlay queuedComputerPlay = new QueuedComputerPlay(EngineConfiguration.NORMAL_ENGINE);


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // --- 1. Handle CORS (Cross-Origin Resource Sharing) ---
        // This is crucial if your React app is on a different domain/port.
        // Replace "http://localhost:3000" with the actual origin of your React app.
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173"); // Or "*" for development (less secure)
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400"); // Cache preflight requests for 24 hours

        // Handle preflight OPTIONS requests if needed (e.g., for POST requests with custom headers)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // --- 2. Set Content Type ---
        response.setContentType("text/plain"); // Or "application/json" if you return JSON
        response.setCharacterEncoding("UTF-8");

        // --- 3. Get Request Parameters ---
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
            // --- 4. Call your existing GWT RPC logic ---
            // This is where you call the method from your MyShogiRpcServiceImpl
            String computerMove = queuedComputerPlay.playMove(sfen);

            // --- 5. Write Response ---
            // For simplicity, returning plain text. For complex data, consider JSON.
            // If you return JSON: out.write("{\"move\": \"" + computerMove + "\"}");
            out.write(computerMove);

        } catch (Exception e) {
            System.err.println("Error processing computer move request: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("Error retrieving computer move: " + e.getMessage());
        } finally {
            out.flush();
            out.close();
        }
    }
}
