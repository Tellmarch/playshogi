package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;

public class PositionEditingPanel extends Composite {

    private EventBus eventBus;

    public PositionEditingPanel() {

        FlowPanel flowPanel = new FlowPanel();

        flowPanel.add(new Button("Empty board",
                (ClickHandler) clickEvent -> eventBus.fireEvent(new PositionChangedEvent(ShogiInitialPositionFactory.createEmptyTsumePosition(),
                        true))));

        flowPanel.add(new Button("Initial position",
                (ClickHandler) clickEvent -> eventBus.fireEvent(new PositionChangedEvent(ShogiInitialPositionFactory.createInitialPosition(), true))));

        initWidget(flowPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating PositionEditingPanel");
        this.eventBus = eventBus;
    }
}
