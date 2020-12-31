package com.playshogi.library.shogi.models.formats.uss;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.sfen.LineReader;
import com.playshogi.library.shogi.models.formats.sfen.StringLineReader;
import com.playshogi.library.shogi.models.position.MutableKomadaiState;
import com.playshogi.library.shogi.models.position.ShogiPosition;

/**
 * USS (Unnamed Shogi Standard) - Format of shogi positions found in old websites
 * Ex:
 * White in hand: R2 G4 S N3 L4 P17
 * 9  8  7  6  5  4  3  2  1
 * +---------------------------+
 * | *  *  *  *  * wB+wK  *  * |a
 * | *  *  *  *  *  *  *  * bB+|b
 * | *  *  *  *  * bP+wS  *  * |c
 * | *  *  *  *  *  *  *  *  * |d
 * | *  *  *  *  *  *  *  *  * |e
 * | *  *  *  *  *  *  *  *  * |f
 * | *  *  *  *  *  *  *  *  * |g
 * | *  *  *  *  *  *  *  *  * |h
 * | *  *  *  *  *  *  *  *  * |i
 * +---------------------------+
 * Black in hand: S2 N
 */
public class UssConverter {


    public static ShogiPosition fromUSS(final String input) {
        return fromUSS(new StringLineReader(input));
    }


    public static ShogiPosition fromUSS(final LineReader lineReader) {
        ShogiPosition position = new ShogiPosition();
        String line = lineReader.nextLine();
        if (line.startsWith("White in hand:")) {
            readPiecesInHand(line, position.getGoteKomadai());
            line = lineReader.nextLine();
        }
        if (!"9  8  7  6  5  4  3  2  1".equals(line.trim())) {
            throw new IllegalArgumentException();
        }
        line = lineReader.nextLine();
        if (!"+---------------------------+".equals(line.trim())) {
            throw new IllegalArgumentException();
        }

        for (int row = 1; row <= 9; row++) {
            line = lineReader.nextLine().trim();
            if (line.length() != 30) {
                throw new IllegalArgumentException();
            }
            int pos = 1;
            for (int column = 9; column >= 1; column--) {
                String piece = line.substring(pos, pos + 3);
                pos += 3;
                if (" * ".equals(piece)) {
                    continue;
                }
                position.getMutableShogiBoardState().setPieceAt(Square.of(column, row), readPiece(piece));
            }
        }

        line = lineReader.nextLine();
        if (!"+---------------------------+".equals(line.trim())) {
            throw new IllegalArgumentException();
        }

        if (lineReader.hasNextLine()) {
            line = lineReader.nextLine();
            if (line.startsWith("Black in hand:")) {
                readPiecesInHand(line, position.getSenteKomadai());
            }
        }
        return position;
    }

    private static Piece readPiece(final String piece) {
        return Piece.getPiece(pieceFromChar(piece.charAt(1)),
                playerFromChar(piece.charAt(0)),
                piece.charAt(2) == '+');
    }

    private static Player playerFromChar(final char c) {
        if (c == 'b') {
            return Player.BLACK;
        } else if (c == 'w') {
            return Player.WHITE;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void readPiecesInHand(final String line, final MutableKomadaiState komadai) {
        String piecesString = line.substring(line.indexOf(":") + 1).trim();
        if ("nothing".equalsIgnoreCase(piecesString)) {
            return;
        }
        String[] pieces = piecesString.split(" ");
        for (String piece : pieces) {
            PieceType pieceType = pieceFromChar(piece.charAt(0));
            int num;
            if (piece.length() == 1) {
                num = 1;
            } else {
                num = Integer.parseInt(piece.substring(1));
            }
            komadai.setPiecesOfType(pieceType, num);
        }
    }

    private static PieceType pieceFromChar(final char x) {
        switch (x) {
            case 'P':
            case 'p':
                return PieceType.PAWN;
            case 'L':
            case 'l':
                return PieceType.LANCE;
            case 'N':
            case 'n':
                return PieceType.KNIGHT;
            case 'S':
            case 's':
                return PieceType.SILVER;
            case 'G':
            case 'g':
                return PieceType.GOLD;
            case 'B':
            case 'b':
                return PieceType.BISHOP;
            case 'R':
            case 'r':
                return PieceType.ROOK;
            case 'K':
            case 'k':
                return PieceType.KING;
        }
        throw new IllegalArgumentException();
    }
}
