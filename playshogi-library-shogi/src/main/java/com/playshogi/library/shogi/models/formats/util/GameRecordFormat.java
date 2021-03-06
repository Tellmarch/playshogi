package com.playshogi.library.shogi.models.formats.util;

import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.library.shogi.models.record.GameTree;

import java.util.List;

public interface GameRecordFormat {

    List<GameRecord> read(String string);

    List<GameRecord> read(LineReader lineReader);

    String write(GameRecord gameRecord);

    String write(GameTree gameTree);

}
