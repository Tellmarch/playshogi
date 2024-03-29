package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.shogivariant.Handicap;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.EditModeSelectedEvent;
import com.playshogi.website.gwt.client.events.kifu.SwitchPlayerToPlayEvent;
import com.playshogi.website.gwt.client.i18n.PlayMessages;
import com.playshogi.website.gwt.client.util.ElementWidget;
import org.dominokit.domino.ui.forms.SwitchButton;
import org.dominokit.domino.ui.themes.Theme;

public class PositionEditingPanel extends Composite {

    interface MyEventBinder extends EventBinder<PositionEditingPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);
    private final PlayMessages messages = com.google.gwt.core.client.GWT.create(PlayMessages.class);

    private final ImportPositionPanel importPositionPanel = new ImportPositionPanel();

    private final Button toPlayButton;
    private final Button importPositionButton;
    private final ListBox presets;
    private final SwitchButton switchButton;
    private EventBus eventBus;

    public PositionEditingPanel() {

        FlowPanel flowPanel = new FlowPanel();

        // flowPanel.add(new HTML("Mouse Wheel = rotate")); // todo add instruction to how to rotate pieces

        switchButton = SwitchButton.create();
        flowPanel.add(new ElementWidget(switchButton
                .setOffTitle("Play").setOnTitle("Edit").setColor(Theme.DEEP_PURPLE.color())
                .addChangeHandler(b -> {
                    eventBus.fireEvent(new EditModeSelectedEvent(b));
                    setEditingButtonsEnabled(b);
                }).element()));

        presets = createPresetsList();
        flowPanel.add(presets);

        importPositionButton = new Button("Import position",
                (ClickHandler) clickEvent -> {
                    GWT.log("Position editing panel: Opening the import position box");
                    importPositionPanel.showInDialog(null);
                });
        flowPanel.add(importPositionButton);


        toPlayButton = new Button("Sente to play",
                (ClickHandler) clickEvent -> eventBus.fireEvent(new SwitchPlayerToPlayEvent()));
        flowPanel.add(toPlayButton);

        setEditingButtonsEnabled(false);

        initWidget(flowPanel);
    }

    private ListBox createPresetsList() {
        final ListBox presets;
        presets = new ListBox();

        presets.addItem("Template", "");
        presets.addItem("----------------", "");
        presets.addItem("Empty board (One King)", "ONE_KING");
        presets.addItem("Empty board (Two Kings)", "TWO_KINGS");
        presets.addItem("----------------", "");
        presets.addItem("Initial position: even game", Handicap.EVEN.name());
        presets.addItem("Handicap: " + messages.handicapLance(), Handicap.LANCE.name());
        presets.addItem("Handicap: " + messages.handicapBishop(), Handicap.BISHOP.name());
        presets.addItem("Handicap: " + messages.handicapRook(), Handicap.ROOK.name());
        presets.addItem("Handicap: " + messages.handicapRookLance(), Handicap.ROOK_LANCE.name());
        presets.addItem("Handicap: " + messages.handicapTwoPieces(), Handicap.TWO_PIECES.name());
        presets.addItem("Handicap: " + messages.handicapFourPieces(), Handicap.FOUR_PIECES.name());
        presets.addItem("Handicap: " + messages.handicapSixPieces(), Handicap.SIX_PIECES.name());
        presets.addItem("Handicap: " + messages.handicapEightPieces(), Handicap.EIGHT_PIECES.name());
        presets.addItem("Handicap: " + messages.handicapNinePieces(), Handicap.NINE_PIECES.name());
        presets.addItem("Handicap: " + messages.handicapTenPieces(), Handicap.TEN_PIECES.name());
        presets.addItem("Handicap: " + messages.handicapThreePawns(), Handicap.THREE_PAWNS.name());
        presets.addItem("Handicap: " + messages.handicapNakedKing(), Handicap.NAKED_KING.name());

        presets.setVisibleItemCount(1);

        presets.addChangeHandler(changeEvent -> {
            String value = presets.getSelectedValue();
            GWT.log(value);
            if ("ONE_KING".equals(value)) {
                eventBus.fireEvent(new PositionChangedEvent(ShogiInitialPositionFactory.createEmptyTsumePosition(false), true));
            } else if ("TWO_KINGS".equals(value)) {
                eventBus.fireEvent(new PositionChangedEvent(ShogiInitialPositionFactory.createEmptyTsumePosition(true), true));
            } else if (!value.isEmpty()) {
                eventBus.fireEvent(new PositionChangedEvent(ShogiInitialPositionFactory.createInitialPosition(Handicap.valueOf(value)), true));
            }
        });
        return presets;
    }

    private void setEditingButtonsEnabled(final boolean editMode) {
        presets.setEnabled(editMode);
        toPlayButton.setEnabled(editMode);
        importPositionButton.setEnabled(editMode);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating PositionEditingPanel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        importPositionPanel.activate(eventBus);
    }

    public void resetToProblemMode() {
        eventBus.fireEvent(new EditModeSelectedEvent(true));
        setEditingButtonsEnabled(true);
        switchButton.check();
    }

    @EventHandler
    public void onPositionChangedEvent(final PositionChangedEvent event) {
        GWT.log("PositionEditingPanel: handle PositionChangedEvent");
        if (event.getPosition().getPlayerToMove() == Player.BLACK) {
            toPlayButton.setText("Black to play");
        } else {
            toPlayButton.setText("White to play");
        }
    }
}
