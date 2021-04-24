package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.record.GameTree;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.openings.PositionKifusPanel;
import com.playshogi.website.gwt.client.widget.openings.PositionStatisticsPanel;

@Singleton
public class OpeningsView extends Composite {

    private static final String OPENINGS = "openings";
    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final PositionStatisticsPanel positionStatisticsPanel;
    private final PositionKifusPanel positionKifusPanel;

    @Inject
    public OpeningsView(final AppPlaceHistoryMapper historyMapper, final SessionInformation sessionInformation) {
        GWT.log("Creating openings view");
        shogiBoard = new ShogiBoard(OPENINGS, sessionInformation.getUserPreferences());
        gameNavigator = new GameNavigator(OPENINGS);

        positionStatisticsPanel = new PositionStatisticsPanel(historyMapper);
        positionKifusPanel = new PositionKifusPanel(historyMapper);

        shogiBoard.setUpperRightPanel(positionStatisticsPanel);
        shogiBoard.setLowerLeftPanel(positionKifusPanel);

        initWidget(shogiBoard);
    }

    public void activate(final ShogiPosition position, final String gameSetId, final EventBus eventBus) {
        GWT.log("Activating openings view");
        shogiBoard.activate(eventBus);
        gameNavigator.getGameNavigation().setGameTree(new GameTree(position), 0);
        gameNavigator.activate(eventBus);
        positionStatisticsPanel.activate(gameSetId, eventBus);
        positionKifusPanel.activate(eventBus);
    }

}
