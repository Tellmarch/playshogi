package com.playshogi.website.gwt.client.widget.board;

public class BoardConfiguration {
	private boolean inverted = false;
	private boolean allowOnlyLegalMoves = true;
	private boolean showPossibleMovesOnPieceSelection = true;
	private boolean showPossibleMovesOnPieceMouseOver = true;
	private boolean alternateSenteAndGoteMoves = true;
	private boolean playSenteMoves = true;
	private boolean playGoteMoves = true;
	private boolean positionEditingMode = false;

	public BoardConfiguration() {
	}

	public boolean isInverted() {
		return inverted;
	}

	public void setInverted(final boolean inverted) {
		this.inverted = inverted;
	}

	public boolean isAllowOnlyLegalMoves() {
		return allowOnlyLegalMoves;
	}

	public void setAllowOnlyLegalMoves(final boolean allowOnlyLegalMoves) {
		this.allowOnlyLegalMoves = allowOnlyLegalMoves;
	}

	public boolean isShowPossibleMovesOnPieceSelection() {
		return showPossibleMovesOnPieceSelection;
	}

	public void setShowPossibleMovesOnPieceSelection(final boolean showPossibleMovesOnPieceSelection) {
		this.showPossibleMovesOnPieceSelection = showPossibleMovesOnPieceSelection;
	}

	public boolean isShowPossibleMovesOnPieceMouseOver() {
		return showPossibleMovesOnPieceMouseOver;
	}

	public void setShowPossibleMovesOnPieceMouseOver(final boolean showPossibleMovesOnPieceMouseOver) {
		this.showPossibleMovesOnPieceMouseOver = showPossibleMovesOnPieceMouseOver;
	}

	public boolean isAlternateSenteAndGoteMoves() {
		return alternateSenteAndGoteMoves;
	}

	public void setAlternateSenteAndGoteMoves(final boolean alternateSenteAndGoteMoves) {
		this.alternateSenteAndGoteMoves = alternateSenteAndGoteMoves;
	}

	public boolean isPlaySenteMoves() {
		return playSenteMoves;
	}

	public void setPlaySenteMoves(final boolean playSenteMoves) {
		this.playSenteMoves = playSenteMoves;
	}

	public boolean isPlayGoteMoves() {
		return playGoteMoves;
	}

	public void setPlayGoteMoves(final boolean playGoteMoves) {
		this.playGoteMoves = playGoteMoves;
	}

	public boolean isPositionEditingMode() {
		return positionEditingMode;
	}

	public void setPositionEditingMode(final boolean positionEditingMode) {
		this.positionEditingMode = positionEditingMode;
	}

	@Override
	public String toString() {
		return "BoardConfiguration [inverted=" + inverted + ", allowOnlyLegalMoves=" + allowOnlyLegalMoves
				+ ", showPossibleMovesOnPieceSelection=" + showPossibleMovesOnPieceSelection
				+ ", showPossibleMovesOnPieceMouseOver=" + showPossibleMovesOnPieceMouseOver
				+ ", alternateSenteAndGoteMoves=" + alternateSenteAndGoteMoves + ", playSenteMoves=" + playSenteMoves
				+ ", playGoteMoves=" + playGoteMoves + ", positionEditingMode=" + positionEditingMode + "]";
	}

}
