package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.record.*;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.controller.NavigationController;
import com.playshogi.website.gwt.client.events.gametree.EditMovePlayedEvent;
import com.playshogi.website.gwt.client.events.gametree.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.EditModeSelectedEvent;
import com.playshogi.website.gwt.client.events.kifu.GameInformationChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.GameRecordSaveRequestedEvent;
import com.playshogi.website.gwt.client.events.kifu.SwitchPlayerToPlayEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.KifuEditorPlace;
import com.playshogi.website.gwt.client.widget.board.BoardSettingsPanel;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.engine.PositionEvaluationDetailsPanel;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.kifu.*;
import com.playshogi.website.gwt.shared.models.KifuDetails;

import java.util.Optional;

@Singleton
public class KifuEditorView extends Composite {

    private static final String KIFU_EDITOR = "kifueditor";
    private final NavigationController navigationController;
    private KifuEditorPlace place;

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
    private final BoardSettingsPanel boardSettingsPanel;
    private final DatabasePanel databasePanel;
    private final PositionEvaluationDetailsPanel positionEvaluationDetailsPanel;

    private EventBus eventBus;

    @Inject
    public KifuEditorView(final PlaceController placeController, final SessionInformation sessionInformation,
                          final AppPlaceHistoryMapper appPlaceHistoryMapper) {
        GWT.log("Creating kifu editor view");

        boardSettingsPanel = new BoardSettingsPanel(sessionInformation.getUserPreferences());
        databasePanel = new DatabasePanel(appPlaceHistoryMapper, sessionInformation.getUserPreferences());

        saveKifuPanel = new SaveKifuPanel(placeController);
        shogiBoard = new ShogiBoard(KIFU_EDITOR, sessionInformation.getUserPreferences());
        shogiBoard.getBoardConfiguration().setPositionEditingMode(false);

        navigationController = new NavigationController(KIFU_EDITOR);

        gameNavigation = navigationController.getGameNavigation();
        gameNavigator = new GameNavigator(KIFU_EDITOR);

        kifuEditorLeftBarPanel = new KifuEditorLeftBarPanel();
        kifuEditorPanel = new KifuEditorPanel(gameNavigator);

        shogiBoard.setUpperRightPanel(kifuEditorPanel);
        shogiBoard.setLowerLeftPanel(kifuEditorLeftBarPanel);

        gameTreePanel = new GameTreePanel(KIFU_EDITOR, gameNavigation, false,
                sessionInformation.getUserPreferences(), false, false);

        positionEvaluationDetailsPanel = new PositionEvaluationDetailsPanel(shogiBoard,
                sessionInformation);
        positionEvaluationDetailsPanel.setSize("1450px", "300px");

        textArea = createCommentsArea();

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
        textArea.addKeyUpHandler(keyUpEvent -> gameNavigation.getCurrentNode().setComment(textArea.getText()));
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

    public void activate(final EventBus eventBus, final KifuEditorPlace place) {
        GWT.log("Activating problem editor view");
        this.place = place;
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        kifuEditorPanel.activate(eventBus, place);
        gameTreePanel.activate(eventBus);
        kifuEditorLeftBarPanel.activate(eventBus);
        saveKifuPanel.activate(eventBus, place);
        boardSettingsPanel.activate(eventBus);
        databasePanel.activate(eventBus);
        positionEvaluationDetailsPanel.activate(eventBus);
        navigationController.activate(eventBus);
        reset();
    }

    private void reset() {
        if (place.getCollectionId() != null && place.getKifuId() == null) {
            if (place.getType() == KifuDetails.KifuType.PROBLEM) {
                resetForNewProblem();
            } else {
                resetForNewKifu();
            }
        }
    }

    private void resetForNewProblem() {
        Scheduler.get().scheduleDeferred(() -> {
                    loadGameRecord(new GameRecord());
                    kifuEditorLeftBarPanel.resetToProblemMode();
                    eventBus.fireEvent(new PositionChangedEvent(ShogiInitialPositionFactory.createEmptyTsumePosition(false), true));
                }
        );
    }

    private void resetForNewKifu() {
        Scheduler.get().scheduleDeferred(() ->
                loadGameRecord(new GameRecord())
        );
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

    public NavigationController getNavigationController() {
        return navigationController;
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
                eventBus.fireEvent(new EditMovePlayedEvent(new EditMove(shogiBoard.getPosition())));
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
        saveKifuPanel.showInSaveDialog(getGameRecord(), place.getType());
    }

}
