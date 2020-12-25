package com.playshogi.website.gwt.client.widget.engine;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.kif.KifMoveConverter;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.kifu.PositionEvaluationEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestPositionEvaluationEvent;
import com.playshogi.website.gwt.shared.models.PositionEvaluationDetails;
import com.playshogi.website.gwt.shared.models.PrincipalVariationDetails;

import java.util.ArrayList;

public class PositionEvaluationDetailsPanel extends Composite {

    interface MyEventBinder extends EventBinder<PositionEvaluationDetailsPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final CellTable<PrincipalVariationDetails> table;
    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();

    private EventBus eventBus;

    private PositionEvaluationDetails evaluation;

    public PositionEvaluationDetailsPanel() {
        GWT.log("Creating PositionEvaluationDetailsPanel");
        VerticalPanel verticalPanel = new VerticalPanel();

        Button evaluateButton = new Button("Position Evaluation (5 seconds)");
        evaluateButton.addClickHandler(clickEvent -> eventBus.fireEvent(new RequestPositionEvaluationEvent()));

        verticalPanel.add(evaluateButton);

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
                String pvString = details.getPrincipalVariation();
                String[] usfMoves = pvString.trim().split(" ");
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
        };
        table.addColumn(variationColumn, "Principal variation");

        verticalPanel.add(table);

        initWidget(verticalPanel);
    }

    @EventHandler
    public void onPositionEvaluationEvent(final PositionEvaluationEvent event) {
        GWT.log("PositionEvaluationDetailsPanel: handle PositionEvaluationEvent");
        evaluation = event.getEvaluation();
        PrincipalVariationDetails[] principalVariationHistory = evaluation.getPrincipalVariationHistory();
        table.setRowCount(principalVariationHistory.length);
        ArrayList<PrincipalVariationDetails> list = new ArrayList<>(principalVariationHistory.length);
        for (int i = principalVariationHistory.length - 1; i >= 0; i--) {
            list.add(principalVariationHistory[i]);
        }
        table.setRowData(0, list);
        table.setVisible(true);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating PositionEvaluationDetailsPanel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        table.setVisible(false);
    }
}
