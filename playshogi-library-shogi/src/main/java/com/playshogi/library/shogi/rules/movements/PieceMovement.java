package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

import java.util.List;

public interface PieceMovement {

    List<Square> getPossibleMoves(ShogiBoardState boardState, Square from);

    /**
     * Does not need to validate the pieces at from and to.
     */
    boolean isMoveDxDyValid(ShogiBoardState boardState, Square from, Square to);

    /**
     * Does not need to validate the empty square at to.
     */
    boolean isDropValid(ShogiBoardState boardState, Square to);

}
