package com.playshogi.website.gwt.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.place.FreeBoardPlace;
import com.playshogi.website.gwt.client.ui.FreeBoardView;

public class FreeBoardActivity extends AbstractActivity {

	interface MyEventBinder extends EventBinder<FreeBoardActivity> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private final String boardId;
	private final FreeBoardView freeBoardView;

	public FreeBoardActivity(final FreeBoardPlace place, final FreeBoardView freeBoardView) {
		this.freeBoardView = freeBoardView;
		this.boardId = place.getBoardId();
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		GWT.log("Starting free board activity");
		eventBinder.bindEventHandlers(this, eventBus);
		freeBoardView.activate(eventBus);
		containerWidget.setWidget(freeBoardView.asWidget());
	}

}