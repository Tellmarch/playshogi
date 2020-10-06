package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.user.client.ui.Image;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;

import java.util.Optional;

public class PieceWrapper {
    private static final String STYLE_SELECTED = "gwt-piece-selected";
    private static final String STYLE_UNSELECTED = "gwt-piece-unselected";

    private Piece piece;
    private final Image image;
    private final int row;
    private final int column;
    private boolean inKomadai;

    public PieceWrapper(final Piece piece, final Image image, final int row, final int column) {
        this(piece, image, row, column, false);
    }

    public PieceWrapper(final Piece piece, final Image image, final int row, final int column, final boolean inKomadai) {
        this.piece = piece;
        this.image = image;
        this.row = row;
        this.column = column;
        this.inKomadai = inKomadai;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(final Piece piece) {
        this.piece = piece;
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

    @Deprecated // prevents this.inKomadai from being immutable
    public void setInKomadai(final boolean inKomadai) {
        this.inKomadai = inKomadai;
    }

    public Optional<Square> getSquare() {
        if (inKomadai)
            return Optional.empty();
        else
            return Optional.of(Square.of(((8 - column) + 1), row + 1));
    }

    /**
     * Selects the piece, even if not previously unselected
     */
    public void select() {
        image.setStyleName(STYLE_SELECTED);
    }

    /**
     * Unselects the piece, even if not previously selected
     */
    public void unselect() {
        image.setStyleName(STYLE_UNSELECTED);
    }
}