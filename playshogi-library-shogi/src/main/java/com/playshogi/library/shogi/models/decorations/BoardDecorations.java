package com.playshogi.library.shogi.models.decorations;

import java.util.ArrayList;
import java.util.List;

public class BoardDecorations {
    private List<Arrow> arrows;
    private List<Circle> circles;

    public BoardDecorations() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public BoardDecorations(final List<Arrow> arrows, final List<Circle> circles) {
        this.arrows = arrows;
        this.circles = circles;
    }

    public List<Arrow> getArrows() {
        return arrows;
    }

    public List<Circle> getCircles() {
        return circles;
    }

    @Override
    public String toString() {
        return "BoardDecorations{" +
                "arrows=" + arrows +
                ", circles=" + circles +
                '}';
    }
}
