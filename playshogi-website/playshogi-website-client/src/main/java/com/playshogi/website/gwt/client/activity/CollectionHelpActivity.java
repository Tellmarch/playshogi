package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class CollectionHelpActivity extends MyAbstractActivity {

    private final Widget mainPageView;

    public CollectionHelpActivity(final Widget mainPageView) {
        this.mainPageView = mainPageView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting main page activity");
        containerWidget.setWidget(mainPageView.asWidget());
    }

}
