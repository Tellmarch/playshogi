package com.playshogi.library.shogi.models;

import com.playshogi.library.models.games.Game;

public enum ShogiGame implements Game {
    SHOGI("Shogi"), MINISHOGI("MiniShogi");

    private ShogiGame(final String variationName) {
        this.variationName = variationName;
    }

    private final String variationName;

    @Override
    public String getVariationName() {
        return variationName;
    }
}
