package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;

public class EditMove implements Move {
    private final ReadOnlyShogiPosition position;

    public EditMove(final ReadOnlyShogiPosition position) {
        this.position = position.clonePosition();
    }

    public ReadOnlyShogiPosition getPosition() {
        return position;
    }

}
