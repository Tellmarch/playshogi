package com.playshogi.website.gwt.client.controller;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.EndOfVariationReachedEvent;
import com.playshogi.website.gwt.client.events.NewVariationPlayedEvent;
import com.playshogi.website.gwt.client.events.UserFinishedProblemEvent;

public class ProblemController {

    interface MyEventBinder extends EventBinder<ProblemController> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    @EventHandler
    public void onNewVariation(final NewVariationPlayedEvent event) {
        GWT.log("Problem controller: handle new variation played event");
        if (event.isPositionCheckmate()) {
            GWT.log("Problem controller: player found alternative checkmate");
            eventBus.fireEvent(new UserFinishedProblemEvent(true));
        } else {
            eventBus.fireEvent(new UserFinishedProblemEvent(false));
        }

    }

    @EventHandler
    public void onEndOfVariation(final EndOfVariationReachedEvent event) {
        GWT.log("Problem feedback: handle end of variation reached event");
        eventBus.fireEvent(new UserFinishedProblemEvent(true));
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating Problem controller");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }
}
