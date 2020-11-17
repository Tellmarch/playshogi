package com.playshogi.website.gwt.client.ui;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;

@Singleton
public class PlayView extends Composite {

    private static final String PLAY = "play";
    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;

    @Inject
    public PlayView() {
        GWT.log("Creating Play view");
        shogiBoard = new ShogiBoard(PLAY);
        gameNavigator = new GameNavigator(PLAY);
        shogiBoard.setUpperRightPanel(null);

        initWidget(shogiBoard);
    }


    public void activate(final EventBus eventBus) {
        com.google.gwt.core.shared.GWT.log("Activating Play view");
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
    }

    public GameNavigator getGameNavigator() {
        return gameNavigator;
    }

    public ShogiPosition getPosition() {
        return gameNavigator.getGameNavigation().getPosition();
    }
}
