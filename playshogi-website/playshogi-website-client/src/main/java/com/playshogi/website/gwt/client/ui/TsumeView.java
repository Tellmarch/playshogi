package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.problems.ProblemFeedbackPanel;
import com.playshogi.website.gwt.client.widget.problems.ProblemOptionsPanelBeta;

@Singleton
public class TsumeView extends Composite {

    private static final String TSUME = "tsume";

    private static final int[] MOVES = {3, 5, 7, 9, 11, 13};

    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final ProblemFeedbackPanel problemFeedbackPanel;
    private final ProblemOptionsPanelBeta problemOptionsPanelBeta;

    @Inject
    public TsumeView() {
        GWT.log("Creating tsume view");
        shogiBoard = new ShogiBoard(TSUME);
        gameNavigator = new GameNavigator(TSUME);
        problemFeedbackPanel = new ProblemFeedbackPanel(gameNavigator, true);

        shogiBoard.setUpperRightPanel(problemFeedbackPanel);

        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);

        gameNavigator.getNavigatorConfiguration().setProblemMode(true);

        HorizontalPanel panel = new HorizontalPanel();
        panel.add(shogiBoard);
        problemOptionsPanelBeta = new ProblemOptionsPanelBeta(MOVES);
        panel.add(problemOptionsPanelBeta);

        initWidget(panel);
    }

    public ShogiPosition getCurrentPosition() {
        return shogiBoard.getPosition();
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating tsume view");
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        problemFeedbackPanel.activate(eventBus);
        problemOptionsPanelBeta.activate(eventBus);
    }

}
