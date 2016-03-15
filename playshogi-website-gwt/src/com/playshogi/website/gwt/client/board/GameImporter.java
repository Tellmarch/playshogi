package com.playshogi.website.gwt.client.board;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

public class GameImporter extends Composite implements ClickHandler {

	private final ShogiBoard shogiBoard;
	private final Button importButton;
	private final Button exportButton;
	private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
	private final GameNavigation<ShogiPosition> gameNavigation;

	public GameImporter(final ShogiBoard shogiBoard, final GameNavigation<ShogiPosition> gameNavigation) {

		this.shogiBoard = shogiBoard;
		this.gameNavigation = gameNavigation;
		importButton = new Button("Import");
		exportButton = new Button("Export");

		importButton.addClickHandler(this);
		exportButton.addClickHandler(this);

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(importButton);
		horizontalPanel.add(exportButton);

		TextArea textArea = new TextArea();
		textArea.setCharacterWidth(50);
		textArea.setVisibleLines(20);

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(horizontalPanel);
		verticalPanel.add(textArea);

		initWidget(verticalPanel);

	}

	@Override
	public void onClick(final ClickEvent event) {
		Object source = event.getSource();
		if (source == importButton) {
			importGame();
			shogiBoard.displayPosition();
		} else if (source == exportButton) {
			exportGame();
		}
	}

	private void exportGame() {
		// TODO Auto-generated method stub

	}

	private void importGame() {
		// TODO Auto-generated method stub

	}

}
