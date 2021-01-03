package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.usf.UsfUtil;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.widget.board.Color;
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

        positionEvaluationDetailsPanel = new PositionEvaluationDetailsPanel(shogiBoard);
        kifuEvaluationChartPanel = new KifuEvaluationChartPanel();

        textArea = new TextArea();
        textArea.setSize("782px", "150px");
        textArea.setStyleName("lesson-content");
        textArea.setEnabled(false);

        gameTreePanel = new GameTreePanel(VIEWKIFU, gameNavigator.getGameNavigation(), true);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(shogiBoard);
        horizontalPanel.add(kifuEvaluationChartPanel);

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(horizontalPanel);
        verticalPanel.add(textArea);
        verticalPanel.add(positionEvaluationDetailsPanel);

        HorizontalPanel withTreePanel = new HorizontalPanel();
        withTreePanel.add(verticalPanel);

        ScrollPanel treeScrollPanel = new ScrollPanel();
        treeScrollPanel.add(gameTreePanel);
        treeScrollPanel.setSize("200%", "750px");

        withTreePanel.add(treeScrollPanel);

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(withTreePanel);
        scrollPanel.setSize("100%", "100%");

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
        Scheduler.get().scheduleDeferred(this::drawObjects);

    }

    private void drawObjects() {
        Optional<String> objects = gameNavigator.getGameNavigation().getCurrentNode().getObjects();
        if (objects.isPresent()) {
            for (String object : objects.get().split("\n")) {
                GWT.log("OBJECT: " + object);
                if (object.startsWith("ARROW,")) {
                    MatchResult result = ARROW_PATTERN.exec(object);

                    String coordinates = result.getGroup(1);
                    Square from = Square.of(UsfUtil.char2ColumnNumber(coordinates.charAt(0)),
                            UsfUtil.char2RowNumber(coordinates.charAt(1)));
                    Square to = Square.of(UsfUtil.char2ColumnNumber(coordinates.charAt(2)),
                            UsfUtil.char2RowNumber(coordinates.charAt(3)));
                    GWT.log("Drawing arrow from " + from + " to " + to);
                    Color c = new Color(Integer.parseInt(result.getGroup(2)), Integer.parseInt(result.getGroup(3)),
                            Integer.parseInt(result.getGroup(4)), Integer.parseInt(result.getGroup(5)));
                    shogiBoard.getDecorationController().drawArrow(from, to, c);
                }
            }
        }
    }
}
