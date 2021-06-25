package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.place.FreeBoardPlace;
import com.playshogi.website.gwt.client.ui.FreeBoardView;

public class FreeBoardActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<FreeBoardActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final String boardId;
    private final FreeBoardView freeBoardView;

    public FreeBoardActivity(final FreeBoardPlace place, final FreeBoardView freeBoardView) {
        this.freeBoardView = freeBoardView;
        this.boardId = place.getBoardId();
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting free board activity");
        eventBinder.bindEventHandlers(this, eventBus);
        freeBoardView.activate(eventBus);
        containerWidget.setWidget(freeBoardView.asWidget());
        Scheduler.get().scheduleDeferred(() -> eventBus.fireEvent(
                new PositionChangedEvent(ShogiInitialPositionFactory.createInitialPosition(), false)));
    }

    @Override
    public void onStop() {
        GWT.log("Stopping free board activity");
        super.onStop();
    }

}