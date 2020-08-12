package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

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
        return PieceMovementsUtils.isAlongDirection(boardState, from, to);
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
