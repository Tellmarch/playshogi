package com.playshogi.library.shogi.engine.insights;

import com.playshogi.library.shogi.engine.EngineConfiguration;
import com.playshogi.library.shogi.engine.MultiVariations;
import com.playshogi.library.shogi.engine.PositionEvaluation;
import com.playshogi.library.shogi.engine.USIConnector;
import com.playshogi.library.shogi.models.position.PositionScore;
import com.playshogi.library.shogi.models.record.GameRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ProblemExtractor {

    private static final Logger LOGGER = Logger.getLogger(ProblemExtractor.class.getName());

    private final static int TIME_MS = 200;

    private final EngineConfiguration engineConfiguration = EngineConfiguration.INSIGHTS_ENGINE;
    private final USIConnector usiConnector = new USIConnector(engineConfiguration);


    public List<ExtractedProblem> extractProblems(final GameRecord gameRecord) {
        usiConnector.setLogOutput(false);

        if (!usiConnector.isConnected()) {
            usiConnector.connect();
        }

        List<PositionEvaluation> evaluations = usiConnector.analyzeKifu(gameRecord.getGameTree(), TIME_MS);

        return extractProblems(gameRecord, evaluations);
    }

    public static List<ExtractedProblem> extractProblems(final GameRecord gameRecord,
                                                         final List<PositionEvaluation> evaluations) {
        ArrayList<ExtractedProblem> problems = new ArrayList<>();
        for (PositionEvaluation evaluation : evaluations) {
            MultiVariations variations = evaluation.getMultiVariations();

            if (variations.getVariations().size() <= 1) {
                continue;
            }

            PositionScore topMoveScore = variations.getMainVariation().getScore();
            PositionScore secondToTopScore = variations.getSecondaryVariation(1).getScore();

            if (topMoveScore.isForcedMateForPlayer() && secondToTopScore.isForcedMateForOpponent()) {
                ExtractedProblem problem =
                        new ExtractedProblem(ExtractedProblem.ProblemType.MATE_OR_BE_MATED, evaluation.getSfen(),
                                evaluation.getMainVariation().getUsf());
                System.out.println("Extracted problem: " + problem);
                problems.add(problem);
            } else if (topMoveScore.isForcedMateForPlayer() && secondToTopScore.getEvaluationCP() < 0) {
                ExtractedProblem problem =
                        new ExtractedProblem(ExtractedProblem.ProblemType.MATE_OR_LOSING, evaluation.getSfen(),
                                evaluation.getMainVariation().getUsf());
                System.out.println("Extracted problem: " + problem);
                problems.add(problem);
            } else if (topMoveScore.getEvaluationCP() > 1000 && secondToTopScore.isForcedMateForOpponent()) {
                ExtractedProblem problem =
                        new ExtractedProblem(ExtractedProblem.ProblemType.WINNING_OR_BE_MATED, evaluation.getSfen(),
                                evaluation.getMainVariation().getUsf());
                System.out.println("Extracted problem: " + problem);
                problems.add(problem);
            } else if (topMoveScore.getEvaluationCP() > 1000 && secondToTopScore.getEvaluationCP() < -1000) {
                ExtractedProblem problem =
                        new ExtractedProblem(ExtractedProblem.ProblemType.WINNING_OR_LOSING, evaluation.getSfen(),
                                evaluation.getMainVariation().getUsf());
                System.out.println("Extracted problem: " + problem);
                problems.add(problem);
            } else if (topMoveScore.getEvaluationCP() > 0 && secondToTopScore.isForcedMateForOpponent()) {
                ExtractedProblem problem =
                        new ExtractedProblem(ExtractedProblem.ProblemType.ESCAPE_MATE, evaluation.getSfen(),
                                evaluation.getMainVariation().getUsf());
                System.out.println("Extracted problem: " + problem);
                problems.add(problem);
            }
        }
        return problems;
    }

    public static String problemToUSF(final ExtractedProblem problem) {
        return "USF:1.0\n" +
                "^*" + problem.getSfen() + ":" +
                problem.getVariation().replaceAll(" ", "");
    }

    public static String problemsToUSF(final List<ExtractedProblem> problems) {
        StringBuilder result = new StringBuilder("USF:1.0\n");
        for (ExtractedProblem problem : problems) {
            result.append("^*").append(problem.getSfen()).append(":").append(problem.getVariation().replaceAll(" ",
                    "")).append("\n");
        }

        return result.toString();
    }
}
