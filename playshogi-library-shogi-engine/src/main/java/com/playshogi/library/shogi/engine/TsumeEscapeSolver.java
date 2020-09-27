package com.playshogi.library.shogi.engine;

import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import static com.playshogi.library.shogi.engine.EscapeTsumeResult.EscapeTsumeEnum.ESCAPE;
import static com.playshogi.library.shogi.engine.EscapeTsumeResult.EscapeTsumeEnum.TSUME;


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

        for (ShogiMove move : rulesEngine.getAllPossibleMoves(position)) {
            rulesEngine.playMoveInPosition(position, move);
            if (!rulesEngine.isPositionCheck(position, Player.WHITE)) { // If Gote is still in check, there is no
                // need to ask the engine
                PositionEvaluation evaluation = queuedTsumeSolver.analyseTsume(position);
                if (evaluation.getBestMove() == null) {
                    //No best move for sente: it is not a tsume, got successfully escaped.
                    return new EscapeTsumeResult(ESCAPE, move);
                }
            }
            rulesEngine.undoMoveInPosition(position, move);
        }

        return new EscapeTsumeResult(TSUME);
    }
}
