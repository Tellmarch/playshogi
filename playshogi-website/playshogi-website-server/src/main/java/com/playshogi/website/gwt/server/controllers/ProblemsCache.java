package com.playshogi.website.gwt.server.controllers;

import com.playshogi.library.shogi.engine.insights.ExtractedProblem;
import com.playshogi.library.shogi.engine.insights.GameInsights;
import com.playshogi.library.shogi.engine.insights.Mistake;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public enum ProblemsCache {
    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(ProblemsCache.class.getName());

    private final Map<String, List<ExtractedProblem>> extractedProblemMap = new ConcurrentHashMap<>(); // key: kifuUsf

    public List<ExtractedProblem> getExtractedProblemsForKifu(final String kifuUsf) {
        LOGGER.info("Querying problems from kifu: " + kifuUsf + " " + extractedProblemMap.get(kifuUsf));
        if (!extractedProblemMap.containsKey(kifuUsf)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(extractedProblemMap.get(kifuUsf));
    }

    public void extractProblemsFromInsights(final String kifuUsf, final GameInsights insights) {
        LOGGER.info("Extracting problems from insights: " + kifuUsf + " " + insights);
        if (extractedProblemMap.containsKey(kifuUsf)) {
            LOGGER.info("Not extracting problems for kifu, they are already there");
            return;
        }

        ArrayList<ExtractedProblem> problems = new ArrayList<>();

        for (Mistake mistake : insights.getBlackAccuracy().getMistakes()) {
            ExtractedProblem problem = extractProblemFromMistake(mistake);
            if (problem != null) problems.add(problem);
        }

        for (Mistake mistake : insights.getWhiteAccuracy().getMistakes()) {
            ExtractedProblem problem = extractProblemFromMistake(mistake);
            if (problem != null) problems.add(problem);
        }

        extractedProblemMap.putIfAbsent(kifuUsf, new CopyOnWriteArrayList<>(problems));
    }

    private ExtractedProblem extractProblemFromMistake(final Mistake mistake) {
        if (mistake.getType() == Mistake.Type.BLUNDER || mistake.getType() == Mistake.Type.MISTAKE) {
            ExtractedProblem problem = new ExtractedProblem(ExtractedProblem.ProblemType.LEARN_FROM_MISTAKE,
                    mistake.getPositionSfen(),
                    mistake.getPositionEvaluation().getMainVariation().getUsf(), mistake.getPreviousMove(), null);
            LOGGER.info("Extracted problem from mistake: " + problem);
            return problem;
        }

        return null;
    }
}
