package com.playshogi.website.gwt.client.widget.board;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;

import java.util.Arrays;

import static com.playshogi.website.gwt.client.widget.board.ShogiBoard.SQUARE_WIDTH;

public class Komadai {

    private static final int KOMADAI_TOP_MARGIN = 5;
    private final int[] pieces = new int[]{0, 0, 0, 0, 0, 0, 0, 0};

    /**
     * Offset, in squares, to display the pieces of the given type.
     */
    private static final int[] sdispx = new int[]{0, 2, 0, 2, 0, 2, 0, 0};
    private static final int[] sdispy = new int[]{3, 2, 2, 1, 1, 0, 0, 4};
    /**
     * Number of pieces that can be displayed without overlapping.
     */
    private static final int[] ndispx = new int[]{5, 2, 2, 2, 2, 2, 2, 2};

    public void removeAll() {
        Arrays.fill(pieces, 0);
    }

    public Point addPiece(final Piece piece) {

        // GWT.log("Adding piece: " + piece.toString());

        int pieceNum = piece.getPieceType().ordinal();
        int leftMargin = (piece.getPieceType() == PieceType.PAWN ? -1 : 0);
        int padding = (piece.getPieceType() == PieceType.PAWN ? SQUARE_WIDTH * 3 / 4 : SQUARE_WIDTH - 1);
        int x = (sdispx[pieceNum] + pieces[pieceNum]) * padding + leftMargin;
        int y =  sdispy[pieceNum] * ShogiBoard.SQUARE_HEIGHT + KOMADAI_TOP_MARGIN;

        pieces[pieceNum]++;

        // GWT.log("Added piece: " + piece.toString() + " at " + x + "," + y);

        return new Point(x, y);
    }

    public static class Point {
        public int x;
        public int y;

        public Point(final int x, final int y) {
            this.x = x;
            this.y = y;
        }
    }

}
