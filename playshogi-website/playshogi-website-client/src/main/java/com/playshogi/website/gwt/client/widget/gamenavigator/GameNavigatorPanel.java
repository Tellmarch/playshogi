package com.playshogi.website.gwt.client.widget.gamenavigator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.events.gametree.NavigateBackEvent;
import com.playshogi.website.gwt.client.events.gametree.NavigateForwardEvent;
import com.playshogi.website.gwt.client.events.gametree.NavigateToEndEvent;
import com.playshogi.website.gwt.client.events.gametree.NavigateToStartEvent;

public class GameNavigatorPanel extends Composite implements ClickHandler {

    interface MyEventBinder extends EventBinder<GameNavigatorPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final Button firstButton;
    private final Button previousButton;
    private final Button nextButton;
    private final Button lastButton;

    private EventBus eventBus;

    private final String activityId;

    public GameNavigatorPanel(final String activityId) {
        GWT.log(activityId + ": Creating game navigator");

        this.activityId = activityId;

        firstButton = new Button("<<");
        previousButton = new Button("<");
        nextButton = new Button(">");
        lastButton = new Button(">>");

        firstButton.addClickHandler(this);
        previousButton.addClickHandler(this);
        nextButton.addClickHandler(this);
        lastButton.addClickHandler(this);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(firstButton);
        horizontalPanel.add(previousButton);
        horizontalPanel.add(nextButton);
        horizontalPanel.add(lastButton);

        initWidget(horizontalPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log(activityId + ": Activating Game Navigator");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, this.eventBus);
    }

    @Override
    public void onClick(final ClickEvent event) {
        Object source = event.getSource();
        if (source == firstButton) {
            eventBus.fireEvent(new NavigateToStartEvent());
        } else if (source == nextButton) {
            eventBus.fireEvent(new NavigateForwardEvent());
        } else if (source == previousButton) {
            eventBus.fireEvent(new NavigateBackEvent());
        } else if (source == lastButton) {
            eventBus.fireEvent(new NavigateToEndEvent());
        }
    }
}
