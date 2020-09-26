package com.playshogi.website.gwt.client.tutorial;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTitleEvent;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class Introduction implements Tutorial{

    private final ShogiBoard shogiBoard;
    private final TutorialMessages tutorialMessages;
    private EventBus eventBus;

    Introduction(ShogiBoard shogiBoard, TutorialMessages tutorialMessages) {
        this.shogiBoard = shogiBoard;
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    public void setup() {
        ShogiPosition shogiPosition = ShogiInitialPositionFactory.createInitialPosition();

        shogiBoard.setPosition(shogiPosition);

        eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.intro()));
        eventBus.fireEvent(new ChangeTutorialTitleEvent(tutorialMessages.introTitle()));
    }

    @Override
    public void onMovePlayed(MovePlayedEvent movePlayedEvent) {

    }

    @Override
    public void activate(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
