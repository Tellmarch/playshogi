package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.events.tutorial.GoNextChapterEvent;
import com.playshogi.website.gwt.client.events.tutorial.GoPreviousChapterEvent;
import com.playshogi.website.gwt.client.events.tutorial.TryChapterAgainEvent;
import com.playshogi.website.gwt.client.place.TutorialPlace;
import com.playshogi.website.gwt.client.tutorial.Tutorial;
import com.playshogi.website.gwt.client.tutorial.TutorialMessages;
import com.playshogi.website.gwt.client.tutorial.Tutorials;
import com.playshogi.website.gwt.client.ui.TutorialView;

public class TutorialActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<TutorialActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);
    private final TutorialMessages tutorialMessages = GWT.create(TutorialMessages.class);

    private final TutorialView tutorialView;

    private EventBus eventBus;

    private final PlaceController placeController;
    private final Tutorials tutorials;
    private int chapter;

    public TutorialActivity(final TutorialPlace place, final TutorialView tutorialView,
                            final PlaceController placeController, final Tutorials tutorials) {
        this.tutorialView = tutorialView;
        this.placeController = placeController;
        this.tutorials = tutorials;
        chapter = place.getChapter();
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        this.eventBus = eventBus;
        GWT.log("Starting tutorial activity");
        eventBinder.bindEventHandlers(this, eventBus);
        tutorials.activate(eventBus);
        tutorialView.activate(eventBus, tutorials);
        containerWidget.setWidget(tutorialView.asWidget());
        Scheduler.get().scheduleDeferred(this::loadChapter);
    }

    private void loadChapter() {
        Tutorial tutorial = tutorials.getChapter(chapter);
        if (tutorial != null) {
            tutorial.setup();
        } else {
            GWT.log("Invalid chapter: " + chapter);
        }
    }

    @Override
    public void onStop() {
        GWT.log("Stopping tutorial activity");
        super.onStop();
    }

    @EventHandler
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
        GWT.log("Tutorial - OnMovePlayed Event");

        tutorials.getChapter(chapter).onMovePlayed(movePlayedEvent);
    }

    @EventHandler
    public void onGoNextChapter(final GoNextChapterEvent event) {
        GWT.log("Tutorial - GoNextChapterEvent Event");

        if (tutorials.hasChapter(chapter + 1)) {
            chapter++;
            updateURL();
            loadChapter();
        } else {
            GWT.log("Tutorial - this is the last chapter");
            eventBus.fireEvent(new ChangeTutorialTextEvent("You have reached the last chapter of this tutorial!"));
        }

    }

    @EventHandler
    public void onGoPreviousChapter(final GoPreviousChapterEvent event) {
        GWT.log("Tutorial - GoPreviousChapterEvent Event");

        if (tutorials.hasChapter(chapter - 1)) {
            chapter--;
            updateURL();
            loadChapter();
        } else {
            GWT.log("Tutorial - this is the first chapter");
        }
    }

    @EventHandler
    public void onTryChapterAgain(final TryChapterAgainEvent event) {
        GWT.log("Tutorial - TryChapterAgainEvent Event");
        loadChapter();
    }

    private void updateURL() {
        History.newItem("Tutorial:" + new TutorialPlace.Tokenizer().getToken(new TutorialPlace(chapter)), false);
    }
}