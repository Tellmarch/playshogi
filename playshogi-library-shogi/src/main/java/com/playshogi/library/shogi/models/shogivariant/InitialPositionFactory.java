package com.playshogi.library.shogi.models.shogivariant;

import com.playshogi.library.shogi.models.position.ShogiPosition;

public interface InitialPositionFactory {

    ShogiPosition createInitialPosition(Handicap handicap);

}
