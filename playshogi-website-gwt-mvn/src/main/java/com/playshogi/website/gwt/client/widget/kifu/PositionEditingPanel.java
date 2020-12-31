package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.EditModeSelectedEvent;
import com.playshogi.website.gwt.client.events.kifu.SwitchPlayerToPlayEvent;

public class PositionEditingPanel extends Composite implements ClickHandler {

    interface MyEventBinder extends EventBinder<PositionEditingPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final Button toPlayButton;
    private final Button initialPositionButton;
    private final Button twoKingsButton;
    private final Button oneKingButton;
    private final RadioButton radio1;
    private final RadioButton radio2;
    private EventBus eventBus;

    public PositionEditingPanel() {

        FlowPanel flowPanel = new FlowPanel();

        radio1 = new RadioButton("editmode", "Edit position");
        flowPanel.add(radio1);
        flowPanel.add(new HTML("<br/>"));

        oneKingButton = new Button("Empty board (One King)",
                (ClickHandler) clickEvent -> eventBus.fireEvent(new PositionChangedEvent(ShogiInitialPositionFactory.createEmptyTsumePosition(false),
                        true)));
        flowPanel.add(oneKingButton);

        twoKingsButton = new Button("Empty board (Two Kings)",
                (ClickHandler) clickEvent -> eventBus.fireEvent(new PositionChangedEvent(ShogiInitialPositionFactory.createEmptyTsumePosition(true),
                        true)));
        flowPanel.add(twoKingsButton);

        initialPositionButton = new Button("Initial position",
                (ClickHandler) clickEvent -> eventBus.fireEvent(new PositionChangedEvent(ShogiInitialPositionFactory.createInitialPosition(), true)));
        flowPanel.add(initialPositionButton);

        toPlayButton = new Button("Sente to play",
                (ClickHandler) clickEvent -> eventBus.fireEvent(new SwitchPlayerToPlayEvent()));
        flowPanel.add(toPlayButton);

        radio2 = new RadioButton("editmode", "Input moves");

        radio1.setValue(true);

        radio1.addClickHandler(this);
        radio2.addClickHandler(this);

        flowPanel.add(new HTML("<br/>"));
        flowPanel.add(radio2);

        initWidget(flowPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating PositionEditingPanel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @Override
    public void onClick(final ClickEvent clickEvent) {
        if (radio1.getValue()) {
            GWT.log("Switching to edit mode");
            eventBus.fireEvent(new EditModeSelectedEvent(true));
            oneKingButton.setEnabled(true);
            twoKingsButton.setEnabled(true);
            initialPositionButton.setEnabled(true);
            toPlayButton.setEnabled(true);
        } else if (radio2.getValue()) {
            GWT.log("Switching to play mode");
            eventBus.fireEvent(new EditModeSelectedEvent(false));
            oneKingButton.setEnabled(false);
            twoKingsButton.setEnabled(false);
            initialPositionButton.setEnabled(false);
            toPlayButton.setEnabled(false);
        }
    }

    @EventHandler
    public void onEPositionChangedEvent(final PositionChangedEvent event) {
        GWT.log("PositionEditingPanel: handle PositionChangedEvent");
        if (event.getPosition().getPlayerToMove() == Player.BLACK) {
            toPlayButton.setText("Black (Sente/Shitate) to play");
        } else {
            toPlayButton.setText("White (Gote/Uwate) to play");
        }
    }
}
