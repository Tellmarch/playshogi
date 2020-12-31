package com.playshogi.library.shogi.models.moves;

import com.playshogi.library.shogi.models.position.Position;

public class EditMove implements Move {
    private final Position position;

    public EditMove(final Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

}
