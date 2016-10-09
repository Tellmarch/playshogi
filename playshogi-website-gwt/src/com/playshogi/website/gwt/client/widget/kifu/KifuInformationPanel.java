package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.GameRecordChangedEvent;

public class KifuInformationPanel extends Composite implements ClickHandler {
	interface MyEventBinder extends EventBinder<KifuInformationPanel> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private EventBus eventBus;

	private final Button saveButton;

	public KifuInformationPanel() {
		FlowPanel verticalPanel = new FlowPanel();

		verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

		saveButton = new Button("Save kifu");
		saveButton.addClickHandler(this);

		verticalPanel.add(saveButton);

		initWidget(verticalPanel);
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
	public void onGameRecordChangedEvent(final GameRecordChangedEvent event) {
		GWT.log("Kifu editor: handle GameRecordChangedEvent");
	}

}
