package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.kifu.KifuInformationPanel;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;

@Singleton
public class ProblemsView extends Composite {

    private static final String PROBLEMS = "problems";

    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final ProblemFeedbackPanel problemFeedbackPanel;
    private final KifuInformationPanel kifuInformationPanel;

    @Inject
    public ProblemsView(final SessionInformation sessionInformation) {
        GWT.log("Creating Problems view");
        shogiBoard = new ShogiBoard(PROBLEMS, sessionInformation.getUserPreferences());
        gameNavigator = new GameNavigator(PROBLEMS);
        problemFeedbackPanel = new ProblemFeedbackPanel(gameNavigator, false);
        kifuInformationPanel = new KifuInformationPanel();

        shogiBoard.setLowerLeftPanel(kifuInformationPanel);
        shogiBoard.setUpperRightPanel(problemFeedbackPanel);
        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);
        gameNavigator.getNavigatorConfiguration().setProblemMode(true);

        initWidget(shogiBoard);
    }

    public ShogiPosition getCurrentPosition() {
        return shogiBoard.getPosition();
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating tsume view");
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        problemFeedbackPanel.activate(eventBus);
        kifuInformationPanel.activate(eventBus);
    }

}
