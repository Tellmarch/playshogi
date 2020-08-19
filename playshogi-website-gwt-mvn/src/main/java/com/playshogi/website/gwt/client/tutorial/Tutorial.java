package com.playshogi.website.gwt.client.tutorial;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.MovePlayedEvent;

public interface Tutorial {

    void setup();

    void onMovePlayed(final MovePlayedEvent movePlayedEvent);

    void activate(final EventBus eventBus);
}
