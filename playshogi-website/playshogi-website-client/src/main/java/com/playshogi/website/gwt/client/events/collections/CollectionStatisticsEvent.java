package com.playshogi.website.gwt.client.events.collections;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.GameCollectionStatisticsDetails;

public class CollectionStatisticsEvent extends GenericEvent {
    private final GameCollectionStatisticsDetails statistics;

    public CollectionStatisticsEvent(final GameCollectionStatisticsDetails statistics) {
        this.statistics = statistics;
    }

    public GameCollectionStatisticsDetails getStatistics() {
        return statistics;
    }
}
