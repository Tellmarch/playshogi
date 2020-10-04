package com.playshogi.library.shogi.models.formats.sfen;

public class StringLineReader implements LineReader {

    private final String[] lines;
    private int position = 0;

    public StringLineReader(final String input) {
        lines = input.split("\n");
    }

    @Override
    public boolean hasNextLine() {
        return position < lines.length;
    }

    @Override
    public String nextLine() {
        return lines[position++];
    }

    @Override
    public String peekNextLine() {
        return lines[position];
    }
}
