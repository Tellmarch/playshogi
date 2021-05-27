package com.playshogi.website.gwt.client.widget.engine;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.UserPreferences;
import com.playshogi.website.gwt.client.events.gametree.HighlightMoveEvent;
import com.playshogi.website.gwt.client.events.gametree.InsertVariationEvent;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.PositionEvaluationEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestPositionEvaluationEvent;
import com.playshogi.website.gwt.client.events.user.NotationStyleSelectedEvent;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.shared.models.PositionEvaluationDetails;
import com.playshogi.website.gwt.shared.models.PrincipalVariationDetails;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.loaders.Loader;
import org.dominokit.domino.ui.loaders.LoaderEffect;

import java.util.ArrayList;

public class PositionEvaluationDetailsPanel extends Composite {

    interface MyEventBinder extends EventBinder<PositionEvaluationDetailsPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final CellTable<PrincipalVariationDetails> table;
    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
    private final CheckBox highlightCheckBox;
    private final ShogiBoard shogiBoard;
    private final UserPreferences userPreferences;
    private final Loader loader;

    private EventBus eventBus;
    private PositionEvaluationDetails evaluation;
    private boolean sync = false; // Whether the evaluation matches the current position of the board
    private PrincipalVariationDetails selectedVariation;

    public PositionEvaluationDetailsPanel(final ShogiBoard shogiBoard, final SessionInformation sessionInformation) {
        GWT.log("Creating PositionEvaluationDetailsPanel");
        this.userPreferences = sessionInformation.getUserPreferences();
        this.shogiBoard = shogiBoard;
        VerticalPanel verticalPanel = new VerticalPanel();

        FlowPanel flowPanel = new FlowPanel();

        Button button = Button.createDefault("Position Evaluation (5 seconds)").style().setMarginRight("1em").get();

        loader = Loader.create(button, LoaderEffect.WIN8_LINEAR).setRemoveLoadingText(true);

        button.addClickListener(
                evt -> {
                    if (!sessionInformation.isLoggedIn()) {
                        Window.alert("Only logged in users can use the computer analysis. Please register or log-in.");
                        return;
                    }
                    loader.start();
                    new Timer() {
                        @Override
                        public void run() {
                            loader.stop();
                        }
                    }.schedule(5000);
                    eventBus.fireEvent(new RequestPositionEvaluationEvent());
                });

        flowPanel.add(new ElementWidget(button.element()));

        highlightCheckBox = new CheckBox("Highlight best move");
        highlightCheckBox.addValueChangeHandler(valueChangeEvent -> {
            if (valueChangeEvent.getValue()) {
                highlightBestMove();
            } else {
                clearHighlight();
            }
        });
        flowPanel.add(highlightCheckBox);

        verticalPanel.add(flowPanel);

        table = new CellTable<>();
        table.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);

        TextColumn<PrincipalVariationDetails> depthColumn = new TextColumn<PrincipalVariationDetails>() {
            @Override
            public String getValue(final PrincipalVariationDetails object) {
                return String.valueOf(object.getDepth());
            }
        };
        table.addColumn(depthColumn, "Depth");

        TextColumn<PrincipalVariationDetails> seldepthColumn = new TextColumn<PrincipalVariationDetails>() {
            @Override
            public String getValue(final PrincipalVariationDetails object) {
                return String.valueOf(object.getSeldepth());
            }
        };
        table.addColumn(seldepthColumn, "Selective Depth");

        TextColumn<PrincipalVariationDetails> nodesColumn = new TextColumn<PrincipalVariationDetails>() {
            @Override
            public String getValue(final PrincipalVariationDetails object) {
                return String.valueOf(object.getNodes());
            }
        };
        table.addColumn(nodesColumn, "Nodes");


        TextColumn<PrincipalVariationDetails> scoreColumn = new TextColumn<PrincipalVariationDetails>() {
            @Override
            public String getValue(final PrincipalVariationDetails object) {
                if (object.isForcedMate()) {
                    return "Mate in " + object.getNumMovesBeforeMate();
                } else {
                    return String.valueOf(object.getEvaluationCP());
                }
            }
        };
        table.addColumn(scoreColumn, "Score");

        TextColumn<PrincipalVariationDetails> variationColumn = new TextColumn<PrincipalVariationDetails>() {
            @Override
            public String getValue(final PrincipalVariationDetails details) {
                return getPVStringFromUSF(details.getPrincipalVariation());
            }
        };
        table.addColumn(variationColumn, "Principal variation");

