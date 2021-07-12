package com.playshogi.library.shogi.engine;

import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import static com.playshogi.library.shogi.engine.EscapeTsumeResult.EscapeTsumeEnum.ESCAPE;
import static com.playshogi.library.shogi.engine.EscapeTsumeResult.EscapeTsumeEnum.ESCAPE_BY_TIMEOUT;


public class TsumeEscapeSolver {

    private final QueuedTsumeSolver queuedTsumeSolver;
    private final ShogiRulesEngine rulesEngine = new ShogiRulesEngine();

    public TsumeEscapeSolver(final QueuedTsumeSolver queuedTsumeSolver) {
        this.queuedTsumeSolver = queuedTsumeSolver;
    }


    public EscapeTsumeResult escapeTsume(final ShogiPosition position) {
        if (position.getPlayerToMove() == Player.BLACK) {
            throw new IllegalStateException("Can only try to escape Tsume from Gote point of view");
        }

        if (!rulesEngine.isPositionCheck(position)) {
            return EscapeTsumeResult.NOT_CHECK;
        }

        int maxMove = 0;
        String variationUsf = "";

        for (ShogiMove move : rulesEngine.getAllPossibleMoves(position)) {
            rulesEngine.playMoveInPosition(position, move);
            if (!rulesEngine.isPositionCheck(position, Player.WHITE)) { // If Gote is still in check, there is no
                // need to ask the engine
                PositionEvaluation evaluation = queuedTsumeSolver.analyseTsume(position);
                if (evaluation.getBestMove() == null) {
                    if ("timeout".equals(evaluation.getMateDetails())) {
                        return new EscapeTsumeResult(ESCAPE_BY_TIMEOUT, move);
                    } else if ("nomate".equals(evaluation.getMateDetails())) {
                        return new EscapeTsumeResult(ESCAPE, move);
                    } else {
                        throw new IllegalStateException("Unknown mate details: " + evaluation.getMateDetails());
                    }
                } else {
                    if (evaluation.getMainVariation().getNumMoves() + 1 > maxMove) {
                        maxMove = evaluation.getMainVariation().getNumMoves() + 1;
                        variationUsf = move.getUsfString() + " " + evaluation.getMainVariation().getUsf();
                    }
                }
            }
            rulesEngine.undoMoveInPosition(position, move);
        }

        return new EscapeTsumeResult(maxMove, variationUsf);
    }
}
