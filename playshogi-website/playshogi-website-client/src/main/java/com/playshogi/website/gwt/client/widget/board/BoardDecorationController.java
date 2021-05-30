package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.decorations.Arrow;
import com.playshogi.library.shogi.models.decorations.Color;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.Square;

import static com.playshogi.website.gwt.client.widget.board.BoardLayout.SQUARE_HEIGHT;
import static com.playshogi.website.gwt.client.widget.board.BoardLayout.SQUARE_WIDTH;

public class BoardDecorationController {
    private static final int ARROW_HEAD_LENGTH = 10;
    private static final int ARROW_WIDTH = 8;

    private final Canvas staticCanvas; // May be null if not supported by the browser
    private final Canvas highlightCanvas; // May be null if not supported by the browser
    private final ShogiBoard shogiBoard;
    private final BoardLayout layout;

    BoardDecorationController(final ShogiBoard shogiBoard, final BoardLayout layout) {
        this.shogiBoard = shogiBoard;
        this.layout = layout;
        staticCanvas = Canvas.createIfSupported();
        staticCanvas.addStyleName("board-canvas");
        layout.addCanvas(staticCanvas);
        highlightCanvas = Canvas.createIfSupported();
        highlightCanvas.addStyleName("board-highlight-canvas");
        layout.addCanvas(highlightCanvas);
    }

    void drawCircle(final Square square, final Color color) {
        if (staticCanvas == null) return;
        Context2d context2d = staticCanvas.getContext2d();
        context2d.setLineWidth(5);
        context2d.setStrokeStyle(color.toString());
        context2d.beginPath();
        context2d.arc(layout.getX(square) + (SQUARE_WIDTH >> 1), layout.getY(square) + (SQUARE_HEIGHT >> 1),
                (SQUARE_WIDTH >> 1) + 2, 0, 2 * Math.PI);
        context2d.stroke();
    }

    void drawArrow(final Arrow arrow) {
        if (arrow.getFrom() != null && arrow.getTo() != null) {
            drawArrow(arrow.getFrom(), arrow.getTo(), arrow.getColor());
        } else if (arrow.getFromKomadaiPiece() != null && arrow.getTo() != null) {
            drawDropArrow(arrow.getFromKomadaiPiece().getPieceType(), arrow.getFromKomadaiPiece().getOwner(),
                    arrow.getTo(), arrow.getColor());
        }
        //TODO other two cases
    }

    private void drawArrow(final Square fromSquare, final Square toSquare, final Color color) {
        drawArrow(staticCanvas,
                layout.getX(fromSquare) + SQUARE_WIDTH / 2,
                layout.getY(fromSquare) + SQUARE_HEIGHT / 2,
                layout.getX(toSquare) + SQUARE_WIDTH / 2,
                layout.getY(toSquare) + SQUARE_HEIGHT / 2,
                color);
    }

    private void drawArrow(final Canvas canvas, final Square fromSquare, final Square toSquare, final Color color) {
        drawArrow(canvas,
                layout.getX(fromSquare) + SQUARE_WIDTH / 2,
                layout.getY(fromSquare) + SQUARE_HEIGHT / 2,
                layout.getX(toSquare) + SQUARE_WIDTH / 2,
                layout.getY(toSquare) + SQUARE_HEIGHT / 2,
                color);
    }

    private void drawArrow(final Canvas canvas, final int fromX, final int fromY, final int toX, final int toY,
                           final Color color) {
        if (canvas == null) return;
        Context2d ctx = canvas.getContext2d();
        ctx.setStrokeStyle(color.toString());
        ctx.setLineWidth(ARROW_WIDTH);

        double angle = Math.atan2(toY - fromY, toX - fromX);

        double adjustedToX = toX - Math.cos(angle) * ((ARROW_WIDTH * 1.15));
        double adjustedToY = toY - Math.sin(angle) * ((ARROW_WIDTH * 1.15));

        ctx.beginPath();
        ctx.moveTo(fromX, fromY);
        ctx.lineTo(adjustedToX, adjustedToY);

        ctx.moveTo(adjustedToX, adjustedToY);
        ctx.lineTo(adjustedToX - ARROW_HEAD_LENGTH * Math.cos(angle - Math.PI / 7),
                adjustedToY - ARROW_HEAD_LENGTH * Math.sin(angle - Math.PI / 7));
        ctx.lineTo(adjustedToX - ARROW_HEAD_LENGTH * Math.cos(angle + Math.PI / 7),
                adjustedToY - ARROW_HEAD_LENGTH * Math.sin(angle + Math.PI / 7));
        ctx.lineTo(adjustedToX, adjustedToY);
        ctx.lineTo(adjustedToX - ARROW_HEAD_LENGTH * Math.cos(angle - Math.PI / 7),
                adjustedToY - ARROW_HEAD_LENGTH * Math.sin(angle - Math.PI / 7));

        ctx.stroke();
    }

    void highlightMove(final ShogiMove move) {
        clear(highlightCanvas);
        if (move instanceof NormalMove) {
            NormalMove normalMove = (NormalMove) move;
            drawArrow(highlightCanvas, normalMove.getFromSquare(), normalMove.getToSquare(), Color.RED);
        } else if (move instanceof DropMove) {
            DropMove dropMove = (DropMove) move;

            drawDropArrow(highlightCanvas, dropMove.getPieceType(), dropMove.getPlayer(), dropMove.getToSquare(),
                    Color.RED);
        }
    }

    private void drawDropArrow(final PieceType pieceType, final Player player, final Square toSquare,
                               final Color color) {
        drawDropArrow(staticCanvas, pieceType, player, toSquare, color);
    }

    private void drawDropArrow(final Canvas canvas, final PieceType pieceType, final Player player,
                               final Square toSquare, final Color color) {
        boolean lowerKomadai = shogiBoard.getBoardConfiguration().isInverted() ? player == Player.WHITE :
                player == Player.BLACK;
        KomadaiPositioning.Point[] points =
                KomadaiPositioning.getPiecesPositions(pieceType.ordinal(), 1, lowerKomadai,
                        layout.getKomadaiWidth());

        int komadaiX = lowerKomadai ? layout.getSenteKomadaiX() : layout.getGoteKomadaiX();
        int komadaiY = lowerKomadai ? layout.getSenteKomadaiY() : layout.getGoteKomadaiY();

        drawArrow(canvas, komadaiX + points[0].x + SQUARE_WIDTH / 2,
                komadaiY + points[0].y + SQUARE_HEIGHT / 2,
                layout.getX(toSquare) + SQUARE_WIDTH / 2,
                layout.getY(toSquare) + SQUARE_HEIGHT / 2,
                color);
    }

    private void clear(final Canvas canvas) {
        if (canvas == null) return;
        canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
    }

    void clear() {
        GWT.log("Decoration controller - clear");
        clear(staticCanvas);
        clear(highlightCanvas);
    }

}
