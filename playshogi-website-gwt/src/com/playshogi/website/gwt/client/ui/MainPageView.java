package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MainPageView extends Composite {
	private static HelloViewImplUiBinder uiBinder = GWT.create(HelloViewImplUiBinder.class);

	interface HelloViewImplUiBinder extends UiBinder<Widget, MainPageView> {
	}

	public MainPageView() {
		GWT.log("Creating main page view");
		initWidget(uiBinder.createAndBindUi(this));
	}

}
