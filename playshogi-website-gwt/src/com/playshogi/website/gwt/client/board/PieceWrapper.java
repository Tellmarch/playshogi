package com.playshogi.website.gwt.client.board;

import com.google.gwt.user.client.ui.Image;
import com.playshogi.library.shogi.models.Piece;

public class PieceWrapper {
	private Piece piece;
	private Image image;
	private int row;
	private int column;
	private boolean inKomadai = false;

	public PieceWrapper(final Piece piece, final Image image, final int row, final int column) {
		this.piece = piece;
		this.image = image;
		this.row = row;
		this.column = column;
	}

	public Piece getPiece() {
		return piece;
	}

	public void setPiece(final Piece piece) {
		this.piece = piece;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(final Image image) {
		this.image = image;
	}

	public int getRow() {
		return row;
	}

	public void setRow(final int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(final int column) {
		this.column = column;
	}

	public boolean isInKomadai() {
		return inKomadai;
	}

	public void setInKomadai(final boolean inKomadai) {
		this.inKomadai = inKomadai;
	}

}