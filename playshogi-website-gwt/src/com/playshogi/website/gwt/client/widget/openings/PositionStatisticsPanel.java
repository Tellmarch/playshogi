package com.playshogi.website.gwt.client.widget.openings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.PositionStatisticsEvent;
import com.playshogi.website.gwt.shared.models.PositionDetails;
import com.playshogi.website.gwt.shared.models.PositionMoveDetails;

public class PositionStatisticsPanel extends Composite implements ClickHandler {
	interface MyEventBinder extends EventBinder<PositionStatisticsPanel> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private EventBus eventBus;

	private PositionDetails positionDetails;

	private final FlowPanel verticalPanel;

	public PositionStatisticsPanel() {
		verticalPanel = new FlowPanel();

		verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

		initWidget(verticalPanel);
	}

	public void activate(final EventBus eventBus) {
		GWT.log("Activating position statistics panel");
		this.eventBus = eventBus;
		eventBinder.bindEventHandlers(this, eventBus);
	}

	@Override
	public void onClick(final ClickEvent event) {
		Object source = event.getSource();
		if (source == verticalPanel) {

		}
	}

	@EventHandler
	public void onGameInformationChangedEvent(final PositionStatisticsEvent event) {
		GWT.log("Position statistics: handle PositionStatisticsEvent");
		positionDetails = event.getPositionDetails();
		refreshInformation();
	}

	private void refreshInformation() {
		GWT.log("Displaying position details: " + positionDetails);
		if (positionDetails != null) {
			verticalPanel.clear();

			int senteRate = (positionDetails.getSente_wins() * 100) / positionDetails.getTotal();
			String winRate = "Sente win rate: " + senteRate;
			verticalPanel.add(new HTML(SafeHtmlUtils.fromTrustedString(winRate)));

			verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

			PositionMoveDetails[] positionMoveDetails = positionDetails.getPositionMoveDetails();

			Grid grid = new Grid(positionMoveDetails.length, 3);

			for (int i = 0; i < positionMoveDetails.length; i++) {
				PositionMoveDetails moveDetails = positionMoveDetails[i];
				grid.setHTML(i, 0, moveDetails.getMove());
				grid.setHTML(i, 1, String.valueOf(moveDetails.getTotal()));

				int moveRate = (moveDetails.getSente_wins() * 1000) / moveDetails.getTotal();
				grid.setHTML(i, 2, String.valueOf(moveRate / 10.));
			}

			verticalPanel.add(grid);
		}
	}

}
