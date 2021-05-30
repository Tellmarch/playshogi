package com.playshogi.website.gwt.client.widget.board;

import static com.playshogi.website.gwt.client.widget.board.BoardLayout.SQUARE_HEIGHT;
import static com.playshogi.website.gwt.client.widget.board.BoardLayout.SQUARE_WIDTH;

class KomadaiPositioning {

    private static final int KOMADAI_INSIDE_MARGIN = 5;

    /**
     * Offset, in squares, to display the pieces of the given type.
     */
    private static final int[] sdispx = new int[]{0, 2, 0, 2, 0, 2, 0, 0};
    private static final int[] gdispx = new int[]{0, 0, 2, 0, 2, 0, 2, 0};
    private static final int[] sdispy = new int[]{3, 2, 2, 1, 1, 0, 0, 4};
    private static final int[] gdispy = new int[]{0, 1, 1, 2, 2, 3, 3, 4};

    /**
     * For a given number of pieces of one type (ordinal in PieceType), returns where to display them
     */
    static Point[] getPiecesPositions(final int pieceOrdinal, final int numberOfPieces, boolean lowerKomadai,
                                      int komadaiWidth) {

        // Pawns occupy a full row, other pieces half of a row
        int availableSpace = pieceOrdinal == 0 ? komadaiWidth : komadaiWidth / 2;
        int horizontalSpacing = (availableSpace - SQUARE_WIDTH) / (numberOfPieces - 1);

        int x = (lowerKomadai ? sdispx[pieceOrdinal] : gdispx[pieceOrdinal]) * SQUARE_WIDTH;
        int y = (lowerKomadai ? sdispy[pieceOrdinal] : gdispy[pieceOrdinal]) * SQUARE_HEIGHT + KOMADAI_INSIDE_MARGIN;

        Point[] result = new Point[numberOfPieces];
        for (int i = 0; i < numberOfPieces; i++) {
            result[i] = new Point(x, y);
            x += horizontalSpacing;
        }

        return result;
    }

    static class Point {
        int x;
        int y;

        Point(final int x, final int y) {
            this.x = x;
            this.y = y;
        }
    }

}
