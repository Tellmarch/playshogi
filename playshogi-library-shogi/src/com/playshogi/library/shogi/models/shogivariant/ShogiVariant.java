package com.playshogi.library.shogi.models.shogivariant;

public enum ShogiVariant {
	NORMAL_SHOGI("Shogi", (short) 9, (short) 9, true),;

	private final String variantName;
	private final short boardWidth;
	private final short boardHeight;
	private final boolean dropsAllowed;

	private ShogiVariant(final String variantName, final short boardWidth, final short boardHeight,
			final boolean dropsAllowed) {
		this.variantName = variantName;
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.dropsAllowed = dropsAllowed;
	}

	public String getVariantName() {
		return variantName;
	}

	public short getBoardWidth() {
		return boardWidth;
	}

	public short getBoardHeight() {
		return boardHeight;
	}

	public boolean isDropsAllowed() {
		return dropsAllowed;
	}

}
