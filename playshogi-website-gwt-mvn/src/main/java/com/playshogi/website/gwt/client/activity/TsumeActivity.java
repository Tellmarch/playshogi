package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.client.events.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.UserSkippedProblemEvent;
import com.playshogi.website.gwt.client.place.TsumePlace;
import com.playshogi.website.gwt.client.ui.TsumeView;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

public class TsumeActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<TsumeActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);

    private final String tsumeId;
    private final TsumeView tsumeView;

    private EventBus eventBus;

    public TsumeActivity(final TsumePlace place, final TsumeView tsumeView) {
        this.tsumeView = tsumeView;
        this.tsumeId = place.getTsumeId();
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting tsume activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        tsumeView.activate(eventBus);
        setTsumeId(tsumeId);
        containerWidget.setWidget(tsumeView.asWidget());
    }

    @Override
    public void onStop() {
        GWT.log("Stopping tsume activity");
        super.onStop();
    }

    @EventHandler
    void onUserSkippedProblem(final UserSkippedProblemEvent event) {
        setTsumeId(null);
    }

    public void setTsumeId(final String tsumeId) {
        if (tsumeId == null || tsumeId.equalsIgnoreCase("null")) {
            int number = Random.nextInt(800) + 100;
            requestTsume(String.valueOf(number));
        } else {
            requestTsume(tsumeId);
        }
    }

    private void requestTsume(final String tsumeId) {
        problemsService.getRandomProblem(getProblemRequestCallback(tsumeId));
        //problemsService.getProblemUsf(tsumeId, getProblemRequestCallback(tsumeId));
    }

    private AsyncCallback<ProblemDetails> getProblemRequestCallback(final String tsumeId) {
        return new AsyncCallback<ProblemDetails>() {

            @Override
            public void onSuccess(final ProblemDetails result) {
                if (result == null) {
                    GWT.log("Got null usf from server for problem request: " + tsumeId);
                } else {
                    GWT.log("Got problem details problem request: " + tsumeId + " : " + result);
                    String resultUsf = result.getUsf();
                    GWT.log("Got usf from server for problem request: " + tsumeId + " : " + resultUsf);
                    GameRecord gameRecord = UsfFormat.INSTANCE.read(resultUsf);
                    GWT.log("Updating game navigator...");
                    //TODO: how to update URL?
                    //placeController.goTo(new TsumePlace(result.getId()));
                    //History.newItem(new TsumePlace.Tokenizer().getToken(new TsumePlace(result.getId())), false);
                    eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree()));
                }
            }

            @Override
            public void onFailure(final Throwable caught) {
                GWT.log("Remote called failed for problem request: " + tsumeId);
            }
        };
    }
}