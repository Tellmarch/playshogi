package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.gametree.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.gametree.MoveSelectedEvent;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.gametree.VisitedProgressEvent;
import com.playshogi.website.gwt.client.events.kifu.GameInformationChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.PositionEvaluationEvent;
import com.playshogi.website.gwt.client.events.kifu.PositionStatisticsEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestPositionEvaluationEvent;
import com.playshogi.website.gwt.client.place.ViewLessonPlace;
import com.playshogi.website.gwt.client.ui.ViewLessonView;
import com.playshogi.website.gwt.client.util.FireAndForgetCallback;
import com.playshogi.website.gwt.shared.models.PositionDetails;
import com.playshogi.website.gwt.shared.models.PositionEvaluationDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;
import com.playshogi.website.gwt.shared.services.UserService;
import com.playshogi.website.gwt.shared.services.UserServiceAsync;

public class ViewLessonActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<ViewLessonActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private final UserServiceAsync userService = GWT.create(UserService.class);

    private final ViewLessonPlace place;
    private final ViewLessonView view;
    private final SessionInformation sessionInformation;
    private final String kifuUsf;
    private final Duration duration = new Duration();

    private int percentage = 0;
    private GameRecord gameRecord;
    private EventBus eventBus;

    public ViewLessonActivity(final ViewLessonPlace place, final ViewLessonView view,
                              final SessionInformation sessionInformation) {
        this.place = place;
        this.view = view;
        this.sessionInformation = sessionInformation;
        kifuUsf = null;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting ViewLessonActivity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        view.activate(eventBus, place.getKifuId(), place.isInverted());
        containerWidget.setWidget(view.asWidget());

        if (kifuUsf != null) {
            Scheduler.get().scheduleDeferred(() ->
                    loadUsf(kifuUsf)
            );
        } else {
            kifuService.getKifuUsf(sessionInformation.getSessionId(), place.getKifuId(), new AsyncCallback<String>() {

                @Override
                public void onSuccess(final String usf) {
                    loadUsf(usf);
                }

                @Override
                public void onFailure(final Throwable caught) {
                    GWT.log("Error while loqding the kifu: " + caught);
                }
            });
        }
    }

    private void loadUsf(final String usf) {
        GWT.log("Loading Kifu from USF: " + usf.length());
        gameRecord = UsfFormat.INSTANCE.readSingle(usf);

        eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree(), place.getMove()));
        eventBus.fireEvent(new GameInformationChangedEvent(gameRecord.getGameInformation()));
    }

    @Override
    public void onStop() {
        GWT.log("Stopping ViewLessonActivity");
        super.onStop();
    }

    @EventHandler
    public void onRequestPositionEvaluationEvent(final RequestPositionEvaluationEvent event) {
        GWT.log("ViewLessonActivity Handling RequestPositionEvaluationEvent");
        String sfen = SfenConverter.toSFEN(view.getNavigationController().getPosition());
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
    public void onPositionChangedEvent(final PositionChangedEvent event) {
        GWT.log("ViewLessonActivity handling PositionChangedEvent");

        ShogiPosition position = event.getPosition();
        int moveCount = position.getMoveCount();

        //Update URL with the new move count
        History.replaceItem("ViewLesson:" +
                new ViewLessonPlace.Tokenizer().getToken(place.withMove(moveCount)), false);

        String gameSetId = "1";
        kifuService.getPositionDetails(SfenConverter.toSFEN(position), gameSetId, new AsyncCallback<PositionDetails>() {
            @Override
            public void onSuccess(final PositionDetails result) {
                GWT.log("ViewLessonActivity - GOT POSITION DETAILS ");
                eventBus.fireEvent(new PositionStatisticsEvent(result, position, gameSetId));
            }

            @Override
            public void onFailure(final Throwable caught) {
                GWT.log("ViewLessonActivity - ERROR GETTING POSITION STATS");
            }
        });
    }

    @EventHandler
    public void onMoveSelectedEvent(final MoveSelectedEvent event) {
        GWT.log("ViewLessonActivity handling MoveSelectedEvent");
        eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree(), event.getMoveNumber()));
    }

    @EventHandler
    public void onVisitedProgress(final VisitedProgressEvent event) {
        GWT.log("ViewLessonActivity: Handling VisitedProgressEvent");
        if (sessionInformation.isLoggedIn()) {
            boolean complete = event.getTotal() == event.getVisited();
            int newPercentage = 100 * event.getVisited() / event.getTotal();

            if ((percentage != 100 && newPercentage == 100) || newPercentage > percentage + 10) {
                percentage = newPercentage;
                userService.saveLessonProgress(sessionInformation.getSessionId(), place.getLessonId(),
                        duration.elapsedMillis(), complete, percentage, null,
                        new FireAndForgetCallback("saveLessonProgress"));
            }
        }
    }
}