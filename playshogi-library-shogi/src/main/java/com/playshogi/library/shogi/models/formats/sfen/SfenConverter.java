package com.playshogi.library.shogi.models.formats.sfen;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.position.MutableKomadaiState;
import com.playshogi.library.shogi.models.position.MutableShogiBoardState;
import com.playshogi.library.shogi.models.position.ShogiBoardStateImpl;
import com.playshogi.library.shogi.models.position.ShogiPosition;

import java.util.Optional;

import static com.playshogi.library.shogi.models.Player.BLACK;
import static com.playshogi.library.shogi.models.formats.usf.UsfUtil.pieceFromChar;
import static com.playshogi.library.shogi.models.formats.usf.UsfUtil.pieceToString;

public class SfenConverter {
    public static final String INITIAL_POSITION_SFEN = "lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b";

    private static final PieceType[] PIECE_TYPE_VALUES = PieceType.values();

    public static String toSFEN(final ShogiPosition pos) {
        StringBuilder res = new StringBuilder();
        int numspace = 0;
        // First, the pieces on board
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Optional<Piece> piece = pos.getShogiBoardState().getPieceAt(1 + (8 - j), 1 + i);
                if (piece.isPresent()) {
                    if (numspace != 0) {
                        res.append(numspace);
                    }
                    numspace = 0;
                    res.append(pieceToString(piece.get()));
                } else {
                    numspace++;
                }
            }
            if (numspace != 0) {
                res.append(numspace);
            }
            numspace = 0;
            if (i != 8) {

                res.append("/");
            }
        }
        res.append(" ");

        // Which side to move?
        if (pos.getPlayerToMove() == Player.BLACK) {
            res.append("b");
        } else {
            res.append("w");
        }

        // Captured pieces
        int[] capture1 = pos.getSenteKomadai().getPieces();
        StringBuilder c = new StringBuilder();
        for (int i = capture1.length - 1; i >= 0; i--) {
            int n = capture1[i];
            if (n != 0) {
                if (n != 1) {
                    c.append(n);
                }
                c.append(pieceToString(Piece.getPiece(PIECE_TYPE_VALUES[i], Player.BLACK)));
            }
        }
        int[] capture2 = pos.getGoteKomadai().getPieces();
        for (int i = capture2.length - 1; i >= 0; i--) {
            int n = capture2[i];
            if (n != 0) {
                if (n != 1) {
                    c.append(n);
                }
                c.append(pieceToString(Piece.getPiece(PIECE_TYPE_VALUES[i], Player.WHITE)));
            }
        }
        if (c.length() == 0) {
            c.append('-');
        }

        return res + " " + c;
    }

    public static String toSFENWithMoveCount(final ShogiPosition pos) {
        return toSFEN(pos) + " " + (pos.getMoveCount() + 1);
    }

    public static ShogiPosition fromSFEN(final String sfen) {
        MutableShogiBoardState shogiBoardState = new ShogiBoardStateImpl(9, 9);
        String[] fields = sfen.split(" ");

        // Reading board pieces
        String[] rows = fields[0].split("/");
        for (int i = 0; i < 9; i++) {
            String r = rows[i];
            boolean prom = false;
            int k = 0;
            for (int j = 0; j < r.length(); j++) {
                char x = r.charAt(j);
                if (x == '+') {
                    prom = true;
                    j++;
                    x = r.charAt(j);
                }

                Piece p = pieceFromChar(x);

                if (p == null) {
                    int s = x - '0';
                    if (1 <= s && s <= 9) {
                        for (int w = 0; w < s; w++) {
                            shogiBoardState.setPieceAt(1 + (8 - k++), 1 + i, null);
                        }
                    }
                } else {
                    if (prom) {
                        p = p.getPromotedPiece();
                    }
                    shogiBoardState.setPieceAt(1 + (8 - k++), 1 + i, p);
                }
                prom = false;
            }
        }

        MutableKomadaiState senteKomadai = new MutableKomadaiState();
        MutableKomadaiState goteKomadai = new MutableKomadaiState();

        int moveCount = 1;
        Player player = Player.BLACK;
        if (fields[1].equalsIgnoreCase("w")) {
            moveCount = 2;
            player = Player.WHITE;
        }

        // TODO : more validation?
        // Read captured pieces
        if (!fields[2].equals("-")) {
            String r = fields[2];
            char x;
            Piece p;
            int s;
            for (int j = 0; j < r.length(); j++) {
                x = r.charAt(j);
                p = pieceFromChar(x);
                // If not a piece, should be a number
                if (p == null) {
                    s = x - '0';
                    if (1 <= s && s <= 9) {
                        j++;
                        x = r.charAt(j);
                        p = pieceFromChar(x);

                        // If not a piece, should be a number
                        if (p == null) {
                            s = 10 * s + (x - '0');
                            if (1 <= s && s <= 99) {
                                j++;
                                x = r.charAt(j);
                                p = pieceFromChar(x);
                            } else {
                                System.out.println("Error parsing SFEN " + sfen);
                                return new ShogiPosition();
                            }
                        }
                    } else {
                        System.out.println("Error parsing SFEN " + sfen);
                        return new ShogiPosition();
                    }
                } else {
                    s = 1;
                }
                if (p == null) {
                    System.out.println("Error parsing SFEN " + sfen);
                    return new ShogiPosition();
                }
                if (p.isBlackPiece()) {
                    senteKomadai.setPiecesOfType(p.getPieceType(), s);
                } else {
                    goteKomadai.setPiecesOfType(p.getPieceType(), s);
                }
            }
        }

        if (fields.length > 3) {
            moveCount = Integer.parseInt(fields[3]);
        }

        return new ShogiPosition(moveCount - 1, player, shogiBoardState, senteKomadai, goteKomadai);
    }

    public static Player extractPlayer(final String sfen) {
        char playerChar = sfen.charAt(sfen.indexOf(" ") + 1);
        return playerChar == 'b' ? BLACK : Player.WHITE;
    }
}
