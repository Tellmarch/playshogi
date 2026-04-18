package com.playshogi.website.gwt.server.servlets;

import com.playshogi.library.database.*;
import com.playshogi.library.database.models.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KifuDownloadServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(KifuDownloadServlet.class.getName());

    private final KifuRepository kifuRepository;
    private final GameSetRepository gameSetRepository;
    private final GameRepository gameRepository;
    private final ProblemSetRepository problemSetRepository;
    private final LessonRepository lessonRepository;

    public KifuDownloadServlet() {
        DbConnection dbConnection = new DbConnection();
        kifuRepository = new KifuRepository(dbConnection);
        gameSetRepository = new GameSetRepository(dbConnection);
        gameRepository = new GameRepository(dbConnection);
        problemSetRepository = new ProblemSetRepository(dbConnection);
        lessonRepository = new LessonRepository(dbConnection);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String format = req.getParameter("format");
        if (format == null) {
            format = "USF";
        }

        if (!"USF".equalsIgnoreCase(format)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Unsupported format: " + format + ". Only USF is currently supported.");
            return;
        }

        String kifuIdParam = req.getParameter("kifuId");
        String collectionIdParam = req.getParameter("collectionId");
        String problemCollectionIdParam = req.getParameter("problemCollectionId");
        String lessonIdParam = req.getParameter("lessonId");

        try {
            String filename;
            List<String> usfStrings = new ArrayList<>();

            if (kifuIdParam != null) {
                int kifuId = Integer.parseInt(kifuIdParam);
                LOGGER.info("Downloading kifu: " + kifuId);
                PersistentKifu kifu = kifuRepository.getKifuById(kifuId);
                if (kifu == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Kifu not found: " + kifuId);
                    return;
                }
                filename = sanitizeFilename(kifu.getName());
                if (kifu.getKifuUsf() != null) {
                    usfStrings.add(kifu.getKifuUsf());
                }

            } else if (collectionIdParam != null) {
                int collectionId = Integer.parseInt(collectionIdParam);
                LOGGER.info("Downloading game collection: " + collectionId);
                PersistentGameSet gameSet = gameSetRepository.getGameSetById(collectionId);
                if (gameSet == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Game collection not found: " + collectionId);
                    return;
                }
                filename = sanitizeFilename(gameSet.getName());
                List<PersistentGame> games = gameRepository.getGamesFromGameSet(collectionId, true);
                if (games != null) {
                    for (PersistentGame game : games) {
                        PersistentKifu kifu = kifuRepository.getKifuById(game.getKifuId(), true);
                        if (kifu != null && kifu.getKifuUsf() != null) {
                            usfStrings.add(kifu.getKifuUsf());
                        }
                    }
                }

            } else if (problemCollectionIdParam != null) {
                int problemCollectionId = Integer.parseInt(problemCollectionIdParam);
                LOGGER.info("Downloading problem collection: " + problemCollectionId);
                PersistentProblemSet problemSet = problemSetRepository.getProblemSetById(problemCollectionId);
                if (problemSet == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                            "Problem collection not found: " + problemCollectionId);
                    return;
                }
                filename = sanitizeFilename(problemSet.getName());
                List<PersistentProblemInCollection> problems =
                        problemSetRepository.getProblemsFromProblemSet(problemCollectionId, false);
                if (problems != null) {
                    for (PersistentProblemInCollection problem : problems) {
                        PersistentKifu kifu = kifuRepository.getKifuById(problem.getProblem().getKifuId(), true);
                        if (kifu != null && kifu.getKifuUsf() != null) {
                            usfStrings.add(kifu.getKifuUsf());
                        }
                    }
                }

            } else if (lessonIdParam != null) {
                int lessonId = Integer.parseInt(lessonIdParam);
                LOGGER.info("Downloading lesson: " + lessonId);
                PersistentLesson lesson = lessonRepository.getLesson(lessonId);
                if (lesson == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Lesson not found: " + lessonId);
                    return;
                }
                filename = sanitizeFilename(lesson.getTitle());
                List<LessonChapterDto> chapters = lessonRepository.listLessonChapters(lessonId);
                if (chapters != null && !chapters.isEmpty()) {
                    for (LessonChapterDto chapter : chapters) {
                        if (chapter.getKifuUsf() != null) {
                            usfStrings.add(chapter.getKifuUsf());
                        }
                    }
                } else if (lesson.getKifuId() != null) {
                    PersistentKifu kifu = kifuRepository.getKifuById(lesson.getKifuId(), true);
                    if (kifu != null && kifu.getKifuUsf() != null) {
                        usfStrings.add(kifu.getKifuUsf());
                    }
                }

            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Missing parameter: one of kifuId, collectionId, problemCollectionId, or lessonId is required" +
                                ".");
                return;
            }

            if (usfStrings.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No kifu records found for the given ID.");
                return;
            }

            String content = String.join("\n", usfStrings);

            resp.setContentType("text/plain; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".usf\"");

            try (PrintWriter writer = new PrintWriter(resp.getOutputStream())) {
                writer.print(content);
            }

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error preparing kifu download", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error preparing download: " + e.getMessage());
        }
    }

    private String sanitizeFilename(final String name) {
        if (name == null || name.isEmpty()) {
            return "kifu";
        }
        return name.replaceAll("[^a-zA-Z0-9._\\- ]", "_").trim();
    }
}
