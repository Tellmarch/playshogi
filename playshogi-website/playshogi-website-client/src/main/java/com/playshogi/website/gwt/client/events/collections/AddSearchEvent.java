package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class AddSearchEvent extends GenericEvent {
    private final String search;

    public AddSearchEvent(final String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    @Override
    public String toString() {
        return "AddSearchEvent{" +
                "search='" + search + '\'' +
                '}';
    }
}
