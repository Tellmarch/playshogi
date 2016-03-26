package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.playshogi.website.gwt.client.ClientFactory;
import com.playshogi.website.gwt.client.place.TsumePlace;

public class MainPageView extends Composite {
	private static HelloViewImplUiBinder uiBinder = GWT.create(HelloViewImplUiBinder.class);

	interface HelloViewImplUiBinder extends UiBinder<Widget, MainPageView> {
	}

	@UiField SpanElement nameSpan;
	@UiField Anchor goodbyeLink;
	private final ClientFactory clientFactory;

	public MainPageView(final ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("goodbyeLink")
	void onClickGoodbye(final ClickEvent e) {
		clientFactory.getPlaceController().goTo(new TsumePlace());
	}

}
