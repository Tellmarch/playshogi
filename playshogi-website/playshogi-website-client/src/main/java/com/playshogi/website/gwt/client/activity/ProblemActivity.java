package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.controller.ProblemController;
import com.playshogi.website.gwt.client.events.gametree.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.GameInformationChangedEvent;
import com.playshogi.website.gwt.client.place.ProblemPlace;
import com.playshogi.website.gwt.client.ui.ProblemView;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class ProblemActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<ProblemActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private final ProblemView problemView;
    private final SessionInformation sessionInformation;
    private final ProblemController problemController;
    private EventBus eventBus;

    private final String kifuId;

    public ProblemActivity(final ProblemPlace place, final ProblemView problemView,
                           final SessionInformation sessionInformation) {
        this.problemView = problemView;
        this.kifuId = place.getKifuId();
        this.sessionInformation = sessionInformation;
        this.problemController = new ProblemController(problemView::getCurrentPosition, sessionInformation);
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting problems activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        problemView.activate(eventBus);
        problemController.activate(eventBus);
        containerWidget.setWidget(problemView.asWidget());
        loadProblem();
    }

    private void loadProblem() {
        String id = kifuId;

        kifuService.getKifuUsf(sessionInformation.getSessionId(), id,
                new AsyncCallback<String>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("Remote called failed for problem request: " + id);
                    }

                    @Override
                    public void onSuccess(final String usf) {
                        GWT.log("Received problem USF: " + usf);
                        GameRecord gameRecord = UsfFormat.INSTANCE.readSingle(usf);
                        eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree()));
                        eventBus.fireEvent(new GameInformationChangedEvent(gameRecord.getGameInformation()));
                    }
                });
    }

    @Override
    public void onStop() {
        GWT.log("Stopping problem activity");
        super.onStop();
    }
}
