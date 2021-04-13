package com.playshogi.library.shogi.engine;

import com.playshogi.library.shogi.models.formats.sfen.LineReader;

import java.io.Closeable;
import java.util.Scanner;

public class UsiLineReader implements LineReader, Closeable {

    private final Scanner scanner;

    public UsiLineReader(final Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    @Override
    public String nextLine() {
        return scanner.nextLine();
    }

    @Override
    public String peekNextLine() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        scanner.close();
    }
}
