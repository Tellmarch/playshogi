package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.engine.KifuEvaluationChartPanel;
import com.playshogi.website.gwt.client.widget.engine.PositionEvaluationDetailsPanel;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.kifu.KifuInformationPanel;
import com.playshogi.website.gwt.client.widget.kifu.KifuNavigationPanel;

import java.util.Optional;

@Singleton
public class ViewKifuView extends Composite {

    private static final String VIEWKIFU = "viewkifu";
    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final KifuNavigationPanel kifuNavigationPanel;
    private final KifuInformationPanel kifuInformationPanel;
    private final PositionEvaluationDetailsPanel positionEvaluationDetailsPanel;
    private final KifuEvaluationChartPanel kifuEvaluationChartPanel;
    private final TextArea textArea;

    @Inject
    public ViewKifuView() {
        GWT.log("Creating ViewKifuView");
        shogiBoard = new ShogiBoard(VIEWKIFU);
        gameNavigator = new GameNavigator(VIEWKIFU);

        kifuNavigationPanel = new KifuNavigationPanel(gameNavigator);
        kifuInformationPanel = new KifuInformationPanel();

        shogiBoard.setUpperRightPanel(kifuNavigationPanel);
        shogiBoard.setLowerLeftPanel(kifuInformationPanel);

        positionEvaluationDetailsPanel = new PositionEvaluationDetailsPanel(shogiBoard);
        kifuEvaluationChartPanel = new KifuEvaluationChartPanel();

        textArea = new TextArea();
        textArea.setSize("782px", "150px");
        textArea.setStyleName("lesson-content");

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(shogiBoard);
        horizontalPanel.add(kifuEvaluationChartPanel);

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(horizontalPanel);
        verticalPanel.add(textArea);
        verticalPanel.add(positionEvaluationDetailsPanel);

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(verticalPanel);
        scrollPanel.setSize("100%", "100%");

        initWidget(scrollPanel);
    }

    public void activate(final EventBus eventBus, final String kifuId) {
        GWT.log("Activating ViewKifuView");
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        kifuInformationPanel.activate(eventBus);
        positionEvaluationDetailsPanel.activate(eventBus);
        kifuEvaluationChartPanel.activate(eventBus, kifuId);
    }

    public GameNavigator getGameNavigator() {
        return gameNavigator;
    }

    @EventHandler
    public void onPositionChanged(final PositionChangedEvent event) {
        GWT.log("ViewKifuView: handle PositionChangedEvent");

        Optional<String> comment = gameNavigator.getGameNavigation().getCurrentComment();
        if (comment.isPresent()) {
            textArea.setText(comment.get());
        } else {
            textArea.setText("");
        }
    }
}
