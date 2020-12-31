package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.shogi.models.position.ShogiBoardState;
import com.playshogi.library.shogi.models.position.Square;

import java.util.List;

public class PromotedBishopMovement extends AbstractPieceMovement {

    private static final int[][] ALLOWED_DCOL_DROW = {{+1, 0}, {0, -1}, {0, +1}, {-1, 0}};

    public PromotedBishopMovement() {
        super(ALLOWED_DCOL_DROW);
    }

    @Override
    public List<Square> getPossibleMoves(final ShogiBoardState boardState, final Square from) {
        List<Square> possibleMoves = super.getPossibleMoves(boardState, from);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, -1, -1, possibleMoves);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, -1, +1, possibleMoves);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, +1, -1, possibleMoves);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, +1, +1, possibleMoves);
        return possibleMoves;
    }

    @Override
    public boolean isMoveDxDyValid(final ShogiBoardState boardState, final Square from, final Square to) {
        // Bishops move an equal number of squares horizontally and vertically
        if (Math.abs(to.getColumn() - from.getColumn()) + Math.abs(to.getRow() - from.getRow()) == 1)
            return true;
        return (Math.abs(to.getColumn() - from.getColumn()) == Math.abs(to.getRow() - from.getRow()) &&
                PieceMovementsUtils.isAlongDirection(boardState, from, to));
    }

    @Override
    public boolean isDropValid(final ShogiBoardState boardState, final Square to) {
        return false;
    }

    @Override
    public boolean isUnpromoteValid(final ShogiBoardState boardState, final Square to) {
        return false;
    }

}
