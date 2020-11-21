package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.controller.ProblemController;
import com.playshogi.website.gwt.client.events.gametree.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.puzzles.UserFinishedProblemEvent;
import com.playshogi.website.gwt.client.events.puzzles.UserSkippedProblemEvent;
import com.playshogi.website.gwt.client.place.ProblemsPlace;
import com.playshogi.website.gwt.client.ui.ProblemsView;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.GameCollectionDetailsAndGames;
import com.playshogi.website.gwt.shared.models.GameDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class ProblemsActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<ProblemsActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private final ProblemsView problemsView;
    private final SessionInformation sessionInformation;
    private final ProblemController problemController = new ProblemController();
    private EventBus eventBus;

    private final String collectionId;

    private GameCollectionDetails details;
    private GameDetails[] games;
    private int problemIndex;


    public ProblemsActivity(final ProblemsPlace place, final ProblemsView problemsView,
                            final SessionInformation sessionInformation) {
        this.problemsView = problemsView;
        this.collectionId = place.getCollectionId();
        this.problemIndex = place.getProblemIndex();
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting problems activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        problemsView.activate(eventBus);
        problemController.activate(eventBus);
        containerWidget.setWidget(problemsView.asWidget());

        if (collectionId != null && !"null".equals(collectionId)) {
            GWT.log("Querying for collection problems");
            kifuService.getGameSetKifuDetails(sessionInformation.getSessionId(), collectionId,
                    new AsyncCallback<GameCollectionDetailsAndGames>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            GWT.log("ProblemsActivity: error retrieving collection games");
                        }

                        @Override
                        public void onSuccess(GameCollectionDetailsAndGames result) {
                            GWT.log("ProblemsActivity: retrieved collection games");
                            games = result.getGames();
                            details = result.getDetails();
                            loadProblem();
                        }
                    });
        }
    }

    @Override
    public void onStop() {
        GWT.log("Stopping tsume activity");
        super.onStop();
    }

    @EventHandler
    void onUserSkippedProblem(final UserSkippedProblemEvent event) {
        problemIndex++;
        loadProblem();
    }

    @EventHandler
    void onUserFinishedProblemEvent(final UserFinishedProblemEvent event) {
        GWT.log("Finished problem. Success: " + event.isSuccess());
    }

    private void loadProblem() {
        if (problemIndex >= games.length) {
            Window.alert("You reached the last problem in the collection!");
            return;
        }
        kifuService.getKifuUsf(sessionInformation.getSessionId(), games[problemIndex].getKifuId(),
                new AsyncCallback<String>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("Remote called failed for problem request: " + games[problemIndex].getKifuId());
                    }

                    @Override
                    public void onSuccess(final String usf) {
                        GWT.log("Received problem USF: " + usf);
                        GameRecord gameRecord = UsfFormat.INSTANCE.readSingle(usf);
                        eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree()));
                        History.newItem("Problems:" + new ProblemsPlace.Tokenizer().getToken(getPlace()), false);
                    }
                });
    }


    private ProblemsPlace getPlace() {
        return new ProblemsPlace(collectionId, problemIndex);
    }

}
