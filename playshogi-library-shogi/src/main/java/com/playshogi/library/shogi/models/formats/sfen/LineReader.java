package com.playshogi.library.shogi.models.formats.sfen;

public interface LineReader {

    boolean hasNextLine();

    String nextLine();

    String peekNextLine();

}
