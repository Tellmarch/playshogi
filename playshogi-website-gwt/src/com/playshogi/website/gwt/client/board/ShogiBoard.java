package com.playshogi.website.gwt.client.board;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.playshogi.library.shogi.models.Piece;

public class ShogiBoard implements EntryPoint {

	@Override
	public void onModuleLoad() {
		// Grids must be sized explicitly, though they can be resized later.
		Grid g = new Grid(5, 5);

		// Put some values in the grid cells.
		for (int row = 0; row < 5; ++row) {
			for (int col = 0; col < 5; ++col) {
				final Image image = PieceGraphics.getPieceImage(Piece.GOTE_BISHOP);

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

				g.setWidget(row, col, image);
				// g.setText(row, col, "" + row + ", " + col);
			}
		}

		// // You can use the CellFormatter to affect the layout of the grid's
		// // cells.
		// g.getCellFormatter().setWidth(0, 2, "256px");

		RootPanel.get().add(g);

	}

}
