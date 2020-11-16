package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.user.client.ui.Image;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.List;

public class BoardSelectionController {

    private static final String STYLE_PIECE_SELECTED = "gwt-piece-selected";
    private static final String STYLE_PIECE_UNSELECTED = "gwt-piece-unselected";
    private static final String STYLE_SQUARE_SELECTED = "gwt-square-selected";
    private static final String STYLE_SQUARE_UNSELECTED = "gwt-square-unselected";

    private final Image[][] squareImages;
    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
    private PieceWrapper selectedPiece = null;

    public BoardSelectionController(final Image[][] squareImages) {
        this.squareImages = squareImages;
    }

    void selectPiece(final PieceWrapper pieceWrapper) {
        selectedPiece = pieceWrapper;
        selectPiece(pieceWrapper.getImage());
    }

    void selectPiece(final Image image) {
        image.setStyleName(STYLE_PIECE_SELECTED);
    }

    public void unselectPiece(final PieceWrapper pieceWrapper) {
        selectedPiece = null;
        unselectPiece(pieceWrapper.getImage());
    }

    public void unselectPiece(final Image image) {
        image.setStyleName(STYLE_PIECE_UNSELECTED);
    }

    public void selectSquare(final Square square) {
        squareImages[square.getRow() - 1][8 - (square.getColumn() - 1)].setStyleName(STYLE_SQUARE_SELECTED);
    }

    void unSelectSquare(final Image image) {
        image.setStyleName(STYLE_SQUARE_UNSELECTED);
    }

    void unselectSquares() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                unSelectSquare(squareImages[i][j]);
            }
        }
    }

    void unselect() {
        unselectSquares();
        if (selectedPiece != null) {
            unSelectSquare(selectedPiece.getImage());
            selectedPiece = null;
        }
    }

    void selectPossibleMoves(final PieceWrapper pieceWrapper, final ShogiPosition position) {
        List<Square> possibleTargets = shogiRulesEngine.getPossibleTargetSquares(position, pieceWrapper.getSquare());
        for (Square square : possibleTargets) {
            selectSquare(square);
        }
    }

    void highlightMove(final ShogiMove move) {
        unselectSquares();
        if (move instanceof NormalMove) {
            NormalMove normalMove = (NormalMove) move;

            selectSquare(normalMove.getFromSquare());
            selectSquare(normalMove.getToSquare());

        } else if (move instanceof DropMove) {
            DropMove dropMove = (DropMove) move;

            selectSquare(dropMove.getToSquare());
        }
    }

    boolean hasPieceSelected() {
        return selectedPiece != null;
    }

    PieceWrapper getSelectedPieceWrapper() {
        return selectedPiece;
    }

    Piece getSelectedPiece() {
        return selectedPiece.getPiece();
    }


}
