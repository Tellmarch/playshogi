package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.controller.NavigationController;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.ByoYomiPlace;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigatorPanel;
import com.playshogi.website.gwt.client.widget.gamenavigator.NavigatorConfiguration;
import com.playshogi.website.gwt.client.widget.problems.ByoYomiFeedbackPanel;
import com.playshogi.website.gwt.client.widget.problems.ByoYomiProgressPanel;

@Singleton
public class ByoYomiView extends Composite {

    private static final String TSUME = "byoyomi";

    private static final int[] MOVES = {3, 5, 7, 9, 11, 13};

    private final ShogiBoard shogiBoard;
    private final GameNavigatorPanel gameNavigatorPanel;
    private final ByoYomiFeedbackPanel byoYomiFeedbackPanel;
    private final ByoYomiProgressPanel byoYomiProgressPanel;
    private final NavigationController navigationController;

    @Inject
    public ByoYomiView(final AppPlaceHistoryMapper historyMapper, final SessionInformation sessionInformation) {
        GWT.log("Creating byo yomi view");
        shogiBoard = new ShogiBoard(TSUME, sessionInformation.getUserPreferences());
        navigationController = new NavigationController(TSUME, NavigatorConfiguration.PROBLEMS);
        gameNavigatorPanel = new GameNavigatorPanel(TSUME);
        byoYomiFeedbackPanel = new ByoYomiFeedbackPanel();
        byoYomiProgressPanel = new ByoYomiProgressPanel(historyMapper);

        shogiBoard.setUpperRightPanel(byoYomiFeedbackPanel);
        shogiBoard.setLowerLeftPanel(byoYomiProgressPanel);

        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);

        initWidget(shogiBoard);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating tsume view");
        shogiBoard.activate(eventBus);
        gameNavigatorPanel.activate(eventBus);
        byoYomiFeedbackPanel.activate(eventBus);
        byoYomiProgressPanel.activate(eventBus);
        navigationController.activate(eventBus);
    }

    public void initUi(ByoYomiPlace place) {
        byoYomiProgressPanel.setTimerVisible(place.getMaxTimeSec() != 0);
        byoYomiFeedbackPanel.setTimerVisible(place.getTimePerMove() != 0);
    }

    public ShogiPosition getCurrentPosition() {
        return shogiBoard.getPosition();
    }
}
