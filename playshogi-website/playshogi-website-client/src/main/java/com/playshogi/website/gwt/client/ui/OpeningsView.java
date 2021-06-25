package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.record.GameTree;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.controller.NavigationController;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigatorPanel;
import com.playshogi.website.gwt.client.widget.openings.PositionKifusPanel;
import com.playshogi.website.gwt.client.widget.openings.PositionStatisticsPanel;

@Singleton
public class OpeningsView extends Composite {

    private static final String OPENINGS = "openings";
    private final ShogiBoard shogiBoard;
    private final GameNavigatorPanel gameNavigatorPanel;
    private final PositionStatisticsPanel positionStatisticsPanel;
    private final PositionKifusPanel positionKifusPanel;
    private final NavigationController navigationController;

    @Inject
    public OpeningsView(final AppPlaceHistoryMapper historyMapper, final SessionInformation sessionInformation) {
        GWT.log("Creating openings view");
        shogiBoard = new ShogiBoard(OPENINGS, sessionInformation.getUserPreferences());
        navigationController = new NavigationController(OPENINGS);
        gameNavigatorPanel = new GameNavigatorPanel(OPENINGS);

        positionStatisticsPanel = new PositionStatisticsPanel(historyMapper, sessionInformation.getUserPreferences(),
                true);
        positionKifusPanel = new PositionKifusPanel(historyMapper, true);

        shogiBoard.setUpperRightPanel(positionStatisticsPanel);
        shogiBoard.setLowerLeftPanel(positionKifusPanel);

        initWidget(shogiBoard);
    }

    public void activate(final ShogiPosition position, final EventBus eventBus) {
        GWT.log("Activating openings view");
        shogiBoard.activate(eventBus);
        navigationController.getGameNavigation().setGameTree(new GameTree(position), 0);
        gameNavigatorPanel.activate(eventBus);
        positionStatisticsPanel.activate(eventBus);
        positionKifusPanel.activate(eventBus);
        navigationController.activate(eventBus);
    }

}
