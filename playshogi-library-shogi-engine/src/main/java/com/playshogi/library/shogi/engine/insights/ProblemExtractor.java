package com.playshogi.library.shogi.engine.insights;

import com.playshogi.library.shogi.engine.EngineConfiguration;
import com.playshogi.library.shogi.engine.MultiVariations;
import com.playshogi.library.shogi.engine.PositionEvaluation;
import com.playshogi.library.shogi.engine.USIConnector;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.position.PositionScore;
import com.playshogi.library.shogi.models.record.GameInformation;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.playshogi.library.shogi.engine.insights.ExtractedProblem.ProblemType.*;

public class ProblemExtractor {

    private static final Logger LOGGER = Logger.getLogger(ProblemExtractor.class.getName());

    private final static int TIME_MS = 2000;

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
        GameNavigation gameNavigation = new GameNavigation(gameRecord.getGameTree());

        Move previousMove;

        for (PositionEvaluation evaluation : evaluations) {
            previousMove = gameNavigation.getCurrentMove();
            gameNavigation.moveForward();
            MultiVariations variations = evaluation.getMultiVariations();

            if (variations.getVariations().size() <= 1) {
                continue;
            }

            PositionScore topMoveScore = variations.getMainVariation().getScore();
            PositionScore secondToTopScore = variations.getSecondaryVariation(1).getScore();

            if (topMoveScore.isForcedMateForPlayer() && secondToTopScore.isForcedMateForOpponent()) {
                problems.add(getExtractedProblem(evaluation, previousMove, gameRecord.getGameInformation(),
                        MATE_OR_BE_MATED));
            } else if (topMoveScore.isForcedMateForPlayer() && secondToTopScore.getEvaluationCP() < 0) {
                problems.add(getExtractedProblem(evaluation, previousMove, gameRecord.getGameInformation(),
                        MATE_OR_LOSING));
            } else if (topMoveScore.getEvaluationCP() > 1000 && secondToTopScore.isForcedMateForOpponent()) {
                problems.add(getExtractedProblem(evaluation, previousMove, gameRecord.getGameInformation(),
                        WINNING_OR_BE_MATED));
            } else if (topMoveScore.getEvaluationCP() > 1000 && secondToTopScore.getEvaluationCP() < -1000) {
                problems.add(getExtractedProblem(evaluation, previousMove, gameRecord.getGameInformation(),
                        WINNING_OR_LOSING));
            } else if (topMoveScore.getEvaluationCP() > 0 && secondToTopScore.isForcedMateForOpponent()) {
                problems.add(getExtractedProblem(evaluation, previousMove, gameRecord.getGameInformation(),
                        ESCAPE_MATE));
            }
        }
        return problems;
    }

    private static ExtractedProblem getExtractedProblem(final PositionEvaluation evaluation,
                                                        final Move previousMove,
                                                        final GameInformation gameInformation,
                                                        final ExtractedProblem.ProblemType type) {
        ExtractedProblem problem =
                new ExtractedProblem(type, evaluation.getSfen(),
                        evaluation.getMainVariation().getUsf(), previousMove.toString(), gameInformation);
        System.out.println("Extracted problem: " + problem);
        return problem;
    }

    public static String problemToUSF(final ExtractedProblem problem) {
        StringBuilder builder = new StringBuilder()
                .append("^*")
                .append(problem.getSfen())
                .append(":")
                .append(problem.getVariation().replaceAll(" ", "")).append("\n");
        UsfFormat.writeGameTags(problem.getGameInformation(), builder);
        return builder
                .append(".0\n")
                .append("X:PLAYSHOGI:PROBLEMTYPE:").append(problem.getType().name()).append("\n")
                .append("X:PLAYSHOGI:PREVIOUSMOVE:").append(problem.getPreviousMove()).append("\n")
                .toString();
    }

    public static String problemsToUSF(final List<ExtractedProblem> problems) {
        StringBuilder result = new StringBuilder("USF:1.0\n");
        for (ExtractedProblem problem : problems) {
            result.append(problemToUSF(problem)).append("\n");
        }

        return result.toString();
    }
}
