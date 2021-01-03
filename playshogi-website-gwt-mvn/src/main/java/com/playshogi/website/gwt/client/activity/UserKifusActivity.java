package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListKifusEvent;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.place.UserKifusPlace;
import com.playshogi.website.gwt.client.ui.UserKifusView;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class UserKifusActivity extends MyAbstractActivity {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private EventBus eventBus;

    interface MyEventBinder extends EventBinder<UserKifusActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final UserKifusPlace place;
    private final UserKifusView view;
    private final SessionInformation sessionInformation;

    public UserKifusActivity(final UserKifusPlace place, final UserKifusView view,
                             final SessionInformation sessionInformation) {
        this.place = place;
        this.view = view;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting user kifus activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        view.activate(eventBus);

        fetchData();

        containerWidget.setWidget(view.asWidget());
    }

    private void fetchData() {
        kifuService.getUserKifus(sessionInformation.getSessionId(), sessionInformation.getUsername(),
                new AsyncCallback<KifuDetails[]>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("UserKifusActivity: error retrieving kifus list");
                    }

                    @Override
                    public void onSuccess(final KifuDetails[] kifuDetails) {
                        GWT.log("UserKifusActivity: retrieved kifus list");
                        eventBus.fireEvent(new ListKifusEvent(kifuDetails));
                    }
                });
    }

    private void refresh() {
        fetchData();
    }

    @EventHandler
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        refresh();
    }

}
