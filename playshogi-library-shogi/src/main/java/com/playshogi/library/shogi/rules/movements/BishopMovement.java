package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.shogi.models.position.ShogiBoardState;
import com.playshogi.library.shogi.models.position.Square;

import java.util.ArrayList;
import java.util.List;

public class BishopMovement implements PieceMovement {

    @Override
    public List<Square> getPossibleMoves(final ShogiBoardState boardState, final Square from) {
        List<Square> result = new ArrayList<>();
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, -1, -1, result);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, -1, +1, result);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, +1, -1, result);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, +1, +1, result);
        return result;
    }

    @Override
    public boolean isMoveDxDyValid(final ShogiBoardState boardState, final Square from, final Square to) {
        // Bishops move an equal number of squares horizontally and vertically
        return (Math.abs(to.getColumn() - from.getColumn()) == Math.abs(to.getRow() - from.getRow()) &&
                PieceMovementsUtils.isAlongDirection(boardState, from, to));
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
