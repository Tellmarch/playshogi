package com.playshogi.website.gwt.client.events.user;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.client.UserPreferences;

public class NotationStyleSelectedEvent extends GenericEvent {
    private final UserPreferences.NotationStyle style;

    public NotationStyleSelectedEvent(UserPreferences.NotationStyle style) {
        this.style = style;
    }

    public UserPreferences.NotationStyle getStyle() {
        return style;
    }
}
