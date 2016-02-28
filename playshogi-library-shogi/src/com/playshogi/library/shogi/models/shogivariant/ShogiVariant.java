package com.playshogi.library.shogi.models.shogivariant;

public enum ShogiVariant {
	NORMAL_SHOGI("Shogi", 9, 9, true),;

	private final String variantName;
	private final int boardWidth;
	private final int boardHeight;
	private final boolean dropsAllowed;

	private ShogiVariant(final String variantName, final int boardWidth, final int boardHeight,
			final boolean dropsAllowed) {
		this.variantName = variantName;
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.dropsAllowed = dropsAllowed;
	}

	public String getVariantName() {
		return variantName;
	}

	public int getBoardWidth() {
		return boardWidth;
	}

	public int getBoardHeight() {
		return boardHeight;
	}

	public boolean isDropsAllowed() {
		return dropsAllowed;
	}

}
