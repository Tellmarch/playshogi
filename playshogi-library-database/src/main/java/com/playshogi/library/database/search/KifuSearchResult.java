package com.playshogi.library.database.search;

import com.playshogi.library.database.models.PersistentKifu;
import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;

public class KifuSearchResult {
    private final PersistentKifu kifu;
    private final ReadOnlyShogiPosition position;

    public KifuSearchResult(final PersistentKifu kifu, final ReadOnlyShogiPosition position) {
        this.kifu = kifu;
        this.position = position;
    }

    public PersistentKifu getKifu() {
        return kifu;
    }

    public ReadOnlyShogiPosition getPosition() {
        return position;
    }
}
