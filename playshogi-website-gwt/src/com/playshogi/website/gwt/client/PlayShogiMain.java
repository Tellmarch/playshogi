package com.playshogi.website.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.playshogi.website.gwt.client.gin.PlayShogiGinjector;

public class PlayShogiMain implements EntryPoint {

	@Override
	public void onModuleLoad() {
		PlayShogiGinjector injector = GWT.create(PlayShogiGinjector.class);
		injector.getApplication().start();
	}

}
