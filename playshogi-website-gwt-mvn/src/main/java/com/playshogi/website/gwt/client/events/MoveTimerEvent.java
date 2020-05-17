package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class MoveTimerEvent extends GenericEvent {

    private final int timems;
    private final boolean countdown;

    public MoveTimerEvent(int timems, boolean countdown) {
        this.timems = timems;
        this.countdown = countdown;
    }

    public int getTimems() {
        return timems;
    }

    public boolean isCountdown() {
        return countdown;
    }
}
