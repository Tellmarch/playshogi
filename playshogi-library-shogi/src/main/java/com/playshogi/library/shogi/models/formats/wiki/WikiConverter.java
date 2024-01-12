package com.playshogi.library.shogi.models.formats.wiki;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.kif.KifUtils;
import com.playshogi.library.shogi.models.position.KomadaiState;
import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;
import com.playshogi.library.shogi.models.position.ShogiBoardState;

import java.util.Optional;

import static com.playshogi.library.shogi.models.position.ShogiBoardState.FIRST_COLUMN;
import static com.playshogi.library.shogi.models.position.ShogiBoardState.FIRST_ROW;

public class WikiConverter {
    public static String toWikiDiagram(final ReadOnlyShogiPosition position, String title, String comment) {

        if (title.isEmpty()) {
            title = "Put title here!!";
        }
        if (comment.isEmpty()) {
            comment = " Insert Comment Here!";
        }


        String resultDiagram = "{{shogi diagram \n" +
                "|  \n" +
                "| '''" + title + "'''\n" +
                "| " + getPiecesInHandInWikiFormat(position, Player.WHITE) + "\n" +
                getBoardStateInWikiFormat(position) +
                "| " + getPiecesInHandInWikiFormat(position, Player.BLACK) + "\n" +
                "|" + comment + "\n" +
                "}}";

        getBoardStateInWikiFormat(position);


        return resultDiagram;
    }

    private static String getPiecesInHandInWikiFormat(final ReadOnlyShogiPosition position, Player player) {
        KomadaiState komadai = player == Player.BLACK ? position.getSenteKomadai() : position.getGoteKomadai();
        String result = "–";
        if (!komadai.isEmpty()) {
            result = "";
            for (PieceType piece : PieceType.STRONGEST_TO_WEAKEST) {
                int pieceCount = komadai.getPiecesOfType(piece);
                if (pieceCount > 0) result += KifUtils.getJapanesePieceSymbol(piece) + "<sub>" + pieceCount + "</sub> ";
            }
        }
        return result.trim();
    }

    private static String getBoardStateInWikiFormat(final ReadOnlyShogiPosition position) {
        StringBuilder builder = new StringBuilder(600);
        ShogiBoardState shogiBoardState = position.getShogiBoardState();
        for (int row = FIRST_ROW; row <= shogiBoardState.getLastRow(); row++) {
            for (int column = FIRST_COLUMN; column <= shogiBoardState.getLastColumn(); column++) {
                builder.append('|');
                Optional<Piece> pieceAt = shogiBoardState.getPieceAt(10 - column, row);
                if (pieceAt.isPresent()) {
                    String pieceToString = " " + pieceToString(pieceAt.get());
                    // characters piece like pss
                    builder.append(pieceToString);
                    if (pieceToString.length() == 3) {
                        builder.append(' ');
                    }
                } else {
                    if (shogiBoardState.getLastColumn() != column) {
                        builder.append("    ");
                    }
                }
            }
            builder.append('\n');
        }

        return builder.toString();
    }

    public static String pieceToString(final Piece x) {
        switch (x) {
            case GOTE_BISHOP:
                return "bg";
            case GOTE_GOLD:
                return "gg";
            case GOTE_KING:
                return "kg";
            case GOTE_KNIGHT:
                return "ng";
            case GOTE_LANCE:
                return "lg";
            case GOTE_PAWN:
                return "pg";
            case GOTE_PROMOTED_BISHOP:
                return "hg";
            case GOTE_PROMOTED_KNIGHT:
                return "png";
            case GOTE_PROMOTED_LANCE:
                return "plg";
            case GOTE_PROMOTED_PAWN:
                return "tg";
            case GOTE_PROMOTED_ROOK:
                return "dg";
            case GOTE_PROMOTED_SILVER:
                return "psg";
            case GOTE_ROOK:
                return "rg";
            case GOTE_SILVER:
                return "sg";
            case SENTE_BISHOP:
                return "bs";
            case SENTE_GOLD:
                return "gs";
            case SENTE_KING:
                return "ks";
            case SENTE_KNIGHT:
                return "ns";
            case SENTE_LANCE:
                return "ls";
            case SENTE_PAWN:
                return "ps";
            case SENTE_PROMOTED_BISHOP:
                return "hs";
            case SENTE_PROMOTED_KNIGHT:
                return "pns";
            case SENTE_PROMOTED_LANCE:
                return "pls";
            case SENTE_PROMOTED_PAWN:
                return "ts";
            case SENTE_PROMOTED_ROOK:
                return "ds";
            case SENTE_PROMOTED_SILVER:
                return "pss";
            case SENTE_ROOK:
                return "rs";
            case SENTE_SILVER:
                return "ss";
            default:
                return "";

        }
    }
}
