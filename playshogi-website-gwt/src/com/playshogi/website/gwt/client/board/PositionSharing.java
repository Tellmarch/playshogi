package com.playshogi.website.gwt.client.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.PositionSharingService;
import com.playshogi.website.gwt.client.PositionSharingServiceAsync;

public class PositionSharing extends Composite {

	private final PositionSharingServiceAsync positionSharingService = GWT.create(PositionSharingService.class);

	// "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp
	// 34"

	public PositionSharing(final ShogiBoard shogiBoard) {
		final Button shareButton = new Button("Share");
		final Button loadButton = new Button("Load");
		final TextBox keyField = new TextBox();
		keyField.setText("MyBoard");

		shareButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				positionSharingService.sharePosition(SfenConverter.toSFEN(shogiBoard.getPosition()), keyField.getText(),
						new AsyncCallback<Void>() {

							@Override
							public void onSuccess(final Void result) {
								GWT.log("share success");
							}

							@Override
							public void onFailure(final Throwable caught) {
								GWT.log("share failure");
							}
						});
			}
		});

		loadButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				positionSharingService.getPosition(keyField.getText(), new AsyncCallback<String>() {

					@Override
					public void onFailure(final Throwable caught) {
						GWT.log("load failure");
					}

					@Override
					public void onSuccess(final String result) {
						GWT.log("load success");
						ShogiPosition positionFromServer = SfenConverter.fromSFEN(result);
						if (positionFromServer != null) {
							shogiBoard.setPosition(positionFromServer);
						}
					}
				});
			}
		});

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(keyField);
		horizontalPanel.add(shareButton);
		horizontalPanel.add(loadButton);

		initWidget(horizontalPanel);

	}
}
