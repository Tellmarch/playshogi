package com.playshogi.website.gwt.client.widget.problems;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.events.puzzles.ProblemNumMovesSelectedEvent;
import com.playshogi.website.gwt.client.util.ElementWidget;
import org.dominokit.domino.ui.cards.Card;
import org.dominokit.domino.ui.chips.Chip;
import org.dominokit.domino.ui.chips.ChipsGroup;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.header.BlockHeader;
import org.dominokit.domino.ui.style.ColorScheme;

public class ProblemOptionsPanel2 extends Composite {


    private ChipsGroup numMovesGroup;

    interface MyEventBinder extends EventBinder<ProblemOptionsPanel2> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    public ProblemOptionsPanel2(int[] moves) {

        Card optionsCard = Card.create("Problems options");

        addNumMovesSelection(moves, optionsCard);

        initWidget(new ElementWidget(optionsCard.element()));
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
        int numMoves = ("All moves".equals(selectedValue)) ? 0 :
                Integer.parseInt(selectedValue.substring(0, 2).trim());
        if (eventBus != null) {
            eventBus.fireEvent(new ProblemNumMovesSelectedEvent(numMoves));
        }
    }


    public void activate(final EventBus eventBus) {
        this.eventBus = null; // To not flood eventbus
        numMovesGroup.selectAt(numMovesGroup.getElementsCount() - 1);
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }
}
