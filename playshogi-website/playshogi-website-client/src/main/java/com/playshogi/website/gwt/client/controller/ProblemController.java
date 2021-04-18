package com.playshogi.website.gwt.client.controller;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.gametree.EndOfVariationReachedEvent;
import com.playshogi.website.gwt.client.events.gametree.NewVariationPlayedEvent;
import com.playshogi.website.gwt.client.events.puzzles.UserFinishedProblemEvent;

public class ProblemController {

    interface MyEventBinder extends EventBinder<ProblemController> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    private String problemId = null;

    @EventHandler
    public void onNewVariation(final NewVariationPlayedEvent event) {
        GWT.log("Problem controller: handle new variation played event");
        if (event.isPositionCheckmate()) {
            GWT.log("Problem controller: player found alternative checkmate");
            eventBus.fireEvent(new UserFinishedProblemEvent(true, problemId));
        } else {
            eventBus.fireEvent(new UserFinishedProblemEvent(false, problemId));
        }

    }

    @EventHandler
    public void onEndOfVariation(final EndOfVariationReachedEvent event) {
        GWT.log("ProblemController: handle end of variation reached event");
        eventBus.fireEvent(new UserFinishedProblemEvent(event.isMainLine(), problemId));
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating Problem controller");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    public void setProblemId(String problemId) {
        GWT.log("ProblemController: setting problemId to " + problemId);
        this.problemId = problemId;
    }
}
