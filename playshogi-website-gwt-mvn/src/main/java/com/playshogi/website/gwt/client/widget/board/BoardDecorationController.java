package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.playshogi.library.models.Square;

import static com.playshogi.website.gwt.client.widget.board.BoardLayout.SQUARE_HEIGHT;
import static com.playshogi.website.gwt.client.widget.board.BoardLayout.SQUARE_WIDTH;

class BoardDecorationController {
    private final ShogiBoard shogiBoard;
    private final Canvas canvas; // May be null if not supported by the browser
    private final BoardLayout layout;

    BoardDecorationController(final ShogiBoard shogiBoard, final Canvas canvas, final BoardLayout layout) {
        this.shogiBoard = shogiBoard;
        this.canvas = canvas;
        this.layout = layout;
    }

    void drawCircle(final Square square) {
        GWT.log("CANVAS" + canvas.getCoordinateSpaceWidth() + " " + canvas.getCoordinateSpaceHeight());
        Context2d context2d = canvas.getContext2d();
        context2d.setLineWidth(5);
        context2d.setStrokeStyle("#0000FF");
        context2d.beginPath();
        context2d.arc(layout.getX(square) + (SQUARE_WIDTH >> 1), layout.getY(square) + (SQUARE_HEIGHT >> 1),
                (SQUARE_WIDTH >> 1) + 2, 0, 2 * Math.PI);
        context2d.stroke();
    }
}
