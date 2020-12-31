package com.playshogi.library.shogi;

import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

public class ShogiUtils {

    public static GameNavigation<ShogiPosition> getNavigation(GameRecord gameRecord) {
        return new GameNavigation<>(new ShogiRulesEngine(), gameRecord.getGameTree(),
                ShogiInitialPositionFactory.createInitialPosition());
    }
}
