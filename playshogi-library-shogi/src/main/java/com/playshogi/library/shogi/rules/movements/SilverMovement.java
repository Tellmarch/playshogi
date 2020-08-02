package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

public class SilverMovement extends AbstractPieceMovement {

    private static final int[][] SILVER_ALLOWED_DCOL_DROW = {{+1, +1}, {+1, -1}, {0, -1}, {-1, +1}, {-1, -1}};

    public SilverMovement() {
        super(SILVER_ALLOWED_DCOL_DROW);
    }

    @Override
    public boolean isDropValid(final ShogiBoardState boardState, final Square to) {
        return true;
    }

    @Override
    public boolean isUnpromoteValid(final ShogiBoardState boardState, final Square to) {
        return true;
    }
}
