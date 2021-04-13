package com.playshogi.library.shogi.engine.insights;

import com.playshogi.library.shogi.engine.EngineConfiguration;
import com.playshogi.library.shogi.engine.PositionEvaluation;
import com.playshogi.library.shogi.engine.USIConnector;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.position.PositionScore;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GameInsightsAnalyser {

    private static final Logger LOGGER = Logger.getLogger(GameInsightsAnalyser.class.getName());

    private final static int TIME_MS = 200;

    private final EngineConfiguration engineConfiguration = EngineConfiguration.INSIGHTS_ENGINE;
    private final USIConnector usiConnector = new USIConnector(engineConfiguration);


    public GameInsights extractInsights(final GameRecord gameRecord) {
        if (!usiConnector.isConnected()) {
            usiConnector.connect();
        }

        List<PositionEvaluation> evaluations = usiConnector.analyzeKifu(gameRecord.getGameTree(), TIME_MS);

        return extractInsights(gameRecord, evaluations);
    }

    public static GameInsights extractInsights(final GameRecord gameRecord,
                                               final List<PositionEvaluation> evaluations) {
        List<Mistake> blackMistakes = new ArrayList<>();
        List<Mistake> whiteMistakes = new ArrayList<>();
        int blackSum = 0;
        int numBlackMoves = 0;
        int whiteSum = 0;
        int numWhiteMoves = 0;

        PositionEvaluation previousEvaluation = null;

        GameNavigation gameNavigation = new GameNavigation(gameRecord.getGameTree());

        for (int moveCount = 1; moveCount < evaluations.size(); moveCount++) {
            gameNavigation.moveForward();
            PositionEvaluation evaluation = evaluations.get(moveCount);
            String playedMove = gameNavigation.getPreviousMove().toString();

            int cpLoss = 0;
            if (previousEvaluation != null && !playedMove.equals(previousEvaluation.getBestMove())) {
                cpLoss = cpLoss(evaluation.getScore(), previousEvaluation.getScore());
                if (cpLoss > 200) {
                    Mistake mistake = new Mistake(moveCount, previousEvaluation.getSfen(),
                            playedMove, previousEvaluation.getBestMove(),
                            previousEvaluation.getScore(), evaluation.getScore());
                    if (gameNavigation.getPlayerToMove() == Player.BLACK) {
                        whiteMistakes.add(mistake);
                    } else {
                        blackMistakes.add(mistake);
                    }
                }
            }

            if (gameNavigation.getPlayerToMove() == Player.BLACK) {
                whiteSum += cpLoss;
                numWhiteMoves++;
            } else {
                blackSum += cpLoss;
                numBlackMoves++;
            }

            System.out.println("Move #" + moveCount + " - score: " + evaluation.getScore() + " - cpLoss: " + cpLoss);

            previousEvaluation = evaluation;
        }

        return new GameInsights(new PlayerAccuracy(numBlackMoves == 0 ? 0 : blackSum / numBlackMoves, blackMistakes),
                new PlayerAccuracy(numWhiteMoves == 0 ? 0 : whiteSum / numWhiteMoves, whiteMistakes));
    }

    private static int cpLoss(final PositionScore newScore, final PositionScore previousScore) {
        if (previousScore == null) {
            return 0;
        }
        return Math.max(0, newScore.getEvaluationCP() + previousScore.getEvaluationCP());
    }
}
