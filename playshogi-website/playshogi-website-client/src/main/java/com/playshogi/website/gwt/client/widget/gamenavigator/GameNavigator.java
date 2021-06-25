package com.playshogi.website.gwt.client.widget.gamenavigator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameTree;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.controller.NavigationController;
import com.playshogi.website.gwt.client.events.gametree.NavigateBackEvent;
import com.playshogi.website.gwt.client.events.gametree.NavigateForwardEvent;
import com.playshogi.website.gwt.client.events.gametree.NavigateToEndEvent;
import com.playshogi.website.gwt.client.events.gametree.NavigateToStartEvent;

public class GameNavigator extends Composite implements ClickHandler {

    private NavigationController navigationController;

    interface MyEventBinder extends EventBinder<GameNavigator> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final Button firstButton;
    private final Button previousButton;
    private final Button nextButton;
    private final Button lastButton;

    private EventBus eventBus;

    private final String activityId;

    public GameNavigator(final String activityId) {
        this(activityId, new NavigatorConfiguration(), new GameNavigation(new ShogiRulesEngine(), new GameTree()));
    }

    public GameNavigator(final String activityId, final GameNavigation gameNavigation) {
        this(activityId, new NavigatorConfiguration(), gameNavigation);
    }

    private GameNavigator(final String activityId, final NavigatorConfiguration navigatorConfiguration,
                          final GameNavigation gameNavigation) {
        GWT.log(activityId + ": Creating game navigator");

        navigationController = new NavigationController(activityId, navigatorConfiguration, gameNavigation);

        this.activityId = activityId;

        firstButton = new Button("<<");
        previousButton = new Button("<");
        nextButton = new Button(">");
        lastButton = new Button(">>");

        firstButton.addClickHandler(this);
        previousButton.addClickHandler(this);
        nextButton.addClickHandler(this);
        lastButton.addClickHandler(this);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(firstButton);
        horizontalPanel.add(previousButton);
        horizontalPanel.add(nextButton);
        horizontalPanel.add(lastButton);

        initWidget(horizontalPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log(activityId + ": Activating Game Navigator");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, this.eventBus);
        navigationController.activate(eventBus);
    }

    @Override
    public void onClick(final ClickEvent event) {
        Object source = event.getSource();
        if (source == firstButton) {
            eventBus.fireEvent(new NavigateToStartEvent());
        } else if (source == nextButton) {
            eventBus.fireEvent(new NavigateForwardEvent());
        } else if (source == previousButton) {
            eventBus.fireEvent(new NavigateBackEvent());
        } else if (source == lastButton) {
            eventBus.fireEvent(new NavigateToEndEvent());
        }
    }

    public NavigatorConfiguration getNavigatorConfiguration() {
        return navigationController.getNavigatorConfiguration();
    }

    public GameNavigation getGameNavigation() {
        return navigationController.getGameNavigation();
    }


}
