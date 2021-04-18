package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.puzzles.HighScoreListEvent;
import com.playshogi.website.gwt.client.place.ByoYomiLandingPlace;
import com.playshogi.website.gwt.client.ui.ByoYomiLandingView;
import com.playshogi.website.gwt.shared.models.SurvivalHighScore;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

import java.util.Arrays;

public class ByoYomiLandingActivity extends MyAbstractActivity {

    private final ByoYomiLandingView byoYomiLandingView;
    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);

    public ByoYomiLandingActivity(final ByoYomiLandingPlace place, final ByoYomiLandingView byoYomiLandingView,
                                  final PlaceController placeController, final SessionInformation sessionInformation) {
        this.byoYomiLandingView = byoYomiLandingView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting byo yomi landing activity");
        containerWidget.setWidget(byoYomiLandingView.asWidget());
        byoYomiLandingView.activate(eventBus);
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
    }

}
