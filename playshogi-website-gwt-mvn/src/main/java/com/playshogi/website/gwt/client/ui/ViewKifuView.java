package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.engine.KifuEvaluationChartPanel;
import com.playshogi.website.gwt.client.widget.engine.PositionEvaluationDetailsPanel;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.kifu.KifuInformationPanel;
import com.playshogi.website.gwt.client.widget.kifu.KifuNavigationPanel;

@Singleton
public class ViewKifuView extends Composite {

    private static final String VIEWKIFU = "viewkifu";
    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final KifuNavigationPanel kifuNavigationPanel;
    private final KifuInformationPanel kifuInformationPanel;
    private final PositionEvaluationDetailsPanel positionEvaluationDetailsPanel;
    private final KifuEvaluationChartPanel kifuEvaluationChartPanel;

    @Inject
    public ViewKifuView() {
        GWT.log("Creating ViewKifuView");
        shogiBoard = new ShogiBoard(VIEWKIFU);
        gameNavigator = new GameNavigator(VIEWKIFU);

        kifuNavigationPanel = new KifuNavigationPanel(gameNavigator);
        kifuInformationPanel = new KifuInformationPanel();

        shogiBoard.setUpperRightPanel(kifuNavigationPanel);
        shogiBoard.setLowerLeftPanel(kifuInformationPanel);

        positionEvaluationDetailsPanel = new PositionEvaluationDetailsPanel();
        kifuEvaluationChartPanel = new KifuEvaluationChartPanel();

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(shogiBoard);
        horizontalPanel.add(kifuEvaluationChartPanel);

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(horizontalPanel);
        verticalPanel.add(positionEvaluationDetailsPanel);

        initWidget(verticalPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating ViewKifuView");
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        kifuInformationPanel.activate(eventBus);
        positionEvaluationDetailsPanel.activate(eventBus);
        kifuEvaluationChartPanel.activate(eventBus);
    }

    public GameNavigator getGameNavigator() {
        return gameNavigator;
    }
}
