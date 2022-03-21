package com.playshogi.library.shogi.models.formats.kif;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.kif.KifUtils.PieceParsingResult;
import com.playshogi.library.shogi.models.moves.*;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.Optional;

public class KifMoveConverter {

    public static ShogiMove fromKifString(final String str, final ShogiPosition shogiPosition,
                                          final ShogiMove previousMove) {

        Player player = shogiPosition.getPlayerToMove();

        if (str.startsWith("投了")) {
            return new SpecialMove(player, SpecialMoveType.RESIGN);
        } else if (str.startsWith("反則勝ち")) {
            return new SpecialMove(player, SpecialMoveType.ILLEGAL_MOVE);
        } else if (str.startsWith("千日手")) {
            return new SpecialMove(player, SpecialMoveType.SENNICHITE);
        } else if (str.startsWith("持将棋")) {
            return new SpecialMove(player, SpecialMoveType.JISHOGI);
        } else if (str.startsWith("中断")) {
            return new SpecialMove(player, SpecialMoveType.BREAK);
        } else if (str.startsWith("切れ負け")) {
            return new SpecialMove(player, SpecialMoveType.TIMEOUT);
        } else if (str.startsWith("詰み")) {
            return new SpecialMove(player, SpecialMoveType.CHECKMATE);
        } else if (str.startsWith("入玉勝ち")) {
            return new SpecialMove(player, SpecialMoveType.NYUGYOKU_WIN);
        }

        int pos = 0;
        // First, read destination coordinates
        Square toSquare;
        char firstChar = str.charAt(0);
        if (firstChar == '同') {
            // same square
            toSquare = ((ToSquareMove) previousMove).getToSquare();
            pos++;
        } else if (firstChar >= '１' && firstChar <= '９') {
            int column = firstChar - '１' + 1;
            int row = KifUtils.getNumberFromJapanese(str.charAt(1));
            toSquare = Square.of(column, row);
            pos += 2;
        } else {
            throw new IllegalArgumentException("Unrecognized move: " + str);
        }

        if (str.charAt(pos) == '　') {
            pos++;
        }

        PieceParsingResult pieceParsingResult = KifUtils.readPiece(str, pos, player);
        Piece piece = pieceParsingResult.piece;
        pos = pieceParsingResult.nextPosition;

        boolean promote = false;
        char c = str.charAt(pos);
        if (c == '成') {
            // Promote
            promote = true;
            c = str.charAt(++pos);
        }
        if (c == '不') {
            c = str.charAt(++pos);
            if (c == '成') {
                // Not Promote
                promote = false;
                c = str.charAt(++pos);
            } else {
                throw new IllegalArgumentException("Error reading the move " + str);
            }
        }
        if (c == '打') {
            // Drop
            return new DropMove(player, piece.getPieceType(), toSquare);
        } else if (c == '(') {
            // Reading the source square
            int column2 = str.charAt(++pos) - '0';
            int row2 = str.charAt(++pos) - '0';
            Square fromSquare = Square.of(column2, row2);

            Optional<Piece> capturedPiece = shogiPosition.getShogiBoardState().getPieceAt(toSquare);
            if (capturedPiece.isPresent()) {
                return new CaptureMove(piece, fromSquare, toSquare, capturedPiece.get(), promote);
            } else {
                return new NormalMove(piece, fromSquare, toSquare, promote);
            }
        } else {
            throw new IllegalArgumentException("Error reading the move " + str);
        }
    }

    public static String toKifString(final ShogiMove move) {

        if (move instanceof NormalMove) {
            NormalMove normalMove = (NormalMove) move;

            return "" + KifUtils.getJapaneseWesternNumber(normalMove.getToSquare().getColumn()) + KifUtils.getJapaneseNumber(normalMove.getToSquare().getRow())
                    + KifUtils.getJapanesePieceSymbol(normalMove.getPiece()) + (normalMove.isPromote() ? "成 (" : " (") + normalMove.getFromSquare().getColumn()
                    + normalMove.getFromSquare().getRow() + ")";

        } else if (move instanceof DropMove) {
            DropMove dropMove = (DropMove) move;

            return "" + KifUtils.getJapaneseWesternNumber(dropMove.getToSquare().getColumn()) + KifUtils.getJapaneseNumber(dropMove.getToSquare().getRow())
                    + KifUtils.getJapanesePieceSymbol(dropMove.getPieceType()) + "打";

        } else if (move instanceof SpecialMove) {
            SpecialMove specialMove = (SpecialMove) move;

            // TODO
            return specialMove.getUsfString();

        } else {
            throw new IllegalArgumentException("Unknown move type " + move);
        }
    }

