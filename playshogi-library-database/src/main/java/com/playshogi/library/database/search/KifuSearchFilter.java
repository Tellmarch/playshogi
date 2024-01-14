package com.playshogi.library.database.search;

import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;

public class KifuSearchFilter {

    private final ReadOnlyShogiPosition partialPositionSearch;

    public KifuSearchFilter(final ReadOnlyShogiPosition partialPositionSearch) {
        this.partialPositionSearch = partialPositionSearch;
    }

    public ReadOnlyShogiPosition getPartialPositionSearch() {
        return partialPositionSearch;
    }
}
