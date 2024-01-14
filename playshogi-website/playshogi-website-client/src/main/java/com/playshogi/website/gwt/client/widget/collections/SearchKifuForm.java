package com.playshogi.website.gwt.client.widget.collections;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.record.GameResult;
import com.playshogi.website.gwt.client.events.collections.SearchKifusEvent;
import elemental2.dom.HTMLDivElement;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.forms.*;
import org.dominokit.domino.ui.modals.ModalDialog;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.List;

public class SearchKifuForm {


    LocalSuggestBoxStore<String> playerNames = LocalSuggestBoxStore.create();
    private HtmlContentBuilder<HTMLDivElement> div = null;

    private Select<GameResult> gameResult;
    private SuggestBox<String> playerName;
    private EventBus eventBus;

    public HtmlContentBuilder<HTMLDivElement> getForm() {
        if (div != null) {
            return div;
        }

        playerName = SuggestBox.create("Player:", playerNames)
                .setHelperText("Type any letter and see suggestions");

        gameResult = Select.<GameResult>create("Game Result")
                .appendChild(SelectOption.create(null, "Any result"))
                .appendChild(SelectOption.create(GameResult.BLACK_WIN, "Black wins"))
                .appendChild(SelectOption.create(GameResult.WHITE_WIN, "White wins"))
                .appendChild(SelectOption.create(GameResult.OTHER, "Other"))
                .appendChild(SelectOption.create(GameResult.UNKNOWN, "Unknown"))
                .setSearchable(false)
                .selectAt(0);

        div = Elements.div().add(playerName).add(gameResult);

        return div;
    }


    public void updatePlayerNames(final List<String> names) {
        playerNames.getSuggestions().clear();
        for (String name : names) {
            playerNames.addSuggestion(SuggestItem.create(name));
        }
    }

    public void showInPopup() {
        ModalDialog modal = ModalDialog.create("Search for Kifus").setAutoClose(false);
        modal.appendChild(getForm());
        Button closeButton = Button.create("CANCEL").linkify();
        closeButton.addClickListener(evt -> modal.close());
        Button searchButton = Button.create("SEARCH").linkify();
        searchButton.addClickListener(evt -> {
            eventBus.fireEvent(new SearchKifusEvent(gameResult.getValue(), playerName.getValue()));
            modal.close();
        });
        modal.appendFooterChild(searchButton);
        modal.appendFooterChild(closeButton);
        modal.open();
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
