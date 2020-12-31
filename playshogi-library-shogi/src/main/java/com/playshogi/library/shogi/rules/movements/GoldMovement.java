package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.shogi.models.position.ShogiBoardState;
import com.playshogi.library.shogi.models.position.Square;

public class GoldMovement extends AbstractPieceMovement {
    private static final int[][] GOLD_ALLOWED_DCOL_DROW = {{+1, -1}, {+1, 0}, {0, -1}, {0, +1}, {-1, -1},
            {-1, 0}};

    public GoldMovement() {
        super(GOLD_ALLOWED_DCOL_DROW);
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
