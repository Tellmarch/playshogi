package com.playshogi.library.shogi.models.formats.sfen;

import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.models.record.GameTree;

public interface GameRecordFormat {

    default GameRecord read(String string) {
        return read(new StringLineReader(string));
    }

    GameRecord read(LineReader lineReader);

    String write(GameRecord gameRecord);

    String write(GameTree gameTree);

}
