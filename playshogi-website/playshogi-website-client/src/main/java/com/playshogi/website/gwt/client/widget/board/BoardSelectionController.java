package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

import java.util.List;

public class BoardSelectionController {

    private static final String STYLE_PIECE_SELECTED = "gwt-piece-selected";
    private static final String STYLE_PIECE_UNSELECTED = "gwt-piece-unselected";
    private static final String STYLE_SQUARE_SELECTED = "gwt-square-selected";
    private static final String STYLE_SQUARE_PREVIOUS_MOVE = "gwt-square-previous-move";
    private static final String STYLE_SQUARE_UNSELECTED = "gwt-square-unselected";

    private final Image[][] squareImages;
    private final ShogiBoard shogiBoard;
    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
    private PieceWrapper selectedPiece = null;

    private boolean selectionLocked = false;
    private ShogiMove previousMove;

    BoardSelectionController(final Image[][] squareImages, final ShogiBoard shogiBoard) {
        this.squareImages = squareImages;
        this.shogiBoard = shogiBoard;
    }

    void selectPiece(final PieceWrapper pieceWrapper) {
        selectedPiece = pieceWrapper;
        pieceWrapper.getImage().setStyleName(STYLE_PIECE_SELECTED);
    }

    private void unselectPiece(final PieceWrapper pieceWrapper) {
        selectedPiece = null;
        pieceWrapper.getImage().setStyleName(STYLE_PIECE_UNSELECTED);
    }

    public void selectSquare(final Square square) {
        selectSquare(square, STYLE_SQUARE_SELECTED);
    }

    public void selectSquare(final Square square, final String selectionStyle) {
        if (selectionLocked) {
            return;
        }
        if (shogiBoard.getBoardConfiguration().isInverted()) {
            squareImages[9 - square.getRow()][square.getColumn() - 1].setStyleName(selectionStyle);
        } else {
            squareImages[square.getRow() - 1][9 - square.getColumn()].setStyleName(selectionStyle);
        }
    }

    private void unSelectSquare(final Image image) {
        if (selectionLocked) {
            return;
        }
        image.setStyleName(STYLE_SQUARE_UNSELECTED);
    }

    void unselectSquares() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                unSelectSquare(squareImages[i][j]);
            }
        }
        highlightPreviousMove();
    }

    void unselect() {
        GWT.log("BoardSelectionController unselect");
        unselectSquares();
        if (selectedPiece != null) {
            unselectPiece(selectedPiece);
        }
    }

    void selectPossibleMoves(final PieceWrapper pieceWrapper, final ReadOnlyShogiPosition position) {
        selectPossibleMoves(pieceWrapper.getSquare(), position);
    }

    public void selectPossibleMoves(final Square square, final ReadOnlyShogiPosition position) {
        List<Square> possibleTargets = shogiRulesEngine.getPossibleTargetSquares(position, square);
        for (Square targetSquare : possibleTargets) {
            selectSquare(targetSquare);
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

    public void setPreviousMove(final ShogiMove move) {
        previousMove = move;
    }

    private void highlightPreviousMove() {
        if (previousMove instanceof NormalMove) {
            NormalMove normalMove = (NormalMove) previousMove;

            selectSquare(normalMove.getFromSquare(), STYLE_SQUARE_PREVIOUS_MOVE);
            selectSquare(normalMove.getToSquare(), STYLE_SQUARE_PREVIOUS_MOVE);

        } else if (previousMove instanceof DropMove) {
            DropMove dropMove = (DropMove) previousMove;

            selectSquare(dropMove.getToSquare(), STYLE_SQUARE_PREVIOUS_MOVE);
        }
    }

    boolean hasPieceSelected() {
        return selectedPiece != null;
    }

    PieceWrapper getSelectedPieceWrapper() {
        return selectedPiece;
    }

    void setupMouseOverHandler(final PieceWrapper pieceWrapper) {
        pieceWrapper.getImage().addMouseOverHandler(event -> {
            if (!hasPieceSelected()) {
                if (!pieceWrapper.isInKomadai()) {
                    if (shogiBoard.getPosition().getPlayerToMove() == pieceWrapper.getPiece().getOwner()) {
                        selectPossibleMoves(pieceWrapper, shogiBoard.getPosition());
                        selectSquare(pieceWrapper.getSquare());
                    }
                }
            }
        });

        pieceWrapper.getImage().addMouseOutHandler(event -> {
            if (!hasPieceSelected()) {
                unselectSquares();
            }
        });
    }

    public void lockSelection() {
        selectionLocked = true;
    }

    public void unlockSelection() {
        selectionLocked = false;
    }

}
