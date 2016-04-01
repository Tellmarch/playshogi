package com.playshogi.website.gwt.client.gin;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.playshogi.website.gwt.client.PlayShogiMain;

@GinModules(PlayShogiGinModule.class)
public interface PlayShogiGinjector extends Ginjector {

	// entry point
	void inject(PlayShogiMain playShogiMain);

}
