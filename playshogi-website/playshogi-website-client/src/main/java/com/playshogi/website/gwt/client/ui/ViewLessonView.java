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
import com.playshogi.website.gwt.client.widget.engine.PositionEvaluationDetailsPanel;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigatorPanel;
import com.playshogi.website.gwt.client.widget.gamenavigator.NavigatorConfiguration;
import com.playshogi.website.gwt.client.widget.kifu.DatabasePanel;
import com.playshogi.website.gwt.client.widget.kifu.GameTreePanel;
import com.playshogi.website.gwt.client.widget.lessons.LessonFeedbackPanel;
import com.playshogi.website.gwt.client.widget.lessons.LessonNavigatorPanel;

import java.util.Optional;

@Singleton
public class ViewLessonView extends Composite {

    private static final String VIEWLESSON = "viewlesson";

    interface MyEventBinder extends EventBinder<ViewLessonView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final SessionInformation sessionInformation;
    private final ShogiBoard shogiBoard;
    private final GameNavigatorPanel gameNavigatorPanel;
    private final GameTreePanel gameTreePanel;
    private final PositionEvaluationDetailsPanel positionEvaluationDetailsPanel;
    private final TextArea textArea;
    private final BoardSettingsPanel boardSettingsPanel;
    private final DatabasePanel databasePanel;
    private final NavigationController navigationController;
    private final LessonNavigatorPanel lessonNavigatorPanel;
    private final LessonFeedbackPanel lessonFeedbackPanel;

    @Inject
    public ViewLessonView(final AppPlaceHistoryMapper appPlaceHistoryMapper,
                          final SessionInformation sessionInformation) {
        GWT.log("Creating ViewLessonView");
        this.sessionInformation = sessionInformation;
        shogiBoard = new ShogiBoard(VIEWLESSON, sessionInformation.getUserPreferences());
        navigationController = new NavigationController(VIEWLESSON, NavigatorConfiguration.LESSONS);
        gameNavigatorPanel = new GameNavigatorPanel(VIEWLESSON);
        lessonNavigatorPanel = new LessonNavigatorPanel(navigationController, sessionInformation.getUserPreferences());
        lessonFeedbackPanel = new LessonFeedbackPanel();

        shogiBoard.setUpperRightPanel(lessonNavigatorPanel.getAsWidget());
        shogiBoard.setLowerLeftPanel(lessonFeedbackPanel.getAsWidget());

        textArea = createCommentsArea();

        gameTreePanel = new GameTreePanel(VIEWLESSON, navigationController, true,
                sessionInformation.getUserPreferences(), true, true);

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
        positionEvaluationDetailsPanel.activate(eventBus);
        gameTreePanel.activate(eventBus);
        boardSettingsPanel.activate(eventBus);
        databasePanel.activate(eventBus);
        navigationController.activate(eventBus);
        lessonNavigatorPanel.activate(eventBus);
        lessonFeedbackPanel.activate(eventBus);
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
