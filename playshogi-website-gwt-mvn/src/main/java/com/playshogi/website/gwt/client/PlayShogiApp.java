package com.playshogi.website.gwt.client;

import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PlayShogiApp {

    private final PlayShogiAppShell playShogiAppShell;
    private final PlaceHistoryHandler historyHandler;

    @Inject
    public PlayShogiApp(final PlayShogiAppShell playShogiAppShell, final PlaceHistoryHandler historyHandler) {
        this.playShogiAppShell = playShogiAppShell;
        this.historyHandler = historyHandler;
    }

    public void start() {
        RootLayoutPanel.get().add(playShogiAppShell);

        historyHandler.handleCurrentHistory();
    }
}
