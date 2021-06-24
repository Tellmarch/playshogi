package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import com.playshogi.website.gwt.shared.models.TournamentDetails;

public class TournamentDetailsEvent extends GenericEvent {
    private final TournamentDetails details;

    public TournamentDetailsEvent(final TournamentDetails tournamentDetails) {
        this.details = tournamentDetails;
    }

    public TournamentDetails getDetails() {
        return details;
    }
}
