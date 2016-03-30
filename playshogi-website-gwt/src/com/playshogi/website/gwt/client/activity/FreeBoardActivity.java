package com.playshogi.website.gwt.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.ClientFactory;
import com.playshogi.website.gwt.client.place.FreeBoardPlace;
import com.playshogi.website.gwt.client.ui.TsumeView;

public class FreeBoardActivity extends AbstractActivity {

	interface MyEventBinder extends EventBinder<FreeBoardActivity> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private final ClientFactory clientFactory;
	private final String boardId;
	private TsumeView tsumeView;

	public FreeBoardActivity(final FreeBoardPlace place, final ClientFactory clientFactory) {
		this.boardId = place.getBoardId();
		this.clientFactory = clientFactory;
	}

	@Override
	public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
		eventBinder.bindEventHandlers(this, eventBus);
		tsumeView = clientFactory.getTsumeView();
		tsumeView.setTsumeId(boardId);
		containerWidget.setWidget(tsumeView.asWidget());
	}

}