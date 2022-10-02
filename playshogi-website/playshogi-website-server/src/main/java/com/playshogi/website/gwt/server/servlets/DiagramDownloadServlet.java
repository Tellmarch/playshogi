package com.playshogi.website.gwt.server.servlets;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.svg.SVGConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class DiagramDownloadServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DiagramDownloadServlet.class.getName());

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sfen = req.getParameter("sfen");
        LOGGER.info("Downloading diagram for position " + sfen);
        ShogiPosition position = SfenConverter.fromSFEN(sfen);

        String svg = SVGConverter.toSVG(position);

        resp.setContentType("text/plain");
        resp.setHeader("Content-disposition", "attachment; filename=test.svg");

        try (PrintWriter p = new PrintWriter(resp.getOutputStream())) {
            p.print(svg);
        }
    }
}
