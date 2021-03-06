package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListKifusEvent;
import com.playshogi.website.gwt.client.events.tutorial.LessonsListEvent;
import com.playshogi.website.gwt.client.events.tutorial.SaveLessonDetailsEvent;
import com.playshogi.website.gwt.client.place.ManageLessonsPlace;
import com.playshogi.website.gwt.client.ui.ManageLessonsView;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class ManageLessonsActivity extends MyAbstractActivity {


    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private EventBus eventBus;

    interface MyEventBinder extends EventBinder<ManageLessonsActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ManageLessonsPlace place;
    private final ManageLessonsView view;
    private final SessionInformation sessionInformation;

    public ManageLessonsActivity(final ManageLessonsPlace place, final ManageLessonsView view,
                                 final SessionInformation sessionInformation) {
        this.place = place;
        this.view = view;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting ManageLessonsActivity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);

        view.activate(eventBus);
        containerWidget.setWidget(view.asWidget());

        fetchData();
    }

    private void fetchData() {
        kifuService.getAllLessons(sessionInformation.getSessionId(), new AsyncCallback<LessonDetails[]>() {
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

        kifuService.getLessonKifus(sessionInformation.getSessionId(), sessionInformation.getUsername(),
                new AsyncCallback<KifuDetails[]>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("LessonsActivity: error retrieving kifus list");
                    }

                    @Override
                    public void onSuccess(final KifuDetails[] kifuDetails) {
                        GWT.log("LessonsActivity: retrieved kifus list");
                        eventBus.fireEvent(new ListKifusEvent(kifuDetails));
                    }
                });
    }

    @EventHandler
    public void onSaveLessonDetails(final SaveLessonDetailsEvent event) {
        GWT.log("ManageLessonsView: handle SaveLessonDetailsEvent: " + event.getDetails());

        String lessonId = event.getDetails().getLessonId();
        if (lessonId == null || "".equals(lessonId)) {
            kifuService.createLesson(sessionInformation.getSessionId(), event.getDetails(), new AsyncCallback<Void>() {
                @Override
                public void onFailure(final Throwable throwable) {
                    GWT.log("LessonsActivity - RPC failure: createLesson " + throwable);
                }

                @Override
                public void onSuccess(final Void unused) {
                    GWT.log("LessonsActivity - RPC success: createLesson ");
                    fetchData();
                }
            });
        } else {
            kifuService.updateLesson(sessionInformation.getSessionId(), event.getDetails(), new AsyncCallback<Void>() {
                @Override
                public void onFailure(final Throwable throwable) {
                    GWT.log("LessonsActivity - RPC failure: updateLesson " + throwable);
                }

                @Override
                public void onSuccess(final Void unused) {
                    GWT.log("LessonsActivity - RPC success: updateLesson ");
                    fetchData();
                }
            });
        }
    }

}
