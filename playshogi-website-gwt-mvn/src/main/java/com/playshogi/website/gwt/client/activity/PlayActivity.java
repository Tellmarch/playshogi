package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.place.PlayPlace;
import com.playshogi.website.gwt.client.ui.PlayView;

public class PlayActivity extends MyAbstractActivity {

    private final PlayView playView;

    interface MyEventBinder extends EventBinder<PlayActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    public PlayActivity(final PlayPlace place, final PlayView playView) {
        this.playView = playView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting play activity");
        eventBinder.bindEventHandlers(this, eventBus);
        playView.activate(eventBus);
        containerWidget.setWidget(playView.asWidget());
    }

    @Override
    public void onStop() {
        GWT.log("Stopping play activity");
        super.onStop();
    }

    @EventHandler
    public void onPositionChanged(final PositionChangedEvent event) {
        GWT.log("PLAY - POSITION CHANGED EVENT - " + event.isTriggeredByUser());

        if (event.isTriggeredByUser()) {

        }

    }
}
