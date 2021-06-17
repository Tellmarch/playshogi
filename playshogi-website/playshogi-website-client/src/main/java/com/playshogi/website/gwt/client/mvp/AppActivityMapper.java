package com.playshogi.website.gwt.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.activity.*;
import com.playshogi.website.gwt.client.place.*;
import com.playshogi.website.gwt.client.ui.*;

public class AppActivityMapper implements ActivityMapper {

    @Inject
    PlaceController placeController;
    @Inject
    SessionInformation sessionInformation;
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
    ViewKifuView viewKifuView;
    @Inject
    PublicCollectionsView publicCollectionsView;
    @Inject
    MyCollectionsView myCollectionsView;
    @Inject
    CollectionView collectionView;
    @Inject
    CollectionHelpView collectionHelpView;
    @Inject
    ProblemStatisticsView problemStatisticsView;
    @Inject
    OpeningsView openingsView;
    @Inject
    TutorialView tutorialView;
    @Inject
    PlayView playView;
    @Inject
    ProblemsView problemsView;
    @Inject
    ProblemView problemView;
    @Inject
    KifuEditorView kifuEditorView;
    @Inject
    UserKifusView userKifusView;
    @Inject
    ManageProblemsView manageProblemsView;
    @Inject
    LessonsView lessonsView;
    @Inject
    ManageLessonsView manageLessonsView;
    @Inject
    ProblemCollectionsView problemCollectionsView;

    @Override
    public Activity getActivity(final Place place) {
        if (place instanceof MainPagePlace) {
            return new MainPageActivity(mainPageView);
        } else if (place instanceof LinksPlace) {
            return new LinksActivity(linksView);
        } else if (place instanceof TsumePlace) {
            return new TsumeActivity((TsumePlace) place, tsumeView, sessionInformation);
        } else if (place instanceof ByoYomiLandingPlace) {
            return new ByoYomiLandingActivity((ByoYomiLandingPlace) place, byoYomiLandingView, placeController,
                    sessionInformation);
        } else if (place instanceof ByoYomiPlace) {
            return new ByoYomiActivity((ByoYomiPlace) place, byoYomiView, placeController, sessionInformation);
        } else if (place instanceof FreeBoardPlace) {
            return new FreeBoardActivity((FreeBoardPlace) place, freeBoardView);
        } else if (place instanceof OpeningsPlace) {
            return new OpeningsActivity((OpeningsPlace) place, openingsView, placeController);
        } else if (place instanceof ProblemStatisticsPlace) {
            return new ProblemStatisticsActivity(problemStatisticsView, sessionInformation);
        } else if (place instanceof LoginPlace) {
            return new LoginActivity((LoginPlace) place, loginView, sessionInformation, placeController);
        } else if (place instanceof ViewKifuPlace) {
            return new ViewKifuActivity((ViewKifuPlace) place, viewKifuView, sessionInformation);
        } else if (place instanceof PreviewKifuPlace) {
            return new ViewKifuActivity((PreviewKifuPlace) place, viewKifuView, sessionInformation);
        } else if (place instanceof TutorialPlace) {
            return new TutorialActivity((TutorialPlace) place, tutorialView, placeController);
        } else if (place instanceof PlayPlace) {
            return new PlayActivity((PlayPlace) place, playView, sessionInformation);
        } else if (place instanceof ProblemsPlace) {
            return new ProblemsActivity((ProblemsPlace) place, problemsView, sessionInformation);
        } else if (place instanceof ProblemPlace) {
            return new ProblemActivity((ProblemPlace) place, problemView, sessionInformation);
        } else if (place instanceof KifuEditorPlace) {
            return new KifuEditorActivity((KifuEditorPlace) place, kifuEditorView, sessionInformation,
                    placeController);
        } else if (place instanceof UserKifusPlace) {
            return new UserKifusActivity((UserKifusPlace) place, userKifusView, sessionInformation);
        } else if (place instanceof ManageProblemsPlace) {
            return new ManageProblemsActivity((ManageProblemsPlace) place, manageProblemsView, sessionInformation);
        } else if (place instanceof ManageLessonsPlace) {
            return new ManageLessonsActivity((ManageLessonsPlace) place, manageLessonsView, sessionInformation);
        } else if (place instanceof MyCollectionsPlace) {
            return new MyCollectionsActivity((MyCollectionsPlace) place, myCollectionsView, sessionInformation);
        } else if (place instanceof PublicCollectionsPlace) {
            return new PublicCollectionsActivity((PublicCollectionsPlace) place, publicCollectionsView,
                    sessionInformation);
        } else if (place instanceof LessonsPlace) {
            return new LessonsActivity((LessonsPlace) place, lessonsView, sessionInformation);
        } else if (place instanceof CollectionHelpPlace) {
            return new CollectionHelpActivity(collectionHelpView);
        } else if (place instanceof CollectionPlace) {
            return new CollectionActivity((CollectionPlace) place, collectionView, sessionInformation);
        } else if (place instanceof ProblemCollectionsPlace) {
            return new ProblemCollectionsActivity((ProblemCollectionsPlace) place, problemCollectionsView,
                    sessionInformation);
        }

        return null;
    }

}
