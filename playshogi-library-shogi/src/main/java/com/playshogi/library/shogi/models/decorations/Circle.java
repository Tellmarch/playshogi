package com.playshogi.library.shogi.models.decorations;

import com.playshogi.library.shogi.models.position.Square;

public class Circle {
    private final Square square;
    private final Color color;

    public Circle(final Square square, final Color color) {
        this.square = square;
        this.color = color;
    }

    public Square getSquare() {
        return square;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "square=" + square +
                ", color=" + color +
                '}';
    }
}
