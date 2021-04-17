package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.engine.KifuEvaluationChartPanel;
import com.playshogi.website.gwt.client.widget.engine.PositionEvaluationDetailsPanel;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.kifu.GameTreePanel;
import com.playshogi.website.gwt.client.widget.kifu.KifuInformationPanel;
import com.playshogi.website.gwt.client.widget.kifu.KifuNavigationPanel;

import java.util.Optional;

@Singleton
public class ViewKifuView extends Composite {

    private static final String VIEWKIFU = "viewkifu";
    private static final RegExp ARROW_PATTERN = RegExp.compile("ARROW,(....),.,.,.,\\((\\d*),(\\d*),(\\d*),(\\d*)\\)");

    interface MyEventBinder extends EventBinder<ViewKifuView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final KifuNavigationPanel kifuNavigationPanel;
    private final KifuInformationPanel kifuInformationPanel;
    private final GameTreePanel gameTreePanel;
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

        kifuEvaluationChartPanel = new KifuEvaluationChartPanel();

        textArea = new TextArea();
        textArea.setSize("782px", "150px");
        textArea.setStyleName("lesson-content");
        textArea.setEnabled(false);


        gameTreePanel = new GameTreePanel(VIEWKIFU, gameNavigator.getGameNavigation(), true);

        ScrollPanel treeScrollPanel = new ScrollPanel();
        treeScrollPanel.add(gameTreePanel);
        treeScrollPanel.setSize("620px", "600px");

        TabLayoutPanel rightPanel = new TabLayoutPanel(1.5, Style.Unit.EM);

        rightPanel.add(treeScrollPanel, "Moves");
        rightPanel.add(kifuEvaluationChartPanel, "Computer");

        rightPanel.setSize("650px", "640px");
        rightPanel.getElement().getStyle().setMarginTop(3, Style.Unit.PX);

        VerticalPanel boardAndTextPanel = new VerticalPanel();
        boardAndTextPanel.add(shogiBoard);
        boardAndTextPanel.add(textArea);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(boardAndTextPanel);
        horizontalPanel.add(rightPanel);

        positionEvaluationDetailsPanel = new PositionEvaluationDetailsPanel(shogiBoard);
        positionEvaluationDetailsPanel.setSize("1450px", "300px");

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(horizontalPanel);
        verticalPanel.add(positionEvaluationDetailsPanel);


        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(verticalPanel);
        scrollPanel.setSize("100%", "100%");

//        initWidget(horizontalPanel);
        initWidget(scrollPanel);
    }

    public void activate(final EventBus eventBus, final String kifuId) {
        GWT.log("Activating ViewKifuView");
        eventBinder.bindEventHandlers(this, eventBus);
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        kifuInformationPanel.activate(eventBus);
        positionEvaluationDetailsPanel.activate(eventBus);
        kifuEvaluationChartPanel.activate(eventBus, kifuId);
        gameTreePanel.activate(eventBus);
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
