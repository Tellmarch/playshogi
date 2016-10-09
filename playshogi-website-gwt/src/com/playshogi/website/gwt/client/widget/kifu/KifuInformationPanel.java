package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.models.record.GameInformation;
import com.playshogi.website.gwt.client.events.GameInformationChangedEvent;

public class KifuInformationPanel extends Composite implements ClickHandler {
	interface MyEventBinder extends EventBinder<KifuInformationPanel> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private EventBus eventBus;

	private final Button saveButton;

	private GameInformation gameInformation;

	private final TextBox senteTextBox;
	private final TextBox goteTextBox;
	private final TextBox dateTextBox;
	private final TextBox venueTextBox;

	public KifuInformationPanel() {
		FlowPanel verticalPanel = new FlowPanel();

		Grid grid = new Grid(4, 2);
		grid.setHTML(0, 0, "Sente:");
		grid.setHTML(1, 0, "Gote:");
		grid.setHTML(2, 0, "Date:");
		grid.setHTML(3, 0, "Venue:");

		senteTextBox = createTextBox();
		goteTextBox = createTextBox();
		dateTextBox = createTextBox();
		venueTextBox = createTextBox();

		grid.setWidget(0, 1, senteTextBox);
		grid.setWidget(1, 1, goteTextBox);
		grid.setWidget(2, 1, dateTextBox);
		grid.setWidget(3, 1, venueTextBox);

		verticalPanel.add(grid);

		verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

		saveButton = new Button("Save");
		saveButton.addClickHandler(this);

		verticalPanel.add(saveButton);

		initWidget(verticalPanel);
	}

	private TextBox createTextBox() {
		TextBox senteTextBox = new TextBox();
		senteTextBox.setVisibleLength(13);
		return senteTextBox;
	}

	public void activate(final EventBus eventBus) {
		GWT.log("Activating kifu information panel");
		this.eventBus = eventBus;
		eventBinder.bindEventHandlers(this, eventBus);
	}

	@Override
	public void onClick(final ClickEvent event) {
		Object source = event.getSource();
		if (source == saveButton) {

		}
	}

	@EventHandler
	public void onGameInformationChangedEvent(final GameInformationChangedEvent event) {
		GWT.log("Kifu editor: handle GameInformationChangedEvent");
		gameInformation = event.getGameInformation();
		refreshInformation();
	}

	private void refreshInformation() {
		GWT.log("Displaying game information: " + gameInformation);
		if (gameInformation != null) {
			senteTextBox.setText(gameInformation.getSente());
			goteTextBox.setText(gameInformation.getGote());
			dateTextBox.setText(gameInformation.getDate());
			venueTextBox.setText(gameInformation.getVenue());
		}
	}

}
