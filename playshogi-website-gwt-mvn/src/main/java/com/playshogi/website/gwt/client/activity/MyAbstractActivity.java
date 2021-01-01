package com.playshogi.website.gwt.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.VersionInformation;

public abstract class MyAbstractActivity extends AbstractActivity {

    private static final VersionInformation VERSION_INFORMATION = new VersionInformation();

    /**
     * This method is called when opening an activity.
     * Be careful when firing events in this method, as they may still be caught by the previous activity handlers.
     * Prefer using "scheduleDeferred" when it is needed, to make sure the activity switch is complete first.
     */
    public abstract void start(AcceptsOneWidget panel, EventBus eventBus);

    @Override
    public void start(final AcceptsOneWidget panel, final com.google.gwt.event.shared.EventBus eventBus) {
        VERSION_INFORMATION.checkVersion();
        start(panel, (EventBus) eventBus);
    }
}
