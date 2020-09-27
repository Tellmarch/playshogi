package com.playshogi.library.shogi.models.formats.psn;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.moves.*;
import com.playshogi.library.shogi.models.position.ShogiPosition;

import java.util.Optional;

import static com.playshogi.library.shogi.models.formats.psn.PsnUtil.*;

public class PsnMoveConverter {

    /**
     * For now, only supports the full notation
     */
    public static ShogiMove fromKifString(final String moveStr, final ShogiPosition position) {
        // TODO support abbreviated notation

        String str = moveStr;
        if (str.contains(".")) {
            str = str.substring(str.indexOf(".") + 1);
        }

        if (str.startsWith("--")) {
            return new SpecialMove(position.getPlayerToMove(), SpecialMoveType.RESIGN);
        }

        // Handle drop moves
        if (str.length() == 4 && str.charAt(1) == '*') {
            return new DropMove(position.getPlayerToMove(), pieceFromChar(str.charAt(0)),
                    Square.of(char2ColumnNumber(str.charAt(2)), char2RowNumber(str.charAt(3))));
        }

        // Other moves: either starts with a promoted piece (e.g. "+R") or normal piece ("R")
        boolean promotedPieceMoving = str.charAt(0) == '+';
        PieceType pieceType = pieceFromChar(promotedPieceMoving ? str.charAt(1) : str.charAt(0));

        if (pieceType == null) {
            throw new IllegalArgumentException("Can not parse move: " + moveStr);
        }

        Piece piece = Piece.getPiece(pieceType, position.getPlayerToMove(), promotedPieceMoving);

        str = str.substring(promotedPieceMoving ? 2 : 1);

        if (str.length() < 5 || str.length() > 6) {
            throw new IllegalArgumentException("Can not parse move: " + moveStr);
        }

        Square from = Square.of(char2ColumnNumber(str.charAt(0)), char2RowNumber(str.charAt(1)));
        Square to = Square.of(char2ColumnNumber(str.charAt(3)), char2RowNumber(str.charAt(4)));

        boolean promote = str.length() == 6 && str.charAt(5) == '+';

        if (str.charAt(2) == '-') {
            return new NormalMove(piece, from, to, promote);
        } else if (str.charAt(2) == 'x') {
            Optional<Piece> pieceAt = position.getPieceAt(to);
            if (!pieceAt.isPresent()) {
                throw new IllegalArgumentException("No piece to capture: " + moveStr);
            }
            return new CaptureMove(piece, from, to, pieceAt.get(), promote);
        } else {
            throw new IllegalArgumentException("Can not parse move: " + moveStr);
        }
    }
}
