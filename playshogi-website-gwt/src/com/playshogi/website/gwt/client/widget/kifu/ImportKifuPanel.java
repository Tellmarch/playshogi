package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class ImportKifuPanel extends Composite implements ClickHandler {

	private final Button loadFromURLButton;

	public ImportKifuPanel() {

		FlowPanel verticalPanel = new FlowPanel();

		loadFromURLButton = new Button("Load from URL");
		loadFromURLButton.addClickHandler(this);

		verticalPanel.add(loadFromURLButton);

		initWidget(verticalPanel);
	}

	@Override
	public void onClick(final ClickEvent event) {
		Object source = event.getSource();
		if (source == loadFromURLButton) {

		}
	}
}
