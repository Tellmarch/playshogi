package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.kifu.KifuEditorPanel;
import com.playshogi.website.gwt.client.widget.kifu.PositionEditingPanel;

@Singleton
public class ProblemEditorView extends Composite {

    private static final String PROBLEM_EDITOR = "pbeditor";
    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final KifuEditorPanel kifuEditorPanel;
    private final PositionEditingPanel positionEditingPanel;

    @Inject
    public ProblemEditorView() {
        GWT.log("Creating problem editor view");
        shogiBoard = new ShogiBoard(PROBLEM_EDITOR);
        gameNavigator = new GameNavigator(PROBLEM_EDITOR);

        kifuEditorPanel = new KifuEditorPanel(gameNavigator);
        positionEditingPanel = new PositionEditingPanel();

        shogiBoard.setUpperRightPanel(kifuEditorPanel);
        shogiBoard.setLowerLeftPanel(positionEditingPanel);

        initWidget(shogiBoard);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating problem editor view");
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        kifuEditorPanel.activate(eventBus);
        positionEditingPanel.activate(eventBus);
    }

}
