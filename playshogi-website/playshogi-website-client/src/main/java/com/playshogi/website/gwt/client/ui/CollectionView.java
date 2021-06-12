package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListCollectionGamesEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.tables.GameTable;
import com.playshogi.website.gwt.client.util.ElementWidget;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLHeadingElement;
import org.dominokit.domino.ui.style.Styles;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class CollectionView extends Composite {

    private final HtmlContentBuilder<HTMLHeadingElement> collectionHeading;
    private final HtmlContentBuilder<HTMLHeadingElement> collectionDescription;
    private final GameTable gameTable;

    interface MyEventBinder extends EventBinder<CollectionView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    @Inject
    public CollectionView(final SessionInformation sessionInformation, final AppPlaceHistoryMapper historyMapper) {
        gameTable = new GameTable(historyMapper, sessionInformation.getUserPreferences(), true);

        HtmlContentBuilder<HTMLDivElement> root = Elements.div();
        root.css(Styles.padding_20);
        collectionHeading = Elements.h(2).textContent("Heading");
        collectionDescription = Elements.h(4).textContent("DSC");
        root.add(collectionHeading);
        root.add(collectionDescription);
        root.add(gameTable.getTable());

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(new ElementWidget(root.element()));
        scrollPanel.setSize("100%", "100%");
        initWidget(scrollPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating CollectionView");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        gameTable.activate(eventBus);
    }

    @EventHandler
    public void onCollectionList(final ListCollectionGamesEvent event) {
        GWT.log("CollectionView: handle ListCollectionGamesEvent");
        //TODO add loading screen
        if (event.getDetails() != null) {
            gameTable.setData(Arrays.asList(event.getDetails()));
        }
        collectionHeading.textContent(event.getCollectionDetails().getName());
        collectionDescription.textContent(event.getCollectionDetails().getDescription());
    }

}
