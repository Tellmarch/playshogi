package com.playshogi.library.shogi.models.decorations;

import com.playshogi.library.shogi.models.position.Square;

public class Arrow {

    private final Square from;
    private final Square to;
    private final Color color;

    public Arrow(final Square from, final Square to, final Color color) {
        this.from = from;
        this.to = to;
        this.color = color;
    }

    public Square getFrom() {
        return from;
    }

    public Square getTo() {
        return to;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Arrow{" +
                "from=" + from +
                ", to=" + to +
                ", color=" + color +
                '}';
    }
}
