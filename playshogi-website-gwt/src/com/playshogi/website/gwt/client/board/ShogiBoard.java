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

public class ShogiBoard implements EntryPoint, ClickHandler {

	private static final int TATAMI_LEFT_MARGIN = 10;
	private static final int TATAMI_TOP_MARGIN = 10;
	private static final int TATAMI_INSIDE_MARGIN = 5;
	private static final int BOARD_LEFT_MARGIN = 11;
	private static final int BOARD_TOP_MARGIN = 11;
	private static final int SQUARE_WIDTH = 43;
	private static final int SQUARE_HEIGHT = 48;
	private static final int KOMADAI_INSIDE_MARGIN = 5;

	private ShogiPosition position;
	private boolean pieceSelected = false;
	private int selectionRow = 0;
	private int selectionColumn = 0;
	private Image[][] pieceImages;
	private Image ban;
	private AbsolutePanel absolutePanel;
	private Image grid;
	private int boardLeft;
	private int boardTop;

	@Override
	public void onModuleLoad() {

		BoardBundle boardResources = GWT.create(BoardBundle.class);

		absolutePanel = new AbsolutePanel();
		ban = new Image(boardResources.ban_kaya_a());
		grid = new Image(boardResources.masu_dot());
		Image tatami = new Image(boardResources.bg_tatami());
		Image goteKomadai = new Image(boardResources.ghand());
		Image senteKomadai = new Image(boardResources.shand());

		absolutePanel.setSize(tatami.getWidth() + "px", tatami.getHeight() + "px");
		absolutePanel.add(tatami, 0, 0);
		absolutePanel.add(goteKomadai, TATAMI_LEFT_MARGIN, TATAMI_TOP_MARGIN);

		boardLeft = TATAMI_LEFT_MARGIN + goteKomadai.getWidth() + TATAMI_INSIDE_MARGIN;
		boardTop = TATAMI_TOP_MARGIN;

		absolutePanel.add(ban, boardLeft, boardTop);
		absolutePanel.add(grid, boardLeft, boardTop);

		absolutePanel.add(senteKomadai, boardLeft + ban.getWidth() + TATAMI_INSIDE_MARGIN,
				TATAMI_TOP_MARGIN + ban.getHeight() - senteKomadai.getHeight());

		position = new ShogiInitialPositionFactory().createInitialPosition();

		displayPosition(absolutePanel, position);

		grid.addClickHandler(this);

		DecoratorPanel absolutePanelWrapper = new DecoratorPanel();
		absolutePanelWrapper.setWidget(absolutePanel);

		RootPanel.get().add(absolutePanelWrapper);
	}

	private void displayPosition(final AbsolutePanel absolutePanel, final ShogiPosition position) {
		int rows = position.getShogiBoardState().getHeight();
		int columns = position.getShogiBoardState().getWidth();

		pieceImages = new Image[rows][columns];

		// Put some values in the grid cells.
		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < columns; ++col) {
				Piece piece = position.getShogiBoardState().getPieceAt(new Square(((8 - col) + 1), row + 1));
				if (piece != null) {
					final Image image = new Image(PieceGraphics.getPieceImage(piece));

					final int imageRow = row;
					final int imageCol = col;
					pieceImages[imageRow][imageCol] = image;

					setupPieceClickHandler(image, imageRow, imageCol);
					image.setStyleName("gwt-piece-unselected");

					absolutePanel.add(image, getX(col), getY(row));
				}
			}
		}
	}

	private int getY(final int row) {
		return boardTop + BOARD_TOP_MARGIN + row * SQUARE_HEIGHT;
	}

	private int getX(final int col) {
		return boardLeft + BOARD_LEFT_MARGIN + col * SQUARE_WIDTH;
	}

	private int getColumn(final int x) {
		return (x - BOARD_LEFT_MARGIN) / SQUARE_WIDTH;
	}

	private int getRow(final int y) {
		return (y - BOARD_TOP_MARGIN) / SQUARE_HEIGHT;
	}

	private void setupPieceClickHandler(final Image image, final int imageRow, final int imageCol) {
		image.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				if (image.getStyleName().equals("gwt-piece-selected")) {
					image.setStyleName("gwt-piece-unselected");
					pieceSelected = false;
				} else {
					if (pieceSelected) {
						unselect();
					}
					selectionRow = imageRow;
					selectionColumn = imageCol;
					pieceSelected = true;
					image.setStyleName("gwt-piece-selected");
				}
			}

		});
	}

	private void unselect() {
		pieceImages[selectionRow][selectionColumn].setStyleName("gwt-piece-unselected");
		pieceSelected = false;
	}

	@Override
	public void onClick(final ClickEvent event) {

		if (event.getSource() == grid) {
			if (pieceSelected) {
				absolutePanel.add(pieceImages[selectionRow][selectionColumn], getX(getColumn(event.getX())),
						getY(getRow(event.getY())));
				unselect();
			}
		}
	}
}
