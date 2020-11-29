package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.EditModeSelectedEvent;

public class PositionEditingPanel extends Composite implements ClickHandler {

    private final RadioButton radio1;
    private final RadioButton radio2;
    private EventBus eventBus;

    public PositionEditingPanel() {

        FlowPanel flowPanel = new FlowPanel();

        flowPanel.add(new Button("Empty board (One King)",
                (ClickHandler) clickEvent -> eventBus.fireEvent(new PositionChangedEvent(ShogiInitialPositionFactory.createEmptyTsumePosition(false),
                        true))));

        flowPanel.add(new Button("Empty board (Two Kings)",
                (ClickHandler) clickEvent -> eventBus.fireEvent(new PositionChangedEvent(ShogiInitialPositionFactory.createEmptyTsumePosition(true),
                        true))));

        flowPanel.add(new Button("Initial position",
                (ClickHandler) clickEvent -> eventBus.fireEvent(new PositionChangedEvent(ShogiInitialPositionFactory.createInitialPosition(), true))));

        radio1 = new RadioButton("editmode", "Edit position");
        radio2 = new RadioButton("editmode", "Input moves");

        radio1.setValue(true);

        radio1.addClickHandler(this);
        radio2.addClickHandler(this);

        flowPanel.add(new HTML("<br/>"));
        flowPanel.add(radio1);
        flowPanel.add(new HTML("<br/>"));
        flowPanel.add(radio2);

        initWidget(flowPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating PositionEditingPanel");
        this.eventBus = eventBus;
    }

    @Override
    public void onClick(final ClickEvent clickEvent) {
        if (radio1.getValue()) {
            GWT.log("Switching to edit mode");
            eventBus.fireEvent(new EditModeSelectedEvent(true));
        } else if (radio2.getValue()) {
            GWT.log("Switching to play mode");
            eventBus.fireEvent(new EditModeSelectedEvent(false));
        }
    }
}
