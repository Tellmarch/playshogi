package com.playshogi.website.gwt.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.activity.*;
import com.playshogi.website.gwt.client.place.*;
import com.playshogi.website.gwt.client.tutorial.Tutorials;
import com.playshogi.website.gwt.client.ui.*;

public class AppActivityMapper implements ActivityMapper {

    @Inject
    MainPageView mainPageView;
    @Inject
    LinksView linksView;
    @Inject
    TsumeView tsumeView;
    @Inject
    ByoYomiLandingView byoYomiLandingView;
    @Inject
    ByoYomiView byoYomiView;
    @Inject
    FreeBoardView freeBoardView;
    @Inject
    LoginView loginView;
    @Inject
    NewKifuView newKifuView;
    @Inject
    ViewKifuView viewKifuView;
    @Inject
    MyGamesView myGamesView;
    @Inject
    GameCollectionsView gameCollectionsView;
    @Inject
    ProblemStatisticsView problemStatisticsView;
    @Inject
    OpeningsView openingsView;
    @Inject
    TutorialView tutorialView;
    @Inject
    SessionInformation sessionInformation;
    @Inject
    PlaceController placeController;
    @Inject
    Tutorials tutorials;

    @Override
    public Activity getActivity(final Place place) {
        if (place instanceof MainPagePlace) {
            return new MainPageActivity(mainPageView);
        } else if (place instanceof LinksPlace) {
            return new LinksActivity(linksView);
        } else if (place instanceof TsumePlace) {
            return new TsumeActivity((TsumePlace) place, tsumeView, placeController, sessionInformation);
        } else if (place instanceof ByoYomiLandingPlace) {
            return new ByoYomiLandingActivity((ByoYomiLandingPlace) place, byoYomiLandingView, placeController,
                    sessionInformation);
        } else if (place instanceof ByoYomiPlace) {
            return new ByoYomiActivity((ByoYomiPlace) place, byoYomiView, placeController, sessionInformation);
        } else if (place instanceof FreeBoardPlace) {
            return new FreeBoardActivity((FreeBoardPlace) place, freeBoardView);
        } else if (place instanceof OpeningsPlace) {
            return new OpeningsActivity((OpeningsPlace) place, openingsView, placeController);
        } else if (place instanceof MyGamesPlace) {
            return new MyGamesActivity(myGamesView);
        } else if (place instanceof GameCollectionsPlace) {
            return new GameCollectionsActivity((GameCollectionsPlace) place, gameCollectionsView, sessionInformation);
        } else if (place instanceof ProblemStatisticsPlace) {
            return new ProblemStatisticsActivity(problemStatisticsView, sessionInformation);
        } else if (place instanceof LoginPlace) {
            return new LoginActivity((LoginPlace) place, loginView, sessionInformation);
        } else if (place instanceof NewKifuPlace) {
            return new NewKifuActivity((NewKifuPlace) place, newKifuView, sessionInformation);
        } else if (place instanceof ViewKifuPlace) {
            return new ViewKifuActivity((ViewKifuPlace) place, viewKifuView, sessionInformation);
        } else if (place instanceof TutorialPlace) {
            return new TutorialActivity((TutorialPlace) place, tutorialView, placeController, tutorials);
        }
        return null;
    }

}
