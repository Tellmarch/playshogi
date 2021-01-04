package com.playshogi.library.shogi.models.decorations;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.formats.usf.UsfUtil;
import com.playshogi.library.shogi.models.position.Square;

public class Arrow {

    private final Square from;
    private final Piece fromKomadaiPiece;
    private final Square to;
    private final Piece toKomadaiPiece;
    private final Color color;

    public Arrow(final Square from, final Square to, final Color color) {
        this(from, null, to, null, color);
    }

    public Arrow(final Piece fromKomadaiPiece, final Square to, final Color color) {
        this(null, fromKomadaiPiece, to, null, color);
    }

    public Arrow(final Square from, final Piece toKomadaiPiece, final Color color) {
        this(from, null, null, toKomadaiPiece, color);
    }

    public Arrow(final Square from, final Piece fromKomadaiPiece, final Square to, final Piece toKomadaiPiece,
                 final Color color) {
        this.from = from;
        this.fromKomadaiPiece = fromKomadaiPiece;
        this.to = to;
        this.toKomadaiPiece = toKomadaiPiece;
        this.color = color;
    }

    public Square getFrom() {
        return from;
    }

    public Square getTo() {
        return to;
    }

    public Color getColor() {
        return color;
    }

    public Piece getFromKomadaiPiece() {
        return fromKomadaiPiece;
    }

    public Piece getToKomadaiPiece() {
        return toKomadaiPiece;
    }

    @Override
    public String toString() {
        return "Arrow{" +
                "from=" + from +
                ", fromKomadaiPiece=" + fromKomadaiPiece +
                ", to=" + to +
                ", toKomadaiPiece=" + toKomadaiPiece +
                ", color=" + color +
                '}';
    }

    public String toUsfString() {
        if (from != null) {
            return "ARROW," + from + to + ",3,0,0," + color.toUsfString() + "," + color.toUsfString();
        } else {
            return "ARROW," + UsfUtil.pieceToString(fromKomadaiPiece) + "*" + to + ",3,0,0," + color.toUsfString() +
                    "," + color.toUsfString();
        }
    }

    public static Arrow parseArrowObject(final String object) {
        String coordinates = object.substring(6, 10);
        String[] color1 = object.substring(object.indexOf("(") + 1, object.indexOf(")")).split(",");

        Color c = new Color(Integer.parseInt(color1[0]), Integer.parseInt(color1[1]),
                Integer.parseInt(color1[2]), Integer.parseInt(color1[3]));

        Square to = Square.of(UsfUtil.char2ColumnNumber(coordinates.charAt(2)),
                UsfUtil.char2RowNumber(coordinates.charAt(3)));

        if (coordinates.charAt(1) == '*') {
            Piece fromPiece = UsfUtil.pieceFromChar(coordinates.charAt(0));

            return new Arrow(fromPiece, to, c);
        } else {
            Square from = Square.of(UsfUtil.char2ColumnNumber(coordinates.charAt(0)),
                    UsfUtil.char2RowNumber(coordinates.charAt(1)));

            return new Arrow(from, to, c);
        }

    }
}
