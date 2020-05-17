package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.problems.ByoYomiProgressPanel;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;

@Singleton
public class ByoYomiView extends Composite {

    private static final String TSUME = "byoyomi";

    private static final int[] MOVES = {3, 5, 7, 9, 11, 13};

    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final ProblemFeedbackPanel problemFeedbackPanel;
    private final ByoYomiProgressPanel byoYomiProgressPanel;

    @Inject
    public ByoYomiView() {
        GWT.log("Creating byo yomi view");
        shogiBoard = new ShogiBoard(TSUME);
        gameNavigator = new GameNavigator(TSUME);
        problemFeedbackPanel = new ProblemFeedbackPanel(null, false);
        byoYomiProgressPanel = new ByoYomiProgressPanel();

        shogiBoard.setUpperRightPanel(problemFeedbackPanel);
        shogiBoard.setLowerLeftPanel(byoYomiProgressPanel);

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
        byoYomiProgressPanel.activate(eventBus);
    }

}
