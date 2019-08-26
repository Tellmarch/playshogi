package com.playshogi.website.gwt.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

public abstract class MyAbstractActivity extends AbstractActivity {
    public abstract void start(AcceptsOneWidget panel, EventBus eventBus);

    @Override
    public void start(final AcceptsOneWidget panel, final com.google.gwt.event.shared.EventBus eventBus) {
        start(panel, (EventBus) eventBus);
    }
}
