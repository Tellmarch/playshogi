package com.playshogi.website.gwt.client.board;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;

public class ShogiBoard implements EntryPoint {

	private static final int TATAMI_LEFT_MARGIN = 10;
	private static final int TATAMI_TOP_MARGIN = 10;
	private static final int TATAMI_INSIDE_MARGIN = 5;
	private static final int BOARD_LEFT_MARGIN = 11;
	private static final int BOARD_TOP_MARGIN = 11;
	private static final int SQUARE_WIDTH = 43;
	private static final int SQUARE_HEIGHT = 48;

	@Override
	public void onModuleLoad() {

		BoardBundle boardResources = GWT.create(BoardBundle.class);

		AbsolutePanel absolutePanel = new AbsolutePanel();
		Image ban = new Image(boardResources.ban_kaya_a());
		Image grid = new Image(boardResources.masu_dot());
		Image tatami = new Image(boardResources.bg_tatami());
		Image goteKomadai = new Image(boardResources.ghand());
		Image senteKomadai = new Image(boardResources.shand());

		absolutePanel.setSize(tatami.getWidth() + "px", tatami.getHeight() + "px");
		absolutePanel.add(tatami, 0, 0);
		absolutePanel.add(goteKomadai, TATAMI_LEFT_MARGIN, TATAMI_TOP_MARGIN);

		int boardLeft = TATAMI_LEFT_MARGIN + goteKomadai.getWidth() + TATAMI_INSIDE_MARGIN;
		int boardTop = TATAMI_TOP_MARGIN;

		absolutePanel.add(ban, boardLeft, boardTop);
		absolutePanel.add(grid, boardLeft, boardTop);

		absolutePanel.add(senteKomadai, boardLeft + ban.getWidth() + TATAMI_INSIDE_MARGIN,
				TATAMI_TOP_MARGIN + ban.getHeight() - senteKomadai.getHeight());

		ShogiPosition initialPosition = new ShogiInitialPositionFactory().createInitialPosition();

		int rows = initialPosition.getShogiBoardState().getHeight();
		int columns = initialPosition.getShogiBoardState().getWidth();
		// Grid g = new Grid(rows, columns);

		// Put some values in the grid cells.
		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < columns; ++col) {
				Piece piece = initialPosition.getShogiBoardState().getPieceAt(new Square((col + 1), row + 1));
				// Piece piece = Piece.GOTE_BISHOP;
				if (piece != null) {
					final Image image = new Image(PieceGraphics.getPieceImage(piece));

					image.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(final ClickEvent event) {
							if (image.getStyleName().equals("gwt-Green-Border")) {
								image.setStyleName("gwt-White-Border");
							} else {
								image.setStyleName("gwt-Green-Border");
							}
						}
					});
					image.setStyleName("gwt-White-Border");

					// g.setWidget(row, col, image);

					absolutePanel.add(image, boardLeft + BOARD_LEFT_MARGIN + col * SQUARE_WIDTH,
							boardTop + BOARD_TOP_MARGIN + row * SQUARE_HEIGHT);
				}
			}

		}

		// // You can use the CellFormatter to affect the layout of the grid's
		// // cells.
		// g.getCellFormatter().setWidth(0, 2, "256px");

		DecoratorPanel absolutePanelWrapper = new DecoratorPanel();
		absolutePanelWrapper.setWidget(absolutePanel);

		// RootPanel.get().add(g);

		RootPanel.get().add(absolutePanelWrapper);
	}

}
