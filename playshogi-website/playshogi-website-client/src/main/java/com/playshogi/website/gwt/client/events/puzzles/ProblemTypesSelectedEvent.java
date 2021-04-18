package com.playshogi.website.gwt.client.events.puzzles;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class ProblemTypesSelectedEvent extends GenericEvent {

    private final boolean includeTsume;
    private final boolean includeTwoKings;
    private final boolean includeHisshi;
    private final boolean includeRealGame;

    public ProblemTypesSelectedEvent(final boolean includeTsume, final boolean includeTwoKings,
                                     final boolean includeHisshi, final boolean includeRealGame) {
        this.includeTsume = includeTsume;
        this.includeTwoKings = includeTwoKings;
        this.includeHisshi = includeHisshi;
        this.includeRealGame = includeRealGame;
    }

    public boolean isIncludeTsume() {
        return includeTsume;
    }

    public boolean isIncludeTwoKings() {
        return includeTwoKings;
    }

    public boolean isIncludeHisshi() {
        return includeHisshi;
    }

    public boolean isIncludeRealGame() {
        return includeRealGame;
    }

    @Override
    public String toString() {
        return "ProblemTypesSelectedEvent{" +
                "includeTsume=" + includeTsume +
                ", includeTwoKings=" + includeTwoKings +
                ", includeHisshi=" + includeHisshi +
                ", includeRealGame=" + includeRealGame +
                '}';
    }
}
