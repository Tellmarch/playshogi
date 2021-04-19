package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;

import javax.inject.Singleton;

@Singleton
public class ManageProblemsView extends Composite {


    interface MyEventBinder extends EventBinder<ManageProblemsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    public ManageProblemsView() {
        initWidget(new HTML("Manage problems"));
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating ManageProblemsView");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }
}
