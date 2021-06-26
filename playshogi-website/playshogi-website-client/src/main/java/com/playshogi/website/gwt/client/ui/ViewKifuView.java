package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.controller.NavigationController;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.widget.board.BoardSettingsPanel;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.engine.KifuEvaluationChartPanel;
import com.playshogi.website.gwt.client.widget.engine.PositionEvaluationDetailsPanel;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigatorPanel;
import com.playshogi.website.gwt.client.widget.kifu.DatabasePanel;
import com.playshogi.website.gwt.client.widget.kifu.GameTreePanel;
import com.playshogi.website.gwt.client.widget.kifu.KifuInformationPanel;
import com.playshogi.website.gwt.client.widget.kifu.KifuNavigationPanel;

import java.util.Optional;

@Singleton
public class ViewKifuView extends Composite {

    private static final String VIEWKIFU = "viewkifu";

    interface MyEventBinder extends EventBinder<ViewKifuView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final SessionInformation sessionInformation;
    private final ShogiBoard shogiBoard;
    private final GameNavigatorPanel gameNavigatorPanel;
    private final KifuInformationPanel kifuInformationPanel;
    private final GameTreePanel gameTreePanel;
    private final PositionEvaluationDetailsPanel positionEvaluationDetailsPanel;
    private final KifuEvaluationChartPanel kifuEvaluationChartPanel;
    private final TextArea textArea;
    private final BoardSettingsPanel boardSettingsPanel;
    private final DatabasePanel databasePanel;
    private final NavigationController navigationController;

    @Inject
    public ViewKifuView(final AppPlaceHistoryMapper appPlaceHistoryMapper,
                        final SessionInformation sessionInformation) {
        GWT.log("Creating ViewKifuView");
        this.sessionInformation = sessionInformation;
        shogiBoard = new ShogiBoard(VIEWKIFU, sessionInformation.getUserPreferences());
        navigationController = new NavigationController(VIEWKIFU);
        gameNavigatorPanel = new GameNavigatorPanel(VIEWKIFU);

        kifuInformationPanel = new KifuInformationPanel();

        shogiBoard.setUpperRightPanel(new KifuNavigationPanel(gameNavigatorPanel));
        shogiBoard.setLowerLeftPanel(kifuInformationPanel);

        kifuEvaluationChartPanel = new KifuEvaluationChartPanel(sessionInformation.getUserPreferences());

        textArea = createCommentsArea();

        gameTreePanel = new GameTreePanel(VIEWKIFU, navigationController, true,
                sessionInformation.getUserPreferences(), false, true);

        boardSettingsPanel = new BoardSettingsPanel(this.sessionInformation.getUserPreferences());
        databasePanel = new DatabasePanel(appPlaceHistoryMapper, sessionInformation.getUserPreferences());

        positionEvaluationDetailsPanel = new PositionEvaluationDetailsPanel(shogiBoard,
                sessionInformation);
        positionEvaluationDetailsPanel.setSize("1450px", "300px");

        VerticalPanel boardAndTextPanel = new VerticalPanel();
        boardAndTextPanel.add(shogiBoard);
        boardAndTextPanel.add(textArea);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(boardAndTextPanel);
        horizontalPanel.add(createRightTabsPanel());

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(horizontalPanel);
        verticalPanel.add(positionEvaluationDetailsPanel);

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(verticalPanel);
        scrollPanel.setSize("100%", "100%");

        initWidget(scrollPanel);
    }

    private TextArea createCommentsArea() {
        final TextArea textArea;
        textArea = new TextArea();
        textArea.setSize("782px", "150px");
        textArea.setStyleName("lesson-content");
        textArea.setEnabled(false);
        return textArea;
    }

    private TabLayoutPanel createRightTabsPanel() {
        ScrollPanel treeScrollPanel = new ScrollPanel();
        treeScrollPanel.add(gameTreePanel);
        treeScrollPanel.setSize("620px", "600px");

        TabLayoutPanel tabsPanel = new TabLayoutPanel(1.5, Style.Unit.EM);

        tabsPanel.add(treeScrollPanel, "Moves");
        tabsPanel.add(kifuEvaluationChartPanel, "Computer");
        tabsPanel.add(boardSettingsPanel, "Board");
        tabsPanel.add(databasePanel, "Database");

        tabsPanel.setSize("650px", "640px");
        tabsPanel.getElement().getStyle().setMarginTop(3, Style.Unit.PX);
        return tabsPanel;
    }

    public void activate(final EventBus eventBus, final String kifuId, final boolean inverted) {
        GWT.log("Activating ViewKifuView");
        eventBinder.bindEventHandlers(this, eventBus);
        shogiBoard.getBoardConfiguration().setInverted(inverted);
        shogiBoard.activate(eventBus);
        gameNavigatorPanel.activate(eventBus);
        kifuInformationPanel.activate(eventBus);
        positionEvaluationDetailsPanel.activate(eventBus);
        kifuEvaluationChartPanel.activate(eventBus, kifuId);
        gameTreePanel.activate(eventBus);
        boardSettingsPanel.activate(eventBus);
        databasePanel.activate(eventBus);
        navigationController.activate(eventBus);
    }

    public NavigationController getNavigationController() {
        return navigationController;
    }

    @EventHandler
    public void onPositionChanged(final PositionChangedEvent event) {
        GWT.log("ViewKifuView: handle PositionChangedEvent");

        Optional<String> comment = navigationController.getGameNavigation().getCurrentComment();
        if (comment.isPresent()) {
            textArea.setText(comment.get());
        } else {
            textArea.setText("");
        }
    }

}