    public static String toKifStringShort(final ShogiMove move, final ShogiMove previousMove) {
        if (move instanceof NormalMove) {
            NormalMove normalMove = (NormalMove) move;


            String dest = "" +
                    KifUtils.getJapaneseWesternNumber(normalMove.getToSquare().getColumn()) +
                    KifUtils.getJapaneseNumber(normalMove.getToSquare().getRow());

            if (previousMove instanceof ToSquareMove) {
                ToSquareMove toSquareMove = (ToSquareMove) previousMove;
                if (((NormalMove) move).getToSquare().equals(toSquareMove.getToSquare())) {
                    dest = "同";
                }
            }

            return dest + KifUtils.getJapanesePieceSymbol(normalMove.getPiece()) + (normalMove.isPromote() ? "成" : "");

        } else if (move instanceof DropMove) {
            DropMove dropMove = (DropMove) move;

            return "" + KifUtils.getJapaneseWesternNumber(dropMove.getToSquare().getColumn()) + KifUtils.getJapaneseNumber(dropMove.getToSquare().getRow())
                    + KifUtils.getJapanesePieceSymbol(dropMove.getPieceType()) + "打";

        } else if (move instanceof SpecialMove) {
            SpecialMove specialMove = (SpecialMove) move;

            // TODO
            return specialMove.getUsfString();

        } else {
            throw new IllegalArgumentException("Unknown move type " + move);
        }
    }

    public static ShogiMove fromKifStringShort(final String str, final ShogiPosition shogiPosition,
                                               final ShogiMove previousMove) {
        Player player = shogiPosition.getPlayerToMove();

        if (str.startsWith("投了")) {
            return new SpecialMove(player, SpecialMoveType.RESIGN);
        } else if (str.startsWith("反則勝ち")) {
            return new SpecialMove(player, SpecialMoveType.ILLEGAL_MOVE);
        } else if (str.startsWith("千日手")) {
            return new SpecialMove(player, SpecialMoveType.SENNICHITE);
        } else if (str.startsWith("持将棋")) {
            return new SpecialMove(player, SpecialMoveType.JISHOGI);
        } else if (str.startsWith("中断")) {
            return new SpecialMove(player, SpecialMoveType.BREAK);
        } else if (str.startsWith("切れ負け")) {
            return new SpecialMove(player, SpecialMoveType.TIMEOUT);
        } else if (str.startsWith("詰み")) {
            return new SpecialMove(player, SpecialMoveType.CHECKMATE);
        }

        int pos = 0;
        // First, read destination coordinates
        Square toSquare;
        char firstChar = str.charAt(0);
        if (firstChar == '同') {
            // same square
            toSquare = ((ToSquareMove) previousMove).getToSquare();
            pos++;
        } else if (firstChar >= '１' && firstChar <= '９') {
            int column = firstChar - '１' + 1;
            int row = KifUtils.getNumberFromJapanese(str.charAt(1));
            toSquare = Square.of(column, row);
            pos += 2;
        } else {
            throw new IllegalArgumentException("Unrecognized move: " + str);
        }

        Optional<Piece> capturedPiece = shogiPosition.getShogiBoardState().getPieceAt(toSquare);

        if (str.charAt(pos) == '　') {
            pos++;
        }

        if (str.charAt(pos) == '同') {
            pos++;
        }

        PieceParsingResult pieceParsingResult = KifUtils.readPiece(str, pos, player);
        Piece piece = pieceParsingResult.piece;
        pos = pieceParsingResult.nextPosition;

        boolean promote = false;

        if (pos < str.length()) {

            char c = str.charAt(pos);
            if (c == '成') {
                // Promote
                promote = true;
                c = pos < str.length() - 1 ? str.charAt(++pos) : 0;
            }
            if (c == '不') {
                c = str.charAt(++pos);
                if (c == '成') {
                    // Not Promote
                    promote = false;
                    c = pos < str.length() - 1 ? str.charAt(++pos) : 0;
                } else {
                    throw new IllegalArgumentException("Error reading the move " + str);
                }
            }
            if (c == '打') {
                // Drop
                return new DropMove(player, piece.getPieceType(), toSquare);
            } else if (c == '(') {
                // Reading the destination square
                int column2 = str.charAt(++pos) - '0';
                int row2 = str.charAt(++pos) - '0';
                Square fromSquare = Square.of(column2, row2);

                if (capturedPiece.isPresent()) {
                    return new CaptureMove(piece, fromSquare, toSquare, capturedPiece.get(), promote);
                } else {
                    return new NormalMove(piece, fromSquare, toSquare, promote);
                }
            }
        }

        // Try to find source square
        ShogiRulesEngine engine = new ShogiRulesEngine();
        ShogiMove candidateMove = null;
        ShogiMove move;

        for (int r = 1; r <= 9; r++) {
            for (int c = 1; c <= 9; c++) {
                Square fromSquare = Square.of(c, r);
                if (capturedPiece.isPresent()) {
                    move = new CaptureMove(piece, fromSquare, toSquare, capturedPiece.get(), promote);
                } else {
                    move = new NormalMove(piece, fromSquare, toSquare, promote);
                }
                if (engine.isMoveLegalInPosition(shogiPosition, move)) {
                    if (candidateMove != null) {
                        throw new IllegalArgumentException("Multiple candidate moves: " + move + " , " + candidateMove);
                    }
                    candidateMove = move;
                }
            }
        }
        move = new DropMove(shogiPosition.getPlayerToMove(), piece.getPieceType(), toSquare);
        if (candidateMove == null && engine.isMoveLegalInPosition(shogiPosition, move)) {
            candidateMove = move;
        }
        return candidateMove;
    }
}
