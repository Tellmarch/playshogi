package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

import java.util.List;

public class PromotedRookMovement extends AbstractPieceMovement {

    private static final int[][] ALLOWED_DCOL_DROW = {{+1, -1}, {-1, -1}, {+1, +1}, {-1, +1}};

    public PromotedRookMovement() {
        super(ALLOWED_DCOL_DROW);
    }

    @Override
    public List<Square> getPossibleMoves(final ShogiBoardState boardState, final Square from) {
        List<Square> possibleMoves = super.getPossibleMoves(boardState, from);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, 0, -1, possibleMoves);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, 0, +1, possibleMoves);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, -1, 0, possibleMoves);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, +1, 0, possibleMoves);
        return possibleMoves;
    }

    @Override
    public boolean isMoveDxDyValid(final ShogiBoardState boardState, final Square from, final Square to) {
        // TODO write more efficient method?
        return getPossibleMoves(boardState, from).contains(to);
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
