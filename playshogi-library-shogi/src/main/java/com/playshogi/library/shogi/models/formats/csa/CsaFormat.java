package com.playshogi.library.shogi.models.formats.csa;

import com.playshogi.library.shogi.models.formats.sfen.GameRecordFormat;
import com.playshogi.library.shogi.models.formats.sfen.LineReader;
import com.playshogi.library.shogi.models.formats.sfen.StringLineReader;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.library.shogi.models.record.GameTree;

import java.util.ArrayList;
import java.util.List;

/**
 * See format specification at https://github.com/Marken-Foo/shogi-translations/blob/main/CSA-standard.md
 */
public enum CsaFormat implements GameRecordFormat {
    INSTANCE;

    @Override
    public List<GameRecord> read(final String string) {
        return read(new StringLineReader(string));
    }

    @Override
    public List<GameRecord> read(final LineReader lineReader) {
        ArrayList<GameRecord> games = new ArrayList<>(1);

        GameRecord gameRecord = new CsaGameParser(lineReader).readGameRecord();
        games.add(gameRecord);

        while (lineReader.hasNextLine() && "/".equals(lineReader.peekNextLine())) { // Games are separated by a /
            lineReader.nextLine();
            games.add(new CsaGameParser(lineReader).readGameRecord());
        }

        return games;
    }

    @Override
    public String write(final GameRecord gameRecord) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String write(final GameTree gameTree) {
        throw new UnsupportedOperationException();
    }
}
