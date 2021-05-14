package com.playshogi.website.gwt.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.widget.navigation.NavigationMenu;
import com.playshogi.website.gwt.client.widget.navigation.NavigationMenu2;

@Singleton
public class PlayShogiAppShell extends Composite {

    private final SimplePanel appWidget = new SimplePanel();

    @Inject
    public PlayShogiAppShell(final ActivityManager activityManager, final NavigationMenu2 navigationMenu) {

        DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
        dockLayoutPanel.addNorth(navigationMenu, 2.2);
        dockLayoutPanel.add(appWidget);

        initWidget(dockLayoutPanel);

        activityManager.setDisplay(appWidget);
    }
}
