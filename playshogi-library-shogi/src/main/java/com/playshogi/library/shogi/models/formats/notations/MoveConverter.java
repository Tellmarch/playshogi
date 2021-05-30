package com.playshogi.library.shogi.models.formats.notations;

import com.playshogi.library.shogi.models.formats.psn.PsnUtil;
import com.playshogi.library.shogi.models.moves.*;

public class MoveConverter {


    public static String toNumericalWestern(final ShogiMove move, final ShogiMove previousMove) {
        if (move instanceof NormalMove) {
            NormalMove normalMove = (NormalMove) move;

            String dest = "" +
                    columnNumber2Char(normalMove.getToSquare().getColumn()) +
                    rowNumber2Char(normalMove.getToSquare().getRow());

            if (previousMove instanceof ToSquareMove) {
                ToSquareMove toSquareMove = (ToSquareMove) previousMove;
                if (normalMove.getToSquare().equals(toSquareMove.getToSquare())) {
                    dest = "";
                }
            }

            char separator = (move instanceof CaptureMove) ? 'x' : '-';

            return PsnUtil.pieceToString(normalMove.getPiece()) + separator + dest + (normalMove.isPromote() ? "+" :
                    "");

        } else if (move instanceof DropMove) {
            DropMove dropMove = (DropMove) move;

            String dest = "" +
                    columnNumber2Char(dropMove.getToSquare().getColumn()) +
                    rowNumber2Char(dropMove.getToSquare().getRow());

            return PsnUtil.pieceTypeToChar(dropMove.getPieceType()) + "*" + dest;

        } else if (move instanceof SpecialMove) {
            SpecialMove specialMove = (SpecialMove) move;

            // TODO
            return specialMove.getUsfString();

        } else {
            throw new IllegalArgumentException("Unknown move type " + move);
        }
    }


    public static char columnNumber2Char(final int col) {
        return (char) (col + '0');
    }

    public static char rowNumber2Char(final int row) {
        return (char) (row + '0');
    }

}
