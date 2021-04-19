package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.widget.kifu.ImportCollectionPanel;

import javax.inject.Singleton;

@Singleton
public class ManageProblemsView extends Composite {


    interface MyEventBinder extends EventBinder<ManageProblemsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;
    private final ImportCollectionPanel importCollectionPanel = new ImportCollectionPanel();

    public ManageProblemsView() {
        Button importButton = new Button("Import collection",
                (ClickHandler) clickEvent -> importCollectionPanel.showInDialog());
        initWidget(importButton);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating ManageProblemsView");
        this.eventBus = eventBus;
        importCollectionPanel.activate(eventBus);
        eventBinder.bindEventHandlers(this, eventBus);
    }
}
