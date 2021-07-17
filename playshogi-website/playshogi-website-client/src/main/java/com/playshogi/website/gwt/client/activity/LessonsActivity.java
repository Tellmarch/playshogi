package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.tutorial.LessonsListEvent;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.place.LessonsPlace;
import com.playshogi.website.gwt.client.ui.LessonsView;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class LessonsActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<LessonsActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);

    private final LessonsView lessonsView;
    private final SessionInformation sessionInformation;

    private EventBus eventBus;

    public LessonsActivity(final LessonsPlace place, final LessonsView lessonsView,
                           final SessionInformation sessionInformation) {
        this.lessonsView = lessonsView;
        this.sessionInformation = sessionInformation;
    }

    private void refreshData() {
        kifuService.getAllPublicLessons(sessionInformation.getSessionId(), new AsyncCallback<LessonDetails[]>() {
            @Override
            public void onFailure(final Throwable throwable) {
                GWT.log("LessonsActivity - RPC failure: getAllLessons " + throwable);
            }

            @Override
            public void onSuccess(final LessonDetails[] lessonDetails) {
                GWT.log("LessonsActivity - RPC success: getAllLessons");
                eventBus.fireEvent(new LessonsListEvent(lessonDetails));
            }
        });
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting lessons activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);

        lessonsView.activate(eventBus);
        containerWidget.setWidget(lessonsView.asWidget());

        refreshData();
    }

    @EventHandler
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        refreshData();
    }
}
