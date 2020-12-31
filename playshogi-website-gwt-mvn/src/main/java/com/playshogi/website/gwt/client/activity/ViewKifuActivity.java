package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.gametree.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.gametree.MoveSelectedEvent;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.*;
import com.playshogi.website.gwt.client.place.ViewKifuPlace;
import com.playshogi.website.gwt.client.ui.ViewKifuView;
import com.playshogi.website.gwt.shared.models.AnalysisRequestResult;
import com.playshogi.website.gwt.shared.models.AnalysisRequestStatus;
import com.playshogi.website.gwt.shared.models.PositionEvaluationDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class ViewKifuActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<ViewKifuActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);

    private final ViewKifuView viewKifuView;

    private GameRecord gameRecord;

    private EventBus eventBus;

    private final SessionInformation sessionInformation;

    private final String kifuId;
    private final int initialMoveCount;
    private AnalysisRequestResult analysisResult;

    public ViewKifuActivity(final ViewKifuPlace place, final ViewKifuView viewKifuView,
                            final SessionInformation sessionInformation) {
        this.viewKifuView = viewKifuView;
        this.sessionInformation = sessionInformation;
        kifuId = place.getKifuId();
        initialMoveCount = place.getMove();
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting view kifu activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        viewKifuView.activate(eventBus, kifuId);
        containerWidget.setWidget(viewKifuView.asWidget());

        kifuService.getKifuUsf(sessionInformation.getSessionId(), kifuId, new AsyncCallback<String>() {

            @Override
            public void onSuccess(final String usf) {
                GWT.log("Kifu loaded successfully: " + usf);
                gameRecord = UsfFormat.INSTANCE.readSingle(usf);

                eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree(), initialMoveCount));
                eventBus.fireEvent(new GameInformationChangedEvent(gameRecord.getGameInformation()));
            }

            @Override
            public void onFailure(final Throwable caught) {
                GWT.log("Error while loqding the kifu: " + caught);
            }
        });
    }

    @Override
    public void onStop() {
        GWT.log("Stopping view kifu activity");
        super.onStop();
    }

    @EventHandler
    public void onRequestPositionEvaluationEvent(final RequestPositionEvaluationEvent event) {
        GWT.log("View Kifu Activity Handling RequestPositionEvaluationEvent");
        String sfen = SfenConverter.toSFEN(viewKifuView.getGameNavigator().getGameNavigation().getPosition());
        kifuService.analysePosition(sessionInformation.getSessionId(), sfen,
                new AsyncCallback<PositionEvaluationDetails>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("ViewKifu - ERROR GETTING POSITION EVALUATION");
                    }

                    @Override
                    public void onSuccess(PositionEvaluationDetails result) {
                        GWT.log("ViewKifu - received position evaluation\n" + result);
                        eventBus.fireEvent(new PositionEvaluationEvent(result));
                    }
                });
    }

    @EventHandler
    public void onRequestKifuEvaluationEvent(final RequestKifuEvaluationEvent event) {
        GWT.log("View Kifu Activity Handling RequestKifuEvaluationEvent");
        String usf = UsfFormat.INSTANCE.write(viewKifuView.getGameNavigator().getGameNavigation().getGameTree());


        kifuService.requestKifuAnalysis(sessionInformation.getSessionId(), usf,
                new AsyncCallback<AnalysisRequestStatus>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("ViewKifu - error requesting kifu evaluation");
                    }

                    @Override
                    public void onSuccess(AnalysisRequestStatus result) {
                        GWT.log("ViewKifu - kifu evaluation result: " + result);
                        if (result.isDenied()) {
                            eventBus.fireEvent(new KifuEvaluationEvent(new AnalysisRequestResult(result), kifuId));
                        } else if (result.needToWait()) {

                            Timer timer = new Timer() {
                                @Override
                                public void run() {
                                    queryKifuAnalysisResults(usf, this);
                                }
                            };

                            timer.scheduleRepeating(1000);
                        } else {
                            queryKifuAnalysisResults(usf, null);
                        }
                    }
                });
    }

    private void queryKifuAnalysisResults(final String usf, final Timer timer) {
        kifuService.getKifuAnalysisResults(sessionInformation.getSessionId(), usf,
                new AsyncCallback<AnalysisRequestResult>() {

                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("ViewKifu - error requesting kifu analysis results");
                        if (timer != null) timer.cancel();
                    }

                    @Override
                    public void onSuccess(AnalysisRequestResult result) {
                        GWT.log("ViewKifu - got kifu analysis results: " + result);
                        if (result.getStatus().isFinal()) {
                            if (timer != null) timer.cancel();
                        }
                        analysisResult = result;
                        eventBus.fireEvent(new KifuEvaluationEvent(result, kifuId));
                    }
                });
    }

    @EventHandler
    public void onPositionChangedEvent(final PositionChangedEvent event) {
        GWT.log("ViewKifuActivity handling PositionChangedEvent");

        //TODO: do all this only if we are in mainline

        int moveCount = event.getPosition().getMoveCount();
        //Update URL with the new move count
        History.newItem("ViewKifu:" + new ViewKifuPlace.Tokenizer().getToken(new ViewKifuPlace(kifuId,
                moveCount)), false);

        if (analysisResult != null && analysisResult.getDetails().length > moveCount) {
            PositionEvaluationDetails detail = analysisResult.getDetails()[moveCount];
            Scheduler.get().scheduleDeferred(() -> eventBus.fireEvent(new PositionEvaluationEvent(detail)));
        }
    }

    @EventHandler
    public void onMoveSelectedEvent(final MoveSelectedEvent event) {
        GWT.log("ViewKifuActivity handling MoveSelectedEvent");
        eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree(), event.getMoveNumber()));
    }

}