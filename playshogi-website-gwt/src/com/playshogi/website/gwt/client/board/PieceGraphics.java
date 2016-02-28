package com.playshogi.website.gwt.client.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.website.gwt.client.RyokoPieceBundle;

public class PieceGraphics {

	private static RyokoPieceBundle resources = GWT.create(RyokoPieceBundle.class);

	public static Image getPieceImage(final Piece piece) {
		switch (piece) {

		default:
			return new Image(resources.sfu());

		}
	}
}
