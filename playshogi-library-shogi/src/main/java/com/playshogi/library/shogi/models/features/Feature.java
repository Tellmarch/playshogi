package com.playshogi.library.shogi.models.features;

import com.playshogi.library.shogi.models.record.GameRecord;

public interface Feature {
    boolean hasFeature(GameRecord record);

    String getName();

    String getDescription();
}
