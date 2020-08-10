package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

import java.util.ArrayList;
import java.util.List;

public class RookMovement implements PieceMovement {

    @Override
    public List<Square> getPossibleMoves(final ShogiBoardState boardState, final Square from) {
        List<Square> result = new ArrayList<Square>();
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, 0, -1, result);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, 0, +1, result);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, -1, 0, result);
        PieceMovementsUtils.addSquaresAlongDirection(boardState, from, +1, 0, result);
        return result;
    }

    @Override
    public boolean isMoveDxDyValid(final ShogiBoardState boardState, final Square from, final Square to) {
        int dRow = Math.abs(to.getRow() - from.getRow());
        int dColumn = Math.abs(to.getColumn() - from.getColumn());
        return (dRow == 0 || dColumn == 0) && getPossibleMoves(boardState, from).contains(to);
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
