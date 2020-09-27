package com.playshogi.library.shogi.models.formats.kif;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;

public class KifUtils {

    private static final char[] JAPANESE_NUMBERS = new char[]{'一', '二', '三', '四', '五', '六', '七', '八', '九', '十'};

    static PieceParsingResult readPiece(final String str, int pos) {
        return readPiece(str, pos, Player.BLACK);
    }

    static PieceParsingResult readPiece(final String str, int pos, Player player) {

        while (str.charAt(pos) == ' ') {
            pos++;
        }

        if (str.charAt(pos) == 'v') {
            player = Player.WHITE;
            pos++;
        }

        Piece piece;
        switch (str.charAt(pos)) {
            case '・':
                piece = null;
                break;
            case '歩':
                piece = Piece.getPiece(PieceType.PAWN, player);
                break;
            case '香':
                piece = Piece.getPiece(PieceType.LANCE, player);
                break;
            case '桂':
                piece = Piece.getPiece(PieceType.KNIGHT, player);
                break;
            case '銀':
                piece = Piece.getPiece(PieceType.SILVER, player);
                break;
            case '金':
                piece = Piece.getPiece(PieceType.GOLD, player);
                break;
            case '角':
                piece = Piece.getPiece(PieceType.BISHOP, player);
                break;
            case '飛':
                piece = Piece.getPiece(PieceType.ROOK, player);
                break;
            case '王':
            case '玉':
                piece = Piece.getPiece(PieceType.KING, player);
                break;
            case 'と':
                piece = Piece.getPiece(PieceType.PAWN, player, true);
                break;
            case '馬':
                piece = Piece.getPiece(PieceType.BISHOP, player, true);
                break;
            case '竜':
            case '龍':
                piece = Piece.getPiece(PieceType.ROOK, player, true);
                break;
            case '杏':
                piece = Piece.getPiece(PieceType.LANCE, player, true);
                break;
            case '圭':
                piece = Piece.getPiece(PieceType.KNIGHT, player, true);
                break;
            case '全':
                piece = Piece.getPiece(PieceType.SILVER, player, true);
                break;
            case '成': {
                // Special case : promoted piece...
                pos++;
                switch (str.charAt(pos)) {
                    case '香':
                        piece = Piece.getPiece(PieceType.LANCE, player, true);
                        break;
                    case '桂':
                        piece = Piece.getPiece(PieceType.KNIGHT, player, true);
                        break;
                    case '銀':
                        piece = Piece.getPiece(PieceType.SILVER, player, true);
                        break;
                    default:
                        throw new IllegalArgumentException("Error reading the move " + str);
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Error reading the piece " + str);
        }
        return new PieceParsingResult(piece, pos + 1);
    }

    static class PieceParsingResult {
        public Piece piece;
        public int nextPosition;

        public PieceParsingResult(final Piece piece, final int nextPosition) {
            this.piece = piece;
            this.nextPosition = nextPosition;
        }
    }

    public static int getNumberFromJapanese(final char rowChar) {
        switch (rowChar) {
            case '一':
                return 1;
            case '二':
                return 2;
            case '三':
                return 3;
            case '四':
                return 4;
            case '五':
                return 5;
            case '六':
                return 6;
            case '七':
                return 7;
            case '八':
                return 8;
            case '九':
                return 9;
            case '十':
                return 10;
        }
        throw new IllegalArgumentException("Illegal row number: " + rowChar);
    }

    public static char getJapaneseNumber(final int number) {
        if (number > 0 && number < JAPANESE_NUMBERS.length) {
            return JAPANESE_NUMBERS[number - 1];
        } else {
            throw new IllegalArgumentException("Illegal number: " + number);
        }
    }

    public static char getJapaneseWesternNumber(final int number) {
        return (char) ('１' + (number - 1));
    }

    public static String getJapanesePieceSymbol(final Piece piece) {
        PieceType pieceType = piece.getPieceType();
        if (!piece.isPromoted()) {
            return getJapanesePieceSymbol(pieceType);
        } else {
            switch (pieceType) {
                case BISHOP:
                    return "馬";
                case KNIGHT:
                    return "成桂";
                case LANCE:
                    return "成香";
                case PAWN:
                    return "と";
                case ROOK:
                    return "竜";
                case SILVER:
                    return "成銀";
                default:
                    throw new IllegalArgumentException("Invalid piece: " + piece);
            }
        }
    }

    public static String getJapanesePieceSymbol(final PieceType pieceType) {
        switch (pieceType) {
            case BISHOP:
                return "角";
            case GOLD:
                return "金";
            case KING:
                return "王";
            case KNIGHT:
                return "桂";
            case LANCE:
                return "香";
            case PAWN:
                return "歩";
            case ROOK:
                return "飛";
            case SILVER:
                return "銀";
            default:
                throw new IllegalArgumentException("Invalid piece type: " + pieceType);
        }
    }

}
