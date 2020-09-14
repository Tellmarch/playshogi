package com.playshogi.library.models.games;

import com.playshogi.library.models.Move;
import com.playshogi.library.models.Position;

public interface GameRulesEngine<P extends Position> {

    void playMoveInPosition(P position, Move move);

    void undoMoveInPosition(P position, Move move);

    boolean isMoveLegalInPosition(P position, Move move);

}
