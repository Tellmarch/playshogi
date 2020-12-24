package com.playshogi.library.shogi.models.formats.usi;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.psn.PsnUtil;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.moves.*;
import com.playshogi.library.shogi.models.position.ShogiPosition;

import java.util.Optional;

import static com.playshogi.library.shogi.models.Player.BLACK;
import static com.playshogi.library.shogi.models.formats.psn.PsnUtil.char2ColumnNumber;
import static com.playshogi.library.shogi.models.formats.psn.PsnUtil.char2RowNumber;

public class UsiMoveConverter {

    public static ShogiMove fromPsnString(final String moveStr, final ShogiPosition position) {
        int length = moveStr.length();

        if (length < 4 || length > 5) {
            if ("resign".equals(moveStr)) {
                return new SpecialMove(position.getPlayerToMove(), SpecialMoveType.RESIGN);
            }
            throw new IllegalArgumentException("Illegal move string: " + moveStr);
        }

        if (moveStr.charAt(1) == '*') {
            // Drop move
            return new DropMove(position.getPlayerToMove(), PsnUtil.pieceFromChar(moveStr.charAt(0)),
                    Square.of(char2ColumnNumber(moveStr.charAt(2)), char2RowNumber(moveStr.charAt(3))));
        } else {
            boolean promote = length == 5 && moveStr.charAt(4) == '+';
            Square from = Square.of(char2ColumnNumber(moveStr.charAt(0)), char2RowNumber(moveStr.charAt(1)));
            Square to = Square.of(char2ColumnNumber(moveStr.charAt(2)), char2RowNumber(moveStr.charAt(3)));

            Optional<Piece> piece = position.getPieceAt(from);

            if (!piece.isPresent()) {
                throw new IllegalArgumentException("Illegal move string in position: " + moveStr);
            }

            if (position.getPieceAt(to).isPresent()) {
                return new CaptureMove(piece.get(), from, to, position.getPieceAt(to).get(), promote);
            } else {
                return new NormalMove(piece.get(), from, to, promote);
            }
        }
    }

    public static String fromPsnToUsfSTring(final String moveStr, final String sfen) {
        return fromPsnToUsfSTring(moveStr, SfenConverter.extractPlayer(sfen));
    }

    public static String fromPsnToUsfSTring(final String moveStr, final Player player) {

        int length = moveStr.length();

        if (length < 4 || length > 5) {
            if ("resign".equals(moveStr)) {
                return "RSGN";
            }
            throw new IllegalArgumentException("Illegal move string: " + moveStr);
        }

        boolean promote = length == 5 && moveStr.charAt(4) == '+';
        if (promote) {
            return String.valueOf(moveStr.charAt(0)) +
                    moveStr.charAt(1) +
                    moveStr.charAt(2) +
                    // In USF, promotion is denoted by using uppercase
                    Character.toUpperCase(moveStr.charAt(3));
        } else if (moveStr.charAt(1) == '*') {
            // In USF, the player is indicated by the case
            if (player == BLACK) {
                return moveStr;
            } else {
                return String.valueOf(Character.toLowerCase(moveStr.charAt(0))) +
                        moveStr.charAt(1) +
                        moveStr.charAt(2) +
                        moveStr.charAt(3);
            }
        } else {
            // No promotion and no drop: the notation is identical
            return moveStr;
        }
    }
}
