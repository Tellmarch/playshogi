package com.playshogi.library.shogi.models.formats.util;

public class DebugLineReader implements LineReader {

    private final LineReader delegate;

    public DebugLineReader(final LineReader delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNextLine() {
        System.out.println("hasNextLine: " + delegate.hasNextLine());
        return delegate.hasNextLine();
    }

    @Override
    public String nextLine() {
        String line = delegate.nextLine();
        System.out.println("nextLine: " + line);
        return line;
    }

    @Override
    public String peekNextLine() {
        String line = delegate.peekNextLine();
        System.out.println("peekNextLine: " + line);
        return line;
    }
}
