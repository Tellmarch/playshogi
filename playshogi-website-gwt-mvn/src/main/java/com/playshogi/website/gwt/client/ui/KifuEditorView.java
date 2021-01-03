package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.record.*;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.EditModeSelectedEvent;
import com.playshogi.website.gwt.client.events.kifu.GameInformationChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.GameRecordSaveRequestedEvent;
import com.playshogi.website.gwt.client.events.kifu.SwitchPlayerToPlayEvent;
import com.playshogi.website.gwt.client.place.KifuEditorPlace;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.kifu.GameTreePanel;
import com.playshogi.website.gwt.client.widget.kifu.KifuEditorLeftBarPanel;
import com.playshogi.website.gwt.client.widget.kifu.KifuEditorPanel;
import com.playshogi.website.gwt.client.widget.kifu.SaveKifuPanel;
import com.playshogi.website.gwt.shared.models.KifuDetails;

import java.util.Optional;

@Singleton
public class KifuEditorView extends Composite {

    private static final String PROBLEM_EDITOR = "pbeditor";

    interface MyEventBinder extends EventBinder<KifuEditorView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final KifuEditorPanel kifuEditorPanel;
    private final GameTreePanel gameTreePanel;
    private final GameNavigation gameNavigation;
    private final TextArea textArea;
    private final KifuEditorLeftBarPanel kifuEditorLeftBarPanel;
    private final SaveKifuPanel saveKifuPanel;

    private EventBus eventBus;
    private KifuDetails.KifuType type;

    @Inject
    public KifuEditorView(final PlaceController placeController) {
        GWT.log("Creating problem editor view");
        saveKifuPanel = new SaveKifuPanel(placeController);
        shogiBoard = new ShogiBoard(PROBLEM_EDITOR);
        shogiBoard.getBoardConfiguration().setPositionEditingMode(false);

        gameNavigation = new GameNavigation(new ShogiRulesEngine(), new GameTree());
        gameNavigator = new GameNavigator(PROBLEM_EDITOR, gameNavigation);

        kifuEditorLeftBarPanel = new KifuEditorLeftBarPanel();
        kifuEditorPanel = new KifuEditorPanel(gameNavigator);

        shogiBoard.setUpperRightPanel(kifuEditorPanel);
        shogiBoard.setLowerLeftPanel(kifuEditorLeftBarPanel);


        VerticalPanel verticalPanel = new VerticalPanel();

        verticalPanel.add(shogiBoard);
        textArea = new TextArea();
        textArea.setSize("782px", "150px");
        textArea.setStyleName("lesson-content");
        textArea.addKeyUpHandler(keyUpEvent -> gameNavigation.getCurrentNode().setComment(textArea.getText()));
        verticalPanel.add(textArea);

        HorizontalPanel horizontalPanel = new HorizontalPanel();

        horizontalPanel.add(verticalPanel);

        gameTreePanel = new GameTreePanel(PROBLEM_EDITOR, gameNavigation, false);

        ScrollPanel treeScrollPanel = new ScrollPanel();
        treeScrollPanel.add(gameTreePanel);
        treeScrollPanel.setSize("200%", "750px");

        horizontalPanel.add(treeScrollPanel);

        initWidget(horizontalPanel);
    }

    public void activate(final EventBus eventBus, final KifuEditorPlace place) {
        GWT.log("Activating problem editor view");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        kifuEditorPanel.activate(eventBus, place);
        gameTreePanel.activate(eventBus);
        kifuEditorLeftBarPanel.activate(eventBus);
        saveKifuPanel.activate(eventBus);
        type = place.getType();
    }

    public GameNavigation getGameNavigation() {
        return gameNavigation;
    }

    public GameInformation getGameInformation() {
        return kifuEditorLeftBarPanel.getGameInformation();
    }

    public GameRecord getGameRecord() {
        GameTree gameTree = gameNavigation.getGameTree();
        GameInformation gameInformation = getGameInformation();
        //TODO GameResult
        return new GameRecord(gameInformation, gameTree, GameResult.UNKNOWN);
    }

    public void loadGameRecord(final GameRecord gameRecord) {
        eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree()));
        eventBus.fireEvent(new GameInformationChangedEvent(gameRecord.getGameInformation()));
    }

    @EventHandler
    public void onEditModeSelectedEvent(final EditModeSelectedEvent event) {
        GWT.log("Problem editor: handle EditModeSelectedEvent - " + event.isEditMode());
        shogiBoard.getBoardConfiguration().setPositionEditingMode(event.isEditMode());
        // Exiting board editing mode
        if (!event.isEditMode()) {
            Node currentNode = gameNavigation.getCurrentNode();

            if (currentNode.getMove() == null && !currentNode.hasChildren()) {
                // Root node, and no child move yet - we change the starting position
                eventBus.fireEvent(new GameTreeChangedEvent(new GameTree(shogiBoard.getPosition()), 0));
            } else if (currentNode.getMove() instanceof EditMove && !currentNode.hasChildren()) {
                // Edit move, and no child move yet - we modify the edit move
                currentNode.setMove(new EditMove(shogiBoard.getPosition()));
            } else {
                // In all other cases, we insert a new Edit move
                gameNavigator.addMove(new EditMove(shogiBoard.getPosition()), true);
            }
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

    @EventHandler
    public void onPositionChanged(final PositionChangedEvent event) {
        GWT.log("Problem editor: handle PositionChangedEvent");

        Optional<String> comment = gameNavigation.getCurrentComment();
        if (comment.isPresent()) {
            textArea.setText(comment.get());
        } else {
            textArea.setText("");
        }
    }

    @EventHandler
    public void onGameRecordSaveRequested(final GameRecordSaveRequestedEvent event) {
        GWT.log("problem editor Activity Handling GameRecordSaveRequestedEvent");
        saveKifuPanel.showInSaveDialog(getGameRecord(), type);
    }

}
