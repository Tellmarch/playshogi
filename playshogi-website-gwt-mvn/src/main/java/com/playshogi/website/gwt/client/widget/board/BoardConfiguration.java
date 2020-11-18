package com.playshogi.website.gwt.client.widget.board;

public class BoardConfiguration {
    private boolean inverted = false;
    private boolean allowIllegalMoves = false;
    private boolean showPossibleMovesOnPieceSelection = true;
    private boolean showPossibleMovesOnPieceMouseOver = true;
    private boolean alternateSenteAndGoteMoves = true;
    private boolean playSenteMoves = true;
    private boolean playGoteMoves = true;
    private boolean positionEditingMode = false;
    private boolean showGoteKomadai = true;
    private boolean showSenteKomadai = true;
    private boolean allowPromotion = true;
    private boolean fillGoteKomadaiWithMissingPieces = false;

    public BoardConfiguration() {
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(final boolean inverted) {
        this.inverted = inverted;
    }

    public boolean allowIllegalMoves() {
        return allowIllegalMoves;
    }

    public void setAllowIllegalMoves(final boolean allowIllegalMoves) {
        this.allowIllegalMoves = allowIllegalMoves;
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

    public boolean isShowGoteKomadai() {
        return showGoteKomadai;
    }

    public void setShowGoteKomadai(final boolean showGoteKomadai) {
        this.showGoteKomadai = showGoteKomadai;
    }

    public boolean isShowSenteKomadai() {
        return showSenteKomadai;
    }

    public void setShowSenteKomadai(final boolean showSenteKomadai) {
        this.showSenteKomadai = showSenteKomadai;
    }

    public boolean isAllowPromotion() {
        return allowPromotion;
    }

    public void setAllowPromotion(boolean allowPromotion) {
        this.allowPromotion = allowPromotion;
    }

    public boolean isFillGoteKomadaiWithMissingPieces() {
        return fillGoteKomadaiWithMissingPieces;
    }

    public void setFillGoteKomadaiWithMissingPieces(final boolean fillGoteKomadaiWithMissingPieces) {
        this.fillGoteKomadaiWithMissingPieces = fillGoteKomadaiWithMissingPieces;
    }

    @Override
    public String toString() {
        return "BoardConfiguration{" +
                "inverted=" + inverted +
                ", allowIllegalMoves=" + allowIllegalMoves +
                ", showPossibleMovesOnPieceSelection=" + showPossibleMovesOnPieceSelection +
                ", showPossibleMovesOnPieceMouseOver=" + showPossibleMovesOnPieceMouseOver +
                ", alternateSenteAndGoteMoves=" + alternateSenteAndGoteMoves +
                ", playSenteMoves=" + playSenteMoves +
                ", playGoteMoves=" + playGoteMoves +
                ", positionEditingMode=" + positionEditingMode +
                ", showGoteKomadai=" + showGoteKomadai +
                ", showSenteKomadai=" + showSenteKomadai +
                ", allowPromotion=" + allowPromotion +
                ", fillGoteKomadaiWithMissingPieces=" + fillGoteKomadaiWithMissingPieces +
                '}';
    }
}
