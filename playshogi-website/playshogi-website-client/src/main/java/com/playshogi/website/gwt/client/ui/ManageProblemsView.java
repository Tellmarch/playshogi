package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.util.FireAndForgetCallback;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ManageProblemsView extends Composite {

    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);

    interface MyEventBinder extends EventBinder<ManageProblemsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    @Inject
    public ManageProblemsView(final SessionInformation sessionInformation) {
        Button byDifficulty = new Button("Create collections by difficulty");
        byDifficulty.addClickHandler(clickEvent ->
                problemsService.createCollectionsByDifficulty(sessionInformation.getSessionId(),
                        new FireAndForgetCallback("createCollectionsByDifficulty")));
        initWidget(byDifficulty);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating ManageProblemsView");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }
}