        table.addCellPreviewHandler(event -> {
            if (BrowserEvents.MOUSEDOWN.equals(event.getNativeEvent().getType()) ||
                    BrowserEvents.CLICK.equals(event.getNativeEvent().getType())) {
                selectedVariation = event.getValue();
            } else if (BrowserEvents.MOUSEOVER.equals(event.getNativeEvent().getType())) {
                if (isSync() && evaluation != null && event.getValue().getPrincipalVariation().length() >= 4) {
                    String move = event.getValue().getPrincipalVariation().substring(0, 4);
                    GWT.log("Highlighting move: " + move);
                    eventBus.fireEvent(new HighlightMoveEvent(UsfMoveConverter.fromUsfString(move,
                            shogiBoard.getPosition())));
                }
            } else if (BrowserEvents.MOUSEOUT.equals(event.getNativeEvent().getType())) {
                if (isSync() && evaluation != null) {
                    clearHighlight();
                    highlightBestMove();
                }
            }
        });

        PopupPanel contextMenu = createContextMenu();
        table.sinkEvents(Event.ONCONTEXTMENU);
        table.addHandler(event -> {
            event.preventDefault();
            event.stopPropagation();
            contextMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
            contextMenu.show();
        }, ContextMenuEvent.getType());

        verticalPanel.add(table);

        initWidget(verticalPanel);
    }

    private PopupPanel createContextMenu() {
        final PopupPanel contextMenu;
        contextMenu = new PopupPanel(true);
        MenuBar menuBar = new MenuBar(true);
        MenuItem addVariation = new MenuItem("Add Variation", () -> {
            if (isSync() && selectedVariation != null) {
                GWT.log("Add variation: " + selectedVariation);
                eventBus.fireEvent(new InsertVariationEvent(selectedVariation));
            }
            contextMenu.hide();
        });
        menuBar.addItem(addVariation);

        contextMenu.add(menuBar);
        contextMenu.hide();
        return contextMenu;
    }

    private String getPVStringFromUSF(final String usfPvString) {
        String[] usfMoves = usfPvString.trim().split(" ");
        ShogiPosition position = SfenConverter.fromSFEN(evaluation.getSfen());
        String result = "";
        ShogiMove previousMove = null;
        for (String usfMove : usfMoves) {
            ShogiMove move = UsfMoveConverter.fromUsfString(usfMove, position);
            shogiRulesEngine.playMoveInPosition(position, move);
            result += userPreferences.getMoveNotationAccordingToPreferences(move, previousMove, true) + " ";
            previousMove = move;
        }

        return result;
    }

    @EventHandler
    public void onPositionEvaluationEvent(final PositionEvaluationEvent event) {
        GWT.log("PositionEvaluationDetailsPanel: handle PositionEvaluationEvent");
        if (!event.getEvaluation().getSfen().equals(SfenConverter.toSFEN(shogiBoard.getPosition()))) {
            GWT.log("PositionEvaluationDetailsPanel PositionEvaluationEvent: Mismatch position!\n"
                    + event.getEvaluation().getSfen() + "\n" + SfenConverter.toSFEN(shogiBoard.getPosition()));
            setSync(false);
            return;
        } else {
            setSync(true);
        }
        loader.stop();
        evaluation = event.getEvaluation();
        showEvaluation();
        highlightBestMove();
        selectedVariation = null;
    }

    private void showEvaluation() {
        if (evaluation != null) {
            PrincipalVariationDetails[] principalVariationHistory = evaluation.getPrincipalVariationHistory();
            table.setRowCount(principalVariationHistory.length);
            ArrayList<PrincipalVariationDetails> list = new ArrayList<>(principalVariationHistory.length);
            for (int i = principalVariationHistory.length - 1; i >= 0; i--) {
                list.add(principalVariationHistory[i]);
            }
            table.setRowData(0, list);
            table.setVisible(true);
        }
    }

    private void highlightBestMove() {
        if (isSync() && evaluation != null) {
            String bestMove = evaluation.getBestMove();
            if (bestMove != null && highlightCheckBox.getValue()) {
                eventBus.fireEvent(new HighlightMoveEvent(UsfMoveConverter.fromUsfString(bestMove,
                        shogiBoard.getPosition())));
            }
        }
    }

    @EventHandler
    public void onPositionChangedEvent(final PositionChangedEvent event) {
        GWT.log("PositionEvaluationDetailsPanel: handle PositionChangedEvent");
        setSync(false);
        selectedVariation = null;
    }

    @EventHandler
    public void onNotationStyleSelected(final NotationStyleSelectedEvent event) {
        GWT.log("PositionEvaluationDetailsPanel: handle NotationStyleSelectedEvent");
        showEvaluation();
    }

    private void setSync(final boolean sync) {
        this.sync = sync;
        if (!sync) {
            clearHighlight();
        }
    }

    private void clearHighlight() {
        eventBus.fireEvent(new HighlightMoveEvent(null));
    }

    private boolean isSync() {
        return sync;
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating PositionEvaluationDetailsPanel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        table.setVisible(false);
    }
}
