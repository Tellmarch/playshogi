package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.shogi.models.position.ShogiBoardState;
import com.playshogi.library.shogi.models.position.Square;

public class KnightMovement extends AbstractPieceMovement {

    private static final int[][] KNIGHT_ALLOWED_DCOL_DROW = {{+1, -2}, {-1, -2}};

    public KnightMovement() {
        super(KNIGHT_ALLOWED_DCOL_DROW);
    }

    @Override
    public boolean isDropValid(final ShogiBoardState boardState, final Square to) {
        return to.getRow() >= ShogiBoardState.FIRST_ROW + 2;
    }

    @Override
    public boolean isUnpromoteValid(final ShogiBoardState boardState, final Square to) {
        return isDropValid(boardState, to);
    }

}
