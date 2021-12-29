package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.*;
import com.playshogi.website.gwt.client.place.AdminCollectionsPlace;
import com.playshogi.website.gwt.client.ui.AdminCollectionsView;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

public class AdminCollectionsActivity extends MyAbstractActivity {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);

    private EventBus eventBus;

    interface MyEventBinder extends EventBinder<AdminCollectionsActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final AdminCollectionsView view;
    private final SessionInformation sessionInformation;

    public AdminCollectionsActivity(final AdminCollectionsPlace place, final AdminCollectionsView view,
                                    final SessionInformation sessionInformation) {
        this.view = view;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting admin collections activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        view.activate(eventBus);

        if (sessionInformation.isAdmin()) {
            fetchData();
        }

        containerWidget.setWidget(view.asWidget());
    }

    private void fetchData() {
        kifuService.getAllGameCollections(sessionInformation.getSessionId(),
                new AsyncCallback<GameCollectionDetails[]>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("AdminCollectionsActivity: error retrieving game collections list");
                    }

                    @Override
                    public void onSuccess(GameCollectionDetails[] gameCollectionDetails) {
                        GWT.log("AdminCollectionsActivity: retrieved game collections list");
                        eventBus.fireEvent(new ListGameCollectionsEvent(null,
                                gameCollectionDetails));
                    }
                });
        problemsService.getAllProblemCollections(sessionInformation.getSessionId(),
                new AsyncCallback<ProblemCollectionDetails[]>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("AdminCollectionsActivity: error retrieving problem collections list");
                    }

                    @Override
                    public void onSuccess(final ProblemCollectionDetails[] problemCollectionDetails) {
                        GWT.log("AdminCollectionsActivity: retrieved problem collections list");
                        eventBus.fireEvent(new ListProblemCollectionsEvent(problemCollectionDetails, null));
                    }
                });

    }

    @EventHandler
    public void onSaveGameCollectionDetails(final SaveGameCollectionDetailsEvent event) {
        GWT.log("AdminCollectionsActivity: Handling SaveGameCollectionDetailsEvent: " + event.getDetails());
        kifuService.updateGameCollectionDetailsAdmin(sessionInformation.getSessionId(), event.getDetails(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("AdminCollectionsActivity: error during updateGameCollectionDetailsAdmin");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(false));
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("AdminCollectionsActivity: updateGameCollectionDetailsAdmin success");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(true));
                        refresh();
                    }
                });
    }

    @EventHandler
    public void onSaveProblemCollectionDetails(final SaveProblemCollectionDetailsEvent event) {
        GWT.log("AdminCollectionsActivity: Handling SaveProblemCollectionDetailsEvent: " + event.getDetails());
        problemsService.updateProblemCollectionDetails(sessionInformation.getSessionId(), event.getDetails(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("AdminCollectionsActivity: error during updateProblemCollectionDetails");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(false));
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("AdminCollectionsActivity: updateProblemCollectionDetails success");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(true));
                        refresh();
                    }
                });
    }


    @EventHandler
    public void onDeleteGameCollection(final DeleteGameCollectionEvent event) {
        GWT.log("AdminCollectionsActivity Handling DeleteGameCollectionEvent");
        kifuService.deleteGameCollection(sessionInformation.getSessionId(), event.getCollectionId(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("AdminCollectionsActivity: error during deleteGameCollection");
                        Window.alert("Deletion failed - maybe you do not have permission?");
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("AdminCollectionsActivity: deleteGameCollection success");
                        refresh();
                    }
                });
    }

    @EventHandler
    public void onConvertGameCollection(final ConvertGameCollectionEvent event) {
        GWT.log("AdminCollectionsActivity Handling ConvertGameCollectionEvent");
        problemsService.convertGameCollection(sessionInformation.getSessionId(), event.getCollectionId(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("AdminCollectionsActivity: error during convertGameCollection");
                        Window.alert("Conversion failed - maybe you do not have permission?");
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("AdminCollectionsActivity: convertGameCollection success");
                        refresh();
                    }
                });
    }

    @EventHandler
    public void onDeleteProblemCollection(final DeleteProblemCollectionEvent event) {
        GWT.log("AdminCollectionsActivity Handling DeleteProblemCollectionEvent");
        problemsService.deleteProblemCollection(sessionInformation.getSessionId(), event.getCollectionId(), false,
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("AdminCollectionsActivity: error during deleteProblemCollection");
                        Window.alert("Deletion failed - maybe you do not have permission?");
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("AdminCollectionsActivity: deleteProblemCollection success");
                        refresh();
                    }
                });
    }

    private void refresh() {
        fetchData();
    }
}
