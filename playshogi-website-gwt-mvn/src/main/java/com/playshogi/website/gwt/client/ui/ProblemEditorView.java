package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameTree;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.EditModeSelectedEvent;
import com.playshogi.website.gwt.client.events.kifu.SwitchPlayerToPlayEvent;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.kifu.GameTreePanel;
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
    private final GameTreePanel gameTreePanel;
    private final GameNavigation gameNavigation;
    private final TextArea textArea;

    private EventBus eventBus;

    @Inject
    public ProblemEditorView() {
        GWT.log("Creating problem editor view");
        shogiBoard = new ShogiBoard(PROBLEM_EDITOR);
        shogiBoard.getBoardConfiguration().setPositionEditingMode(true);

        gameNavigation = new GameNavigation(new ShogiRulesEngine(),
                new GameTree(), ShogiInitialPositionFactory.createInitialPosition());
        gameNavigator = new GameNavigator(PROBLEM_EDITOR, gameNavigation);

        kifuEditorPanel = new KifuEditorPanel(gameNavigator);
        positionEditingPanel = new PositionEditingPanel();

        shogiBoard.setUpperRightPanel(kifuEditorPanel);
        shogiBoard.setLowerLeftPanel(positionEditingPanel);


        VerticalPanel verticalPanel = new VerticalPanel();

        verticalPanel.add(shogiBoard);
        textArea = new TextArea();
        textArea.setSize("782px", "150px");
        textArea.setStyleName("lesson-content");
        verticalPanel.add(textArea);

        HorizontalPanel horizontalPanel = new HorizontalPanel();

        horizontalPanel.add(verticalPanel);
        gameTreePanel = new GameTreePanel(PROBLEM_EDITOR, gameNavigation);
        horizontalPanel.add(gameTreePanel);

        initWidget(horizontalPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating problem editor view");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        kifuEditorPanel.activate(eventBus);
        positionEditingPanel.activate(eventBus);
        gameTreePanel.activate(eventBus);
    }

    @EventHandler
    public void onEditModeSelectedEvent(final EditModeSelectedEvent event) {
        GWT.log("Problem editor: handle EditModeSelectedEvent - " + event.isEditMode());
        shogiBoard.getBoardConfiguration().setPositionEditingMode(event.isEditMode());
        // Exiting board editing mode
        if (!event.isEditMode()) {
            gameNavigator.reset(shogiBoard.getPosition());
        }
        // To reset selection/handlers
        shogiBoard.displayPosition();
    }

    @EventHandler
    public void onSwitchPlayerToPlay(final SwitchPlayerToPlayEvent event) {
        GWT.log("Problem editor: handle SwitchPlayerToPlayEvent");
        shogiBoard.getPosition().setPlayerToMove(shogiBoard.getPosition().getPlayerToMove().opposite());
        eventBus.fireEvent(new PositionChangedEvent(shogiBoard.getPosition(), true));
    }

    public GameNavigation getGameNavigation() {
        return gameNavigation;
    }
}
