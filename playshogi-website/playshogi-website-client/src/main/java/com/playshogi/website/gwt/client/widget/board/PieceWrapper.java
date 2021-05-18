package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.user.client.ui.Image;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.position.Square;

public class PieceWrapper {

    static final int BLACK_KOMADAI_ROW = -1;
    static final int WHITE_KOMADAI_ROW = -2;

    private final Piece piece;
    private final Image image;
    // Coordinates from top left, starting at 0
    private final int row;
    private final int column;
    private final Square square;
    private final boolean inKomadai;

    PieceWrapper(final Piece piece, final Image image, final int row, final int column, final Square square,
                 final boolean inKomadai) {
        this.piece = piece;
        this.image = image;
        this.row = row;
        this.column = column;
        this.square = square;
        this.inKomadai = inKomadai;
    }

    public Piece getPiece() {
        return piece;
    }

    public Image getImage() {
        return image;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isInKomadai() {
        return inKomadai;
    }

    public Square getSquare() {
        return square;
    }

    @Override
    public String toString() {
        return "PieceWrapper{" +
                "piece=" + piece +
                ", row=" + row +
                ", column=" + column +
                ", inKomadai=" + inKomadai +
                '}';
    }
}