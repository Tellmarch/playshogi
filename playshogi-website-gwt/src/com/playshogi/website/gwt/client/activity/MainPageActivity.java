package com.playshogi.website.gwt.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.playshogi.website.gwt.client.ui.MainPageView;

public class MainPageActivity extends AbstractActivity {

	private final MainPageView mainPageView;

	public MainPageActivity(final MainPageView mainPageView) {
		this.mainPageView = mainPageView;
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		containerWidget.setWidget(mainPageView.asWidget());
	}

}
