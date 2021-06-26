package com.playshogi.website.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.kif.KifMoveConverter;
import com.playshogi.library.shogi.models.formats.notations.MoveConverter;
import com.playshogi.library.shogi.models.formats.psn.PsnMoveConverter;
import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.website.gwt.client.widget.board.PieceGraphics;

public class UserPreferences {

    private static final String PIECE_STYLE_COOKIE = "pieceStyle";
    private static final String NOTATION_STYLE_COOKIE = "notationStyle";
    private static final String ANNOTATIONS_COOKIE = "annotations";

    public enum NotationStyle {
        TRADITIONAL,
        NUMERICAL_JAPANESE,
        KK_NOTATION,
        WESTERN_NUMERICAL,
        WESTERN_ALPHABETICAL
    }

    private PieceGraphics.Style pieceStyle = PieceGraphics.Style.RYOKO;
    private NotationStyle notationStyle = NotationStyle.TRADITIONAL;
    private boolean annotateGraphs = false;

    public UserPreferences() {
        String pieceStyleCookie = Cookies.getCookie(PIECE_STYLE_COOKIE);
        if (("RYOKO".equals(pieceStyleCookie) || "HIDETCHI".equals(pieceStyleCookie))) {
            pieceStyle = PieceGraphics.Style.valueOf(pieceStyleCookie);
        }
        String notationStyleCookie = Cookies.getCookie(NOTATION_STYLE_COOKIE);
        if (notationStyleCookie != null) {
            try {
                notationStyle = NotationStyle.valueOf(notationStyleCookie);
            } catch (Exception e) {
                GWT.log("Unrecognized notation: " + notationStyleCookie);
                Cookies.removeCookie(NOTATION_STYLE_COOKIE);
            }
        }
        String annotationsCookie = Cookies.getCookie(ANNOTATIONS_COOKIE);
        if (("true".equals(annotationsCookie))) {
            annotateGraphs = true;
        }
    }


    public PieceGraphics.Style getPieceStyle() {
        return pieceStyle;
    }

    public void setPieceStyle(final PieceGraphics.Style pieceStyle) {
        this.pieceStyle = pieceStyle;
        Cookies.setCookie(PIECE_STYLE_COOKIE, pieceStyle.name());
    }

    public NotationStyle getNotationStyle() {
        return notationStyle;
    }

    public void setNotationStyle(final NotationStyle notationStyle) {
        this.notationStyle = notationStyle;
        Cookies.setCookie(NOTATION_STYLE_COOKIE, notationStyle.name());
    }

    public String getMoveNotationAccordingToPreferences(final Move move, final Move previousMove,
                                                        final boolean withColorSymbol) {
        if (move instanceof ShogiMove) {
            if (previousMove instanceof ShogiMove) {
                return getMoveNotationAccordingToPreferences((ShogiMove) move, (ShogiMove) previousMove,
                        withColorSymbol);
            } else {
                return getMoveNotationAccordingToPreferences((ShogiMove) move, withColorSymbol);
            }
        } else if (move instanceof EditMove) {
            return "EDIT";
        } else {
            throw new IllegalStateException("Unknown move type");
        }
    }

    public String getMoveNotationAccordingToPreferences(final ShogiMove move, final boolean withColorSymbol) {
        return getMoveNotationAccordingToPreferences(move, null, withColorSymbol);
    }

    public String getMoveNotationAccordingToPreferences(final ShogiMove move, final ShogiMove previousMove,
                                                        final boolean withColorSymbol) {
        switch (notationStyle) {
            case TRADITIONAL:
                return getPlayerSymbol(move, withColorSymbol) + KifMoveConverter.toKifStringShort(move, previousMove);
            case WESTERN_ALPHABETICAL:
                return getPlayerSymbol(move, withColorSymbol) + PsnMoveConverter.toPsnStringShort(move, previousMove);
            case WESTERN_NUMERICAL:
                return getPlayerSymbol(move, withColorSymbol) + MoveConverter.toNumericalWestern(move, previousMove);
            case KK_NOTATION:
            case NUMERICAL_JAPANESE:
            default:
                throw new IllegalStateException("Unexpected notation style: " + notationStyle);
        }
    }

    public String getPlayerSymbol(final ShogiMove move, final boolean withColorSymbol) {
        if (!withColorSymbol) return "";
        return move.getPlayer() == Player.BLACK ? "☗ " : "☖ ";
    }

    public boolean isAnnotateGraphs() {
        return annotateGraphs;
    }

    @Override
    public String toString() {
        return "UserPreferences{" +
                "pieceStyle=" + pieceStyle +
                ", notationStyle=" + notationStyle +
                ", annotateGraphs=" + annotateGraphs +
                '}';
    }
}
