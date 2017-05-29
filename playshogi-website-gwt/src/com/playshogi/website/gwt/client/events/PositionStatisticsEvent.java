package com.playshogi.website.gwt.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.PositionDetails;

public class PositionStatisticsEvent extends GenericEvent {

	private final PositionDetails positionDetails;

	public PositionStatisticsEvent(final PositionDetails positionDetails) {
		this.positionDetails = positionDetails;
	}

	public PositionDetails getPositionDetails() {
		return positionDetails;
	}

}
