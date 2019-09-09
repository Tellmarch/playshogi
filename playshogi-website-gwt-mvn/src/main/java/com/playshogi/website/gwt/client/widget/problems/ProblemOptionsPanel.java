package com.playshogi.website.gwt.client.widget.problems;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.events.ProblemNumMovesSelectedEvent;

public class ProblemOptionsPanel extends Composite implements ClickHandler {

    interface MyEventBinder extends EventBinder<ProblemOptionsPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    public ProblemOptionsPanel(int[] moves) {

        FlowPanel verticalPanel = new FlowPanel();
        for (int i : moves) {
            RadioButton button = new RadioButton("moves", i + " moves");
            button.addClickHandler(this);
            verticalPanel.add(button);
        }

        RadioButton allButton = new RadioButton("moves", "All");
        allButton.addClickHandler(this);
        allButton.setValue(true);
        verticalPanel.add(allButton);

        initWidget(verticalPanel);
    }

    @Override
    public void onClick(final ClickEvent event) {
        Object source = event.getSource();
        String text = ((RadioButton) source).getText();
        GWT.log("CLICK: " + text);
        int numMoves = ("All".equals(text)) ? 0 : Integer.parseInt(text.substring(0, 2).trim());
        eventBus.fireEvent(new ProblemNumMovesSelectedEvent(numMoves));
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating Problem feedback panel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }
}
