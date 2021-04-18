package com.playshogi.website.gwt.client.gin;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.playshogi.website.gwt.client.PlayShogiApp;

@GinModules(PlayShogiGinModule.class)
public interface PlayShogiGinjector extends Ginjector {

    PlayShogiApp getApplication();

}
