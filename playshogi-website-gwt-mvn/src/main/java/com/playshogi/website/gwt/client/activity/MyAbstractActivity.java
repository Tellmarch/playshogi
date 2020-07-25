package com.playshogi.website.gwt.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.VersionInformation;

public abstract class MyAbstractActivity extends AbstractActivity {

    private static final VersionInformation VERSION_INFORMATION = new VersionInformation();

    public abstract void start(AcceptsOneWidget panel, EventBus eventBus);

    @Override
    public void start(final AcceptsOneWidget panel, final com.google.gwt.event.shared.EventBus eventBus) {
        VERSION_INFORMATION.checkVersion();
        start(panel, (EventBus) eventBus);
    }
}
