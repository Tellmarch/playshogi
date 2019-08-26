package com.playshogi.library.shogi.models.formats.sfen;

import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.models.record.GameTree;

public interface GameRecordFormat {

    GameRecord read(String usfString);

    GameRecord read(LineReader lineReader);

    String write(GameRecord gameRecord);

    String write(GameTree gameTree);

}
