package com.playshogi.library.shogi.models.formats.csa;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.sfen.LineReader;
import com.playshogi.library.shogi.models.moves.*;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.library.shogi.models.record.*;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.logging.Logger;

/**
 * Reads one single game from an CSA file
 * Not reusable - create a new instance for each game to parse (mostly because it is not thread-safe)
 */
class CsaGameParser {
    private static final Logger LOGGER = Logger.getLogger(CsaGameParser.class.getName());

    private final LineReader lineReader;
    private GameInformation gameInformation;
    private GameResult gameResult;
    private GameTree gameTree;
    private GameNavigation gameNavigation;

    CsaGameParser(final LineReader lineReader) {
        this.lineReader = lineReader;
    }

    GameRecord readGameRecord() {
        readMetaData();
        readInitialPosition();
        readMoves();

        return new GameRecord(gameInformation, gameTree, gameResult);
    }

    private void readMoves() {
        gameTree = new GameTree();
        gameNavigation = new GameNavigation(new ShogiRulesEngine(), gameTree);

        while (lineReader.hasNextLine()) {
            String line = lineReader.peekNextLine();

            if (line.startsWith("'")) {
                // Comment: ignore
                lineReader.nextLine();
                continue;
            }

            if (line.startsWith("T")) {
                // Time information: ignore
                lineReader.nextLine();
                continue;
            }

            if (!(line.startsWith("+") || line.startsWith("-") || line.startsWith("%"))) {
                // Not a move
                break;
            }
            lineReader.nextLine(); // Consumes the line

            if (line.startsWith("%")) {
                SpecialMoveType moveType = readSpecialMoveType(line);
                Player player = gameNavigation.getPlayerToMove();
                Move move = new SpecialMove(player, moveType);
                gameNavigation.addMove(move);
                if (gameResult == null) {
                    if (moveType.isLosingMove()) {
                        gameResult = player == Player.BLACK ? GameResult.WHITE_WIN : GameResult.BLACK_WIN;
                    } else {
                        gameResult = GameResult.UNKNOWN;
                    }
                }
            } else if ("+".equals(line)) {
                if (gameNavigation.getPlayerToMove() != Player.BLACK) {
                    throw new IllegalStateException("Expecting White's turn, but Kifu says it is Black to move");
                }
            } else if ("-".equals(line)) {
                if (gameNavigation.getPlayerToMove() != Player.WHITE) {
                    throw new IllegalStateException("Expecting Black's turn, but Kifu says it is White to move");
                }
            } else {
                gameNavigation.addMove(readRegularMove(line));
            }
        }
    }

