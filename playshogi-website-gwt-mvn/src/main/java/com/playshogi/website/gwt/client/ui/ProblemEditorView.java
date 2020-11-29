package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.kifu.EditModeSelectedEvent;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.kifu.KifuEditorPanel;
import com.playshogi.website.gwt.client.widget.kifu.PositionEditingPanel;

@Singleton
public class ProblemEditorView extends Composite {

    private static final String PROBLEM_EDITOR = "pbeditor";

    interface MyEventBinder extends EventBinder<ProblemEditorView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

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
        eventBinder.bindEventHandlers(this, eventBus);
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        kifuEditorPanel.activate(eventBus);
        positionEditingPanel.activate(eventBus);
    }

    @EventHandler
    public void onEditModeSelectedEvent(final EditModeSelectedEvent event) {
        GWT.log("Problem editor: handle EditModeSelectedEvent - " + event.isEditMode());
        shogiBoard.getBoardConfiguration().setPositionEditingMode(event.isEditMode());
    }
}
