package com.playshogi.library.shogi.models.formats.sfen;

public interface LineReader {

    boolean hasNextLine();

    /**
     * Reads the next line and advance the reader. May throw if at the end of the file.
     */
    String nextLine();

    /**
     * Reads the next line but does not advance the reader. May throw if at the end of the file.
     */
    String peekNextLine();

}