    private static final Square HAND = Square.of(0, 0);

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private Move readRegularMove(final String line) {
        try {
            Player player = line.charAt(0) == '+' ? Player.BLACK : Player.WHITE;
            Square from = Square.of(line.charAt(1) - '0', line.charAt(2) - '0');
            Square to = Square.of(line.charAt(3) - '0', line.charAt(4) - '0');
            Piece endPiece = readPiece(line.substring(5, 7), player);
            if (HAND.equals(from)) {
                return new DropMove(player, endPiece.getPieceType(), to);
            } else {
                Piece startingPiece = gameNavigation.getPosition().getPieceAt(from).get();
                boolean promote = endPiece.isPromoted() && !startingPiece.isPromoted();
                if (gameNavigation.getPosition().isEmptySquare(to)) {
                    return new NormalMove(startingPiece, from, to, promote);
                } else {
                    Piece capturedPiece = gameNavigation.getPosition().getPieceAt(to).get();
                    return new CaptureMove(startingPiece, from, to, capturedPiece, promote);
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Invalid move: " + line, ex);
        }
    }

    private Piece readPiece(final String piece, final Player player) {
        switch (piece) {
            case "FU":
                return player == Player.BLACK ? Piece.SENTE_PAWN : Piece.GOTE_PAWN;
            case "KY":
                return player == Player.BLACK ? Piece.SENTE_LANCE : Piece.GOTE_LANCE;
            case "KE":
                return player == Player.BLACK ? Piece.SENTE_KNIGHT : Piece.GOTE_KNIGHT;
            case "GI":
                return player == Player.BLACK ? Piece.SENTE_SILVER : Piece.GOTE_SILVER;
            case "KI":
                return player == Player.BLACK ? Piece.SENTE_GOLD : Piece.GOTE_GOLD;
            case "KA":
                return player == Player.BLACK ? Piece.SENTE_BISHOP : Piece.GOTE_BISHOP;
            case "HI":
                return player == Player.BLACK ? Piece.SENTE_ROOK : Piece.GOTE_ROOK;
            case "OU":
                return player == Player.BLACK ? Piece.SENTE_KING : Piece.GOTE_KING;
            case "TO":
                return player == Player.BLACK ? Piece.SENTE_PROMOTED_PAWN : Piece.GOTE_PROMOTED_PAWN;
            case "NY":
                return player == Player.BLACK ? Piece.SENTE_PROMOTED_LANCE : Piece.GOTE_PROMOTED_LANCE;
            case "NK":
                return player == Player.BLACK ? Piece.SENTE_PROMOTED_KNIGHT : Piece.GOTE_PROMOTED_KNIGHT;
            case "NG":
                return player == Player.BLACK ? Piece.SENTE_PROMOTED_SILVER : Piece.GOTE_PROMOTED_SILVER;
            case "UM":
                return player == Player.BLACK ? Piece.SENTE_PROMOTED_BISHOP : Piece.GOTE_PROMOTED_BISHOP;
            case "RY":
                return player == Player.BLACK ? Piece.SENTE_PROMOTED_ROOK : Piece.GOTE_PROMOTED_ROOK;
            default:
                throw new IllegalStateException("Unexpected piece: " + piece);
        }
    }

    private SpecialMoveType readSpecialMoveType(final String line) {
        switch (line) {
            case "%TORYO":
                return SpecialMoveType.RESIGN;
            case "%CHUDAN":
                return SpecialMoveType.BREAK;
            case "%SENNICHITE":
                return SpecialMoveType.SENNICHITE;
            case "%TIME_UP":
                return SpecialMoveType.TIMEOUT;
            case "%ILLEGAL_MOVE":
            case "%+ILLEGAL_ACTION":
            case "%-ILLEGAL_ACTION":
                return SpecialMoveType.ILLEGAL_MOVE;
            case "%TSUMI":
                return SpecialMoveType.CHECKMATE;
            case "%JISHOGI":
            case "%HIKIWAKE":
                return SpecialMoveType.JISHOGI;
            case "%KACHI":
            case "%MATTA":
            case "%FUZUMI":
            case "%ERROR":
                return SpecialMoveType.OTHER;
            default:
                throw new IllegalStateException("Unexpected special move: " + line);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void readInitialPosition() {
        String line = lineReader.peekNextLine();
        if (!line.startsWith("P")) {
            // Not a position
            return;
        }

        lineReader.nextLine(); // Consumes the line
        if ("PI".equals(line)) {
            // Even game
        } else {
            throw new UnsupportedOperationException("Custom starting position in CSA file: " + line);
        }
    }

    private void readMetaData() {
        gameInformation = new GameInformation();

        while (lineReader.hasNextLine()) {
            String line = lineReader.peekNextLine();

            if (line.startsWith("'")) {
                // Comment: ignore
                lineReader.nextLine();
                continue;
            }

            if (line.startsWith("V")) {
                // Version: ignore
                lineReader.nextLine();
                continue;
            }

            if (line.startsWith("$")) {
                lineReader.nextLine(); // consumes the line
                String[] split = line.split(":", 2);
                if (split.length != 2) {
                    throw new IllegalStateException("Unexpected metadata line: " + line);
                }
                String keyword = split[0].substring(1);
                String value = split[1];

                switch (keyword) {
                    case "EVENT":
                        gameInformation.setEvent(value);
                        break;
                    case "SITE":
                        gameInformation.setLocation(value);
                        break;
                    case "START_TIME":
                        String date = value.substring(0, 10);
                        gameInformation.setDate(date);
                        break;
                    case "OPENING":
                        gameInformation.setOpening(value);
                        break;
                    case "RESULT":
                        if ("sente_win".equalsIgnoreCase(value)) {
                            gameResult = GameResult.BLACK_WIN;
                        } else if ("gote_win".equalsIgnoreCase(value)) {
                            gameResult = GameResult.WHITE_WIN;
                        } else if ("draw".equalsIgnoreCase(value)) {
                            gameResult = GameResult.OTHER;
                        } else {
                            gameResult = GameResult.UNKNOWN;
                        }
                        break;
                    case "END_TIME":
                        break;
                    default:
                        LOGGER.info("Ignoring meta tag: " + keyword);
                        break;
                }

            } else if (line.startsWith("N")) {
                lineReader.nextLine(); // consumes the line
                if (line.startsWith("N+")) {
                    gameInformation.setBlack(line.substring(2));
                } else if (line.startsWith("N-")) {
                    gameInformation.setWhite(line.substring(2));
                } else {
                    throw new IllegalStateException("Unexpected metadata line: " + line);
                }

            } else {
                return; // Not a metadata line; end of section
            }
        }
    }
}
