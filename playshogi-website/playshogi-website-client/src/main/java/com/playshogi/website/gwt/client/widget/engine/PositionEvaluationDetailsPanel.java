package com.playshogi.website.gwt.client.widget.engine;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.kif.KifMoveConverter;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.HighlightMoveEvent;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.PositionEvaluationEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestPositionEvaluationEvent;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.shared.models.PositionEvaluationDetails;
import com.playshogi.website.gwt.shared.models.PrincipalVariationDetails;

import java.util.ArrayList;

public class PositionEvaluationDetailsPanel extends Composite {

    interface MyEventBinder extends EventBinder<PositionEvaluationDetailsPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final CellTable<PrincipalVariationDetails> table;
    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
    private final CheckBox highlightCheckBox;
    private final ShogiBoard shogiBoard;

    private EventBus eventBus;
    private PositionEvaluationDetails evaluation;
    private boolean sync = false; // Whether the evaluation matches the current position of the board

    public PositionEvaluationDetailsPanel(final ShogiBoard shogiBoard) {
        GWT.log("Creating PositionEvaluationDetailsPanel");
        this.shogiBoard = shogiBoard;
        VerticalPanel verticalPanel = new VerticalPanel();

        FlowPanel flowPanel = new FlowPanel();

        Button evaluateButton = new Button("Position Evaluation (5 seconds)");
        evaluateButton.addClickHandler(clickEvent -> eventBus.fireEvent(new RequestPositionEvaluationEvent()));

        flowPanel.add(evaluateButton);
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

        verticalPanel.add(table);

        initWidget(verticalPanel);
    }

    private String getPVStringFromUSF(final String usfPvString) {
        String[] usfMoves = usfPvString.trim().split(" ");
        ShogiPosition position = SfenConverter.fromSFEN(evaluation.getSfen());
        String result = "";
        ShogiMove previousMove = null;
        for (String usfMove : usfMoves) {
            ShogiMove move = UsfMoveConverter.fromUsfString(usfMove, position);
            shogiRulesEngine.playMoveInPosition(position, move);
            result += KifMoveConverter.toKifStringShort(move, previousMove) + " ";
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
        evaluation = event.getEvaluation();
        PrincipalVariationDetails[] principalVariationHistory = evaluation.getPrincipalVariationHistory();
        table.setRowCount(principalVariationHistory.length);
        ArrayList<PrincipalVariationDetails> list = new ArrayList<>(principalVariationHistory.length);
        for (int i = principalVariationHistory.length - 1; i >= 0; i--) {
            list.add(principalVariationHistory[i]);
        }
        table.setRowData(0, list);
        table.setVisible(true);
        highlightBestMove();
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
