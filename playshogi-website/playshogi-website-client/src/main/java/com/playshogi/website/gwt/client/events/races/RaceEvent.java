package com.playshogi.website.gwt.client.events.races;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.RaceDetails;

public class RaceEvent extends GenericEvent {
    private final RaceDetails raceDetails;

    public RaceEvent(final RaceDetails raceDetails) {
        this.raceDetails = raceDetails;
    }

    public RaceDetails getRaceDetails() {
        return raceDetails;
    }
}
