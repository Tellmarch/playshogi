package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.ui.MainPageView;

public class MainPageActivity extends MyAbstractActivity {

    private final MainPageView mainPageView;

    public MainPageActivity(final MainPageView mainPageView) {
        this.mainPageView = mainPageView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting main page activity");
        containerWidget.setWidget(mainPageView.asWidget());
    }

}
