package com.playshogi.website.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.playshogi.website.gwt.client.widget.board.PieceGraphics;

public class UserPreferences {

    private static final String PIECE_STYLE_COOKIE = "pieceStyle";
    private static final String NOTATION_STYLE_COOKIE = "notationStyle";

    public enum NotationStyle {
        TRADITIONAL,
        NUMERICAL_JAPANESE,
        KK_NOTATION,
        WESTERN_NUMERICAL,
        WESTERN_ALPHABETICAL
    }

    private PieceGraphics.Style pieceStyle = PieceGraphics.Style.RYOKO;
    private NotationStyle notationStyle = NotationStyle.TRADITIONAL;

    public UserPreferences() {
        String pieceStyleCookie = Cookies.getCookie(PIECE_STYLE_COOKIE);
        if (pieceStyleCookie != null && ("RYOKO" .equals(pieceStyleCookie) || "HIDETCHI" .equals(pieceStyleCookie))) {
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

    @Override
    public String toString() {
        return "UserPreferences{" +
                "pieceStyle=" + pieceStyle +
                ", notationStyle=" + notationStyle +
                '}';
    }
}
