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
import com.playshogi.website.gwt.client.events.kifu.PositionEvaluationEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestPositionEvaluationEvent;
import com.playshogi.website.gwt.shared.models.PrincipalVariationDetails;

import java.util.ArrayList;

public class PositionEvaluationDetailsPanel extends Composite {

    interface MyEventBinder extends EventBinder<PositionEvaluationDetailsPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    private final CellTable<PrincipalVariationDetails> table;

    public PositionEvaluationDetailsPanel() {
        GWT.log("Creating PositionEvaluationDetailsPanel");
        VerticalPanel verticalPanel = new VerticalPanel();

        Button evaluateButton = new Button("Evaluate");
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
            public String getValue(final PrincipalVariationDetails object) {
                return String.valueOf(object.getPrincipalVariation());
            }
        };
        table.addColumn(variationColumn, "Principal variation");

        verticalPanel.add(table);

        initWidget(verticalPanel);
    }

    @EventHandler
    public void onPositionEvaluationEvent(final PositionEvaluationEvent event) {
        GWT.log("PositionEvaluationDetailsPanel: handle PositionEvaluationEvent");
        PrincipalVariationDetails[] principalVariationHistory = event.getEvaluation().getPrincipalVariationHistory();
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
