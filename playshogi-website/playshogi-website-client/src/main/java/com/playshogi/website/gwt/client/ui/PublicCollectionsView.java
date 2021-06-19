package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListGameCollectionsEvent;
import com.playshogi.website.gwt.client.events.collections.ListProblemCollectionsEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.tables.GameCollectionsTable;
import com.playshogi.website.gwt.client.tables.ProblemCollectionsTable;
import com.playshogi.website.gwt.client.util.ElementWidget;
import elemental2.dom.HTMLDivElement;
import org.dominokit.domino.ui.style.Styles;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.tabs.TabsPanel;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.Arrays;

@Singleton
public class PublicCollectionsView extends Composite {

    interface MyEventBinder extends EventBinder<PublicCollectionsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private SessionInformation sessionInformation;
    private final ProblemCollectionsTable problemsTable;
    private final GameCollectionsTable gamesTable;

    private EventBus eventBus;

    @Inject
    public PublicCollectionsView(final PlaceController placeController, final SessionInformation sessionInformation,
                                 final AppPlaceHistoryMapper historyMapper) {
        GWT.log("Creating public collections view");
        this.sessionInformation = sessionInformation;
        problemsTable = new ProblemCollectionsTable(historyMapper, false, false);
        gamesTable = new GameCollectionsTable(historyMapper, false, false);

        HtmlContentBuilder<HTMLDivElement> div = Elements.div();
        div.css(Styles.padding_20);

        div.add(Elements.h(1).textContent("Public Collections"));

        TabsPanel tabsPanel = TabsPanel.create()
                .appendChild(Tab.create("Game Collections").appendChild(gamesTable.getTable()))
                .appendChild(Tab.create("Problems Collections").appendChild(problemsTable.getTable()));

        div.add(tabsPanel);
        div.style("overflow:scroll; height:100%;");

        initWidget(new ElementWidget(div.element()));
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating game collections view");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        problemsTable.activate(eventBus);
        gamesTable.activate(eventBus);
    }

    @EventHandler
    public void onListGameCollectionsEvent(final ListGameCollectionsEvent event) {
        GWT.log("PublicCollectionsView: handle GameCollectionsEvent:");

        if (event.getPublicCollections() != null) {
            gamesTable.setData(Arrays.asList(event.getPublicCollections()));
        }
    }

    @EventHandler
    public void onListProblemCollections(final ListProblemCollectionsEvent event) {
        GWT.log("PublicCollectionsView: handle ListProblemCollectionsEvent");
        if (event.getPublicCollections() != null) {
            problemsTable.setData(Arrays.asList(event.getPublicCollections()));
        }
    }

}
