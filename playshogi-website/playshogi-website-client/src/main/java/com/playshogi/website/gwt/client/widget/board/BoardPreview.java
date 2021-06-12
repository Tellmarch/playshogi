package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.website.gwt.client.UserPreferences;
import elemental2.dom.Element;
import jsinterop.base.Js;

import static com.playshogi.website.gwt.client.widget.board.BoardLayout.SQUARE_HEIGHT;
import static com.playshogi.website.gwt.client.widget.board.BoardLayout.SQUARE_WIDTH;

public class BoardPreview extends Composite {

    private final AbsolutePanel absolutePanel;
    private final boolean inverted;
    private final UserPreferences userPreferences;
    private final Image grid;
    private final Image ban;

    public BoardPreview(final ShogiPosition position, final boolean inverted, final UserPreferences userPreferences) {
        this(position, inverted, userPreferences, 1.0);
    }

    public BoardPreview(final ShogiPosition position, final boolean inverted, final UserPreferences userPreferences,
                        final double scale) {
        this.inverted = inverted;
        this.userPreferences = userPreferences;
        absolutePanel = new AbsolutePanel();
        int width = 9 * SQUARE_WIDTH + 2;
        int height = 9 * SQUARE_HEIGHT + 2;
        absolutePanel.setSize(width + "px", height + "px");
        grid = new Image(ShogiBoard.BOARD_RESOURCES.masu_dot());
        ban = new Image(ShogiBoard.BOARD_RESOURCES.ban_kaya_a());
        showPosition(position);
        if (scale != 1.0) {
            AbsolutePanel wrapperPanel = new AbsolutePanel();
            absolutePanel.getElement().getStyle().setProperty("transform", "scale(" + scale + ")");
            wrapperPanel.add(absolutePanel, (int) (-width * (1 - scale) / 2), (int) (-height * (1 - scale) / 2));
            wrapperPanel.setSize(width * scale + "px", height * scale + "px");
            initWidget(wrapperPanel);
        } else {
            initWidget(absolutePanel);
        }
    }

    public Element asElement() {
        return Js.uncheckedCast(getElement());
    }

    public void showPosition(final ShogiPosition position) {
        GWT.log("wd count: " + absolutePanel.getWidgetCount());
        while (absolutePanel.getWidgetCount() > 0) {
            absolutePanel.remove(0);
        }
        absolutePanel.add(ban, 0, 0);
        absolutePanel.add(grid, -10, -10);
        GWT.log("wd count: " + absolutePanel.getWidgetCount());

        for (int row = 0, rows = position.getRows(); row < rows; ++row) {
            for (int col = 0, columns = position.getColumns(); col < columns; ++col) {
                Piece piece = position.getShogiBoardState().getPieceAt(getSquare(row, col)).orElse(null);
                if (piece != null) {
                    final Image image = new Image(PieceGraphics.getPieceImage(piece, userPreferences.getPieceStyle(),
                            inverted));
                    absolutePanel.add(image, getX(col), getY(row));
                }
            }
        }
    }

    int getY(final int row) {
        return row * SQUARE_HEIGHT;
    }

    int getX(final int col) {
        return col * SQUARE_WIDTH;
    }

    private Square getSquare(final int row, final int col) {
        if (inverted) {
            return Square.of(col + 1, 9 - row);
        } else {
            return Square.of(9 - col, row + 1);
        }
    }
}
