package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.controller.NavigationController;
import com.playshogi.website.gwt.client.widget.board.BoardButtons;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigatorPanel;
import com.playshogi.website.gwt.client.widget.gamenavigator.NavigatorConfiguration;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;

@Singleton
public class ProblemView extends Composite {

    private static final String PROBLEMS = "problems";

    interface MyEventBinder extends EventBinder<ProblemView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ShogiBoard shogiBoard;
    private final GameNavigatorPanel gameNavigatorPanel;
    private final ProblemFeedbackPanel problemFeedbackPanel;
    private final NavigationController navigationController;

    @Inject
    public ProblemView(final SessionInformation sessionInformation) {
        GWT.log("Creating Problem view");
        shogiBoard = new ShogiBoard(PROBLEMS, sessionInformation.getUserPreferences());
        navigationController = new NavigationController(PROBLEMS, NavigatorConfiguration.PROBLEMS);
        gameNavigatorPanel = new GameNavigatorPanel(PROBLEMS);
        problemFeedbackPanel = new ProblemFeedbackPanel(gameNavigatorPanel, false);

        shogiBoard.setUpperRightPanel(problemFeedbackPanel);
        shogiBoard.setLowerLeftPanel(createLowerLeftPanel());
        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);

        initWidget(shogiBoard);
    }

    private FlowPanel createLowerLeftPanel() {

        FlowPanel panel = new FlowPanel();
        panel.add(BoardButtons.createSettingsWidget(shogiBoard));
        panel.add(BoardButtons.createClearArrowsWidget(shogiBoard));
        return panel;
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating ProblemsView");
        eventBinder.bindEventHandlers(this, eventBus);
        shogiBoard.activate(eventBus);
        gameNavigatorPanel.activate(eventBus);
        problemFeedbackPanel.activate(eventBus);
        navigationController.activate(eventBus);
    }

}
