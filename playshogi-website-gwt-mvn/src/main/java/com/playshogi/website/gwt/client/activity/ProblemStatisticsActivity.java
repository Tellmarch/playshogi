package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.puzzles.HighScoreListEvent;
import com.playshogi.website.gwt.client.events.puzzles.ProblemStatisticsEvent;
import com.playshogi.website.gwt.client.ui.ProblemStatisticsView;
import com.playshogi.website.gwt.shared.models.ProblemStatisticsDetails;
import com.playshogi.website.gwt.shared.models.SurvivalHighScore;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

import java.util.Arrays;

public class ProblemStatisticsActivity extends MyAbstractActivity {

    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);
    private final ProblemStatisticsView problemStatisticsView;
    private final SessionInformation sessionInformation;
    private EventBus eventBus;

    public ProblemStatisticsActivity(final ProblemStatisticsView problemStatisticsView,
                                     SessionInformation sessionInformation) {
        this.problemStatisticsView = problemStatisticsView;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        this.eventBus = eventBus;
        GWT.log("Starting problem statistics activity");
        problemStatisticsView.activate(eventBus);
        if (sessionInformation.isLoggedIn()) {
            problemsService.getProblemStatisticsDetails(sessionInformation.getSessionId(),
                    new AsyncCallback<ProblemStatisticsDetails[]>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            GWT.log("Remote call failed to retrieve problem statistics");
                        }

                        @Override
                        public void onSuccess(ProblemStatisticsDetails[] problemStatisticsDetails) {
                            GWT.log("Received problem statistics: " + Arrays.toString(problemStatisticsDetails));
                            eventBus.fireEvent(new ProblemStatisticsEvent(problemStatisticsDetails));
                        }
                    });
        }
        problemsService.getHighScores(new AsyncCallback<SurvivalHighScore[]>() {
            @Override
            public void onFailure(Throwable throwable) {
                GWT.log("Remote call failed to retrieve high scores");
            }

            @Override
            public void onSuccess(SurvivalHighScore[] survivalHighScores) {
                GWT.log("Received high scores: " + Arrays.toString(survivalHighScores));
                eventBus.fireEvent(new HighScoreListEvent(survivalHighScores));
            }
        });
        containerWidget.setWidget(problemStatisticsView.asWidget());
    }

}