package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.*;
import com.playshogi.website.gwt.client.events.kifu.ImportGameRecordEvent;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.place.ProblemCollectionPlace;
import com.playshogi.website.gwt.client.ui.ProblemCollectionView;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetailsAndProblems;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;
import org.dominokit.domino.ui.notifications.Notification;

public class ProblemCollectionActivity extends MyAbstractActivity {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private final ProblemsServiceAsync problemService = GWT.create(ProblemsService.class);
    private EventBus eventBus;

    interface MyEventBinder extends EventBinder<ProblemCollectionActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ProblemCollectionPlace place;
    private final ProblemCollectionView view;
    private final SessionInformation sessionInformation;

    private ProblemCollectionDetailsAndProblems problemCollectionDetailsAndProblems = null;

    public ProblemCollectionActivity(final ProblemCollectionPlace place, final ProblemCollectionView view,
                                     final SessionInformation sessionInformation) {
        this.place = place;
        this.view = view;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting game collection activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        view.activate(eventBus);

        fetchData();

        containerWidget.setWidget(view.asWidget());
    }

    private void fetchData() {
        if (sessionInformation.isLoggedIn()) {
            GWT.log("Querying for collection games");
            problemService.getProblemCollection(sessionInformation.getSessionId(), place.getCollectionId(), true,
                    new AsyncCallback<ProblemCollectionDetailsAndProblems>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            GWT.log("ProblemCollectionActivity: error retrieving collection problems");
                        }

                        @Override
                        public void onSuccess(ProblemCollectionDetailsAndProblems result) {
                            GWT.log("ProblemCollectionActivity: retrieved collection problems");
                            problemCollectionDetailsAndProblems = result;
                            eventBus.fireEvent(new ListCollectionProblemsEvent(result.getProblems(),
                                    result.getDetails()));
                        }
                    });
        }
    }

    @EventHandler
    public void onSaveProblemCollectionDetails(final SaveProblemCollectionDetailsEvent event) {
        GWT.log("ProblemCollectionActivity: Handling SaveProblemCollectionDetailsEvent: " + event.getDetails());
        problemService.updateProblemCollectionDetails(sessionInformation.getSessionId(), event.getDetails(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("ProblemCollectionActivity: error during saveProblemCollectionDetails");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(false));
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("ProblemCollectionActivity: saveProblemCollectionDetails success");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(true));
                        refresh();
                    }
                });
    }

    @EventHandler
    public void onImportProblemRecord(final ImportGameRecordEvent event) {
        GWT.log("ProblemCollectionActivity Handling ImportProblemRecordEvent");

        problemService.saveProblemAndAddToCollection(sessionInformation.getSessionId(),
                UsfFormat.INSTANCE.write(event.getGameRecord()),
                event.getCollectionId(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("ProblemCollectionActivity: error during saveKifu");
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("ProblemCollectionActivity: saveKifu success");
                        refresh();
                    }
                });
    }

    @EventHandler
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        refresh();
    }

    @EventHandler
    public void onRemoveProblemFromCollection(final RemoveProblemFromCollectionEvent event) {
        GWT.log("ProblemCollectionActivity Handling RemoveProblemFromCollectionEvent");
        problemService.removeProblemFromCollection(sessionInformation.getSessionId(), event.getProblemId(),
                place.getCollectionId(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("ProblemCollectionActivity: error during removeProblemFromCollection");
                        Notification.createDanger("Deletion failed - maybe you do not have permission?").show();
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("ProblemCollectionActivity: removeProblemFromCollection success");
                        refresh();
                    }
                });
    }

    @EventHandler
    public void onMoveProblemUp(final MoveProblemUpEvent event) {
        GWT.log("ProblemCollectionActivity Handling MoveProblemUpEvent");

        ProblemDetails[] problems = problemCollectionDetailsAndProblems.getProblems();

        if (problems[event.getProblemDetails().getIndexInCollection() - 1] != event.getProblemDetails()) {
            GWT.log("Can't move the problem in the collection (inconsistent indexes)");
            return;
        }

        if (event.getProblemDetails().getIndexInCollection() == 1) {
            GWT.log("Can't move the first problem in the collection");
            return;
        }

        String firstProblemId = event.getProblemDetails().getId();
        String secondProblemId = problems[event.getProblemDetails().getIndexInCollection() - 2].getId();

        swapProblemIds(firstProblemId, secondProblemId);

    }

    @EventHandler
    public void onMoveProblemUp(final MoveProblemDownEvent event) {
        GWT.log("ProblemCollectionActivity Handling MoveProblemDownEvent");

        ProblemDetails[] problems = problemCollectionDetailsAndProblems.getProblems();

        if (problems[event.getProblemDetails().getIndexInCollection() - 1] != event.getProblemDetails()) {
            GWT.log("Can't move the problem in the collection (inconsistent indexes)");
            return;
        }

        if (event.getProblemDetails().getIndexInCollection() == problems.length) {
            GWT.log("Can't move the last problem in the collection");
            return;
        }

        String firstProblemId = event.getProblemDetails().getId();
        String secondProblemId = problems[event.getProblemDetails().getIndexInCollection()].getId();

        swapProblemIds(firstProblemId, secondProblemId);
    }


    private void swapProblemIds(final String firstProblemId, final String secondProblemId) {
        problemService.swapProblemsInCollection(sessionInformation.getSessionId(),
                problemCollectionDetailsAndProblems.getDetails().getId(), firstProblemId, secondProblemId,
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("ProblemCollectionActivity: swapProblemsInCollection failure");
                        Notification.createDanger("Move failed").show();
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("ProblemCollectionActivity: swapProblemsInCollection success");
                        refresh();
                    }
                });
    }

    private void refresh() {
        fetchData();
    }

}
