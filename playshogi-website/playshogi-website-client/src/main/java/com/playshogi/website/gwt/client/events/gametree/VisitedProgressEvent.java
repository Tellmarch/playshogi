package com.playshogi.website.gwt.client.events.gametree;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class VisitedProgressEvent extends GenericEvent {
    private final int visited;
    private final int total;

    public VisitedProgressEvent(final int visited, final int total) {
        this.visited = visited;
        this.total = total;
    }

    public int getVisited() {
        return visited;
    }

    public int getTotal() {
        return total;
    }
}
