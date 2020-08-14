package com.playshogi.library.shogi.models.formats.sfen;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.position.KomadaiState;
import com.playshogi.library.shogi.models.position.ShogiBoardState;
import com.playshogi.library.shogi.models.position.ShogiBoardStateImpl;
import com.playshogi.library.shogi.models.position.ShogiPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static com.playshogi.library.shogi.models.formats.usf.UsfUtil.pieceFromChar;
import static com.playshogi.library.shogi.models.formats.usf.UsfUtil.pieceToString;

import java.util.Optional;

public class SfenConverter {

    private static final PieceType[] PIECE_TYPE_VALUES = PieceType.values();

    public static String toSFEN(final ShogiPosition pos) {
        String res = "";
        int numspace = 0;
        // First, the pieces on board
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Optional<Piece> piece = pos.getShogiBoardState().getPieceAt(1 + (8 - j), 1 + i);
                if (piece.isPresent()) {
                    if (numspace != 0) {
                        res += numspace;
                    }
                    numspace = 0;
                    res += pieceToString(piece.get());
                } else {
                    numspace++;
                }
            }
            if (numspace != 0) {
                res += numspace;
            }
            numspace = 0;
            if (i != 8) {

                res += "/";
            }
        }
        res += " ";

        // Which side to move?
        if (pos.isSenteToPlay()) {
            res += "b";
        } else {
            res += "w";
        }

        // Captured pieces
        int[] capture1 = pos.getSenteKomadai().getPieces();
        String c = "";
        for (int i = capture1.length - 1; i >= 0; i--) {
            int n = capture1[i];
            if (n != 0) {
                if (n != 1) {
                    c += n;
                }
                c += pieceToString(Piece.getPiece(PIECE_TYPE_VALUES[i], true));
            }
        }
        int[] capture2 = pos.getGoteKomadai().getPieces();
        for (int i = capture2.length - 1; i >= 0; i--) {
            int n = capture2[i];
            if (n != 0) {
                if (n != 1) {
                    c += n;
                }
                c += pieceToString(Piece.getPiece(PIECE_TYPE_VALUES[i], false));
            }
        }
        if (c.equals("")) {
            c = "-";
        }

        // Should we add the move count?

        return res + " " + c;
    }

    public static ShogiPosition fromSFEN(final String sfen) {
        ShogiBoardState shogiBoardState = new ShogiBoardStateImpl(9, 9);
        String[] fields = sfen.split(" ");
        Collection<PieceType> pieces = new ArrayList<>(Arrays.asList(
            PieceType.KING, PieceType.KING,
            PieceType.ROOK, PieceType.ROOK,
            PieceType.BISHOP, PieceType.BISHOP,
            PieceType.GOLD, PieceType.GOLD, PieceType.GOLD, PieceType.GOLD,
            PieceType.SILVER, PieceType.SILVER, PieceType.SILVER, PieceType.SILVER,
            PieceType.KNIGHT, PieceType.KNIGHT, PieceType.KNIGHT, PieceType.KNIGHT,
            PieceType.LANCE, PieceType.LANCE, PieceType.LANCE, PieceType.LANCE,
            PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN,
            PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN,
            PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN, PieceType.PAWN
        ));

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
                    pieces.remove(p.getPieceType());
                }
                prom = false;
            }
        }

        KomadaiState senteKomadai = new KomadaiState();
        KomadaiState goteKomadai = new KomadaiState();

        boolean senteTurn = true;
        if (fields[1].equalsIgnoreCase("w")) {
            senteTurn = false;
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
                if (p.isSentePiece()) {
                    senteKomadai.setPiecesOfType(p.getPieceType(), s);
                } else {
                    goteKomadai.setPiecesOfType(p.getPieceType(), s);
                }
            }
        }

        // If this is a tsume problem (no sente king) put remaining pieces in gote hand
        if (pieces.contains(PieceType.KING)) {
            for (PieceType type : PieceType.values()) {
                int pieceCount = Collections.frequency(pieces, type);
                goteKomadai.setPiecesOfType(type, pieceCount - senteKomadai.getPiecesOfType(type));
            }
        }

        return new ShogiPosition(senteTurn, shogiBoardState, senteKomadai, goteKomadai);
    }
}
