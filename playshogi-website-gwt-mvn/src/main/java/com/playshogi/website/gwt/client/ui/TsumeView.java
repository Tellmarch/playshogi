package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;

@Singleton
public class TsumeView extends Composite {

    private static final String TSUME = "tsume";
    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final ProblemFeedbackPanel problemFeedbackPanel;

    @Inject
    public TsumeView() {
        GWT.log("Creating tsume view");
        shogiBoard = new ShogiBoard(TSUME);
        gameNavigator = new GameNavigator(TSUME);
        problemFeedbackPanel = new ProblemFeedbackPanel(gameNavigator);

        shogiBoard.setUpperRightPanel(problemFeedbackPanel);

        shogiBoard.getBoardConfiguration().setShowGoteKomadai(false);
        shogiBoard.getBoardConfiguration().setPlayGoteMoves(false);

        gameNavigator.getNavigatorConfiguration().setProblemMode(true);

        initWidget(shogiBoard);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating tsume view");
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        problemFeedbackPanel.activate(eventBus);
    }

}
