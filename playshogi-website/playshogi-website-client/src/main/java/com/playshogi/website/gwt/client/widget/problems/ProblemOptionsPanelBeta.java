package com.playshogi.website.gwt.client.widget.problems;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.events.puzzles.ProblemNumMovesSelectedEvent;
import com.playshogi.website.gwt.client.events.puzzles.ProblemTypesSelectedEvent;
import com.playshogi.website.gwt.client.events.puzzles.ProblemsOrderSelectedEvent;
import com.playshogi.website.gwt.client.util.ElementWidget;
import org.dominokit.domino.ui.cards.Card;
import org.dominokit.domino.ui.chips.Chip;
import org.dominokit.domino.ui.chips.ChipsGroup;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.header.BlockHeader;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.notifications.Notification;
import org.dominokit.domino.ui.style.ColorScheme;

import static org.jboss.elemento.Elements.br;

public class ProblemOptionsPanelBeta extends Composite {

    private Chip tsume;
    private Chip twoKings;
    private Chip hisshi;
    private Chip realGameTsume;

    private boolean tsumeSelected = false;
    private boolean twoKingsSelected = false;
    private boolean hisshiSelected = false;
    private boolean realGameTsumeSelected = false;
    private ChipsGroup numMovesGroup;
    private ChipsGroup orderGroup;

    interface MyEventBinder extends EventBinder<ProblemOptionsPanelBeta> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    public ProblemOptionsPanelBeta(int[] moves) {

        Card optionsCard = Card.create("Problems options");

        addProblemTypeSelection(optionsCard);
        addNumMovesSelection(moves, optionsCard);
        addProblemsOrderSelection(optionsCard);


        initWidget(new ElementWidget(optionsCard.element()));
    }

    private void addProblemTypeSelection(final Card selectableChipsCard) {
        selectableChipsCard.appendChild(BlockHeader.create("Type of Problems"));

        tsume = Chip.create("Tsume").setSelectable(true).setColorScheme(ColorScheme.GREY);
        tsume.addSelectionHandler(value -> {
            tsume.setLeftIcon(Icons.ALL.check());
            tsumeSelected = true;
            handleTypeSelection();
        });
        tsume.addDeselectionHandler(() -> {
            tsume.removeLeftAddon();
            tsumeSelected = false;
            handleTypeSelection();
        });
        twoKings = Chip.create("Two-kings Tsume").setSelectable(true).setColorScheme(ColorScheme.GREY);
        twoKings.addSelectionHandler(value -> {
            twoKings.setLeftIcon(Icons.ALL.check());
            twoKingsSelected = true;
            handleTypeSelection();
        });
        twoKings.addDeselectionHandler(() -> {
            twoKings.removeLeftAddon();
            twoKingsSelected = false;
            handleTypeSelection();
        });
        hisshi = Chip.create("Hisshi").setSelectable(true).setColorScheme(ColorScheme.GREY);
        hisshi.addSelectionHandler(value -> {
            hisshi.setLeftIcon(Icons.ALL.check());
            hisshiSelected = true;
            handleTypeSelection();
        });
        hisshi.addDeselectionHandler(() -> {
            hisshi.removeLeftAddon();
            hisshiSelected = false;
            handleTypeSelection();
        });
        realGameTsume = Chip.create("Real game Tsume").setSelectable(true).setColorScheme(ColorScheme.GREY);
        realGameTsume.addSelectionHandler(value -> {
            realGameTsume.setLeftIcon(Icons.ALL.check());
            realGameTsumeSelected = true;
            handleTypeSelection();
        });
        realGameTsume.addDeselectionHandler(() -> {
            realGameTsume.removeLeftAddon();
            realGameTsumeSelected = false;
            handleTypeSelection();
        });
        selectableChipsCard.appendChild(
                Row.create()
                        .addColumn(
                                Column.span12()
                                        .appendChild(tsume)
                                        .appendChild(twoKings)
                                        .appendChild(hisshi)
                                        .appendChild(realGameTsume)));

        tsume.select();

        selectableChipsCard.appendChild(br());
    }

    private void handleTypeSelection() {
        ProblemTypesSelectedEvent event = new ProblemTypesSelectedEvent(tsumeSelected,
                twoKingsSelected, hisshiSelected, realGameTsumeSelected);
        if (eventBus != null) {
            eventBus.fireEvent(event);
        }
    }

    private void addNumMovesSelection(final int[] moves, final Card selectableChipsCard) {
        selectableChipsCard.appendChild(BlockHeader.create("Number of moves"));

        numMovesGroup = ChipsGroup.create();
        for (int n : moves) {
            numMovesGroup.appendChild(Chip.create(n + " moves"));
        }

        numMovesGroup.appendChild(Chip.create("All moves"));
        numMovesGroup.setColorScheme(ColorScheme.TEAL).selectAt(0);
        numMovesGroup.addSelectionHandler(value -> handleNumMovesSelection());
        selectableChipsCard.appendChild(Row.create().addColumn(Column.span12().appendChild(numMovesGroup)));
    }

    private void handleNumMovesSelection() {
        String selectedValue = numMovesGroup.getSelectedChip().getValue();
        Notification.createInfo(
                "Number of moves [ "
                        + selectedValue
                        + " ] is selected")
                .show();
        int numMoves = ("All moves".equals(selectedValue)) ? 0 :
                Integer.parseInt(selectedValue.substring(0, 2).trim());
        if (eventBus != null) {
            eventBus.fireEvent(new ProblemNumMovesSelectedEvent(numMoves));
        }
    }

    private void addProblemsOrderSelection(final Card selectableChipsCard) {
        selectableChipsCard.appendChild(BlockHeader.create("Problems order"));

        orderGroup = ChipsGroup.create();
        orderGroup.appendChild(Chip.create("Random"));
        orderGroup.appendChild(Chip.create("Linear"));

        orderGroup.setColorScheme(ColorScheme.BLUE).selectAt(0);
        orderGroup.addSelectionHandler(value -> handleProblemsOrderSelection());
        selectableChipsCard.appendChild(Row.create().addColumn(Column.span12().appendChild(orderGroup)));
    }

    private void handleProblemsOrderSelection() {
        Notification.createInfo(
                "Order [ "
                        + orderGroup.getSelectedChip().getValue()
                        + " ] is selected")
                .show();
        if (eventBus != null) {
            eventBus.fireEvent(new ProblemsOrderSelectedEvent("Random".equals(orderGroup.getSelectedChip().getValue())));
        }
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = null; // To not flood eventbus
        tsume.select();
        twoKings.deselect();
        hisshi.deselect();
        realGameTsume.deselect();
        numMovesGroup.selectAt(numMovesGroup.getElementsCount() - 1);
        orderGroup.selectAt(0);
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }
}
