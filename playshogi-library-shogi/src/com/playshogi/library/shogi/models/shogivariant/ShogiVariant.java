package com.playshogi.library.shogi.models.shogivariant;

public enum ShogiVariant {
	NORMAL_SHOGI("Shogi", 9, 9, 3, true), MINI_SHOGI("Mini Shogi", 5, 5, 1, true);

	private final String variantName;
	private final int boardWidth;
	private final int boardHeight;
	private final int sentePromotionHeight;
	private final boolean dropsAllowed;

	private ShogiVariant(final String variantName, final int boardWidth, final int boardHeight,
			final int promotionHeight, final boolean dropsAllowed) {
		this.variantName = variantName;
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.sentePromotionHeight = promotionHeight;
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

	public int getSentePromotionHeight() {
		return sentePromotionHeight;
	}

	public int getGotePromotionHeight() {
		return boardHeight + 1 - sentePromotionHeight;
	}

	public boolean isDropsAllowed() {
		return dropsAllowed;
	}

}
