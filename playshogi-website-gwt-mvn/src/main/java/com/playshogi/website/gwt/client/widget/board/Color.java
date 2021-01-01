package com.playshogi.website.gwt.client.widget.board;

public class Color {

    public final static Color RED = new Color(204, 0, 0, 255);

    private final int r;
    private final int g;
    private final int b;
    private final int a;

    public Color(final int r, final int g, final int b, final int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public int getA() {
        return a;
    }

    @Override
    public String toString() {
        return "#" + pad(Integer.toHexString(r))
                + pad(Integer.toHexString(g))
                + pad(Integer.toHexString(b));
    }

    private String pad(String s) {
        if (s.length() == 1) {
            return "0" + s;
        }
        return s;
    }
}
