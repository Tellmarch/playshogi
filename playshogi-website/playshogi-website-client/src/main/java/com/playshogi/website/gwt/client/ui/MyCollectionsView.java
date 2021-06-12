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
import com.playshogi.website.gwt.client.widget.kifu.CollectionPropertiesPanel;
import com.playshogi.website.gwt.client.widget.kifu.ImportCollectionPopup;
import com.playshogi.website.gwt.client.widget.kifu.ImportKifuPanel;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLHeadingElement;
import org.dominokit.domino.ui.Typography.Strong;
import org.dominokit.domino.ui.alerts.Alert;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.tabs.TabsPanel;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.Arrays;

@Singleton
public class MyCollectionsView extends Composite {

    interface MyEventBinder extends EventBinder<MyCollectionsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private SessionInformation sessionInformation;
    private final ImportCollectionPopup importCollectionPopup = new ImportCollectionPopup();
    private final CollectionPropertiesPanel collectionPropertiesPanel = new CollectionPropertiesPanel();
    private final ImportKifuPanel importKifuPanel = new ImportKifuPanel();
    private final HtmlContentBuilder<HTMLHeadingElement> loggedOutWarning;
    private final HtmlContentBuilder<HTMLHeadingElement> noCollectionsWarning;
    private final ProblemCollectionsTable problemsTable;
    private final GameCollectionsTable gamesTable;

    private EventBus eventBus;

    @Inject
    public MyCollectionsView(final PlaceController placeController, final SessionInformation sessionInformation,
                             final AppPlaceHistoryMapper historyMapper) {
        GWT.log("Creating game collections view");
        this.sessionInformation = sessionInformation;
        problemsTable = new ProblemCollectionsTable(historyMapper, true);
        gamesTable = new GameCollectionsTable(historyMapper, true);

        //TODO add margin to the left side in domino ui

        loggedOutWarning = Elements.h(4).textContent("You are logged out.");
        noCollectionsWarning = Elements.h(4).textContent("You have no Collections. Add some using " +
                "the buttons!");

        HtmlContentBuilder<HTMLDivElement> div = Elements.div();

        div.add(Elements.h(1).textContent("My Collections"));

        div.add(Alert.warning()
                .appendChild(Strong.of("Warning! "))
                .appendChild("feature in development, please keep backups of your game collections to avoid " +
                        "accidental data deletion."));
        div.add(loggedOutWarning);
        div.add(noCollectionsWarning);

        div.add(Button.createPrimary("Create New Collection")
                .addClickListener(evt -> sessionInformation.ifLoggedIn(collectionPropertiesPanel::showInCreateDialog))
                .style().setMarginRight("10px").setMarginBottom("20px"));
        div.add(Button.createPrimary("Import Collection")
                .addClickListener(evt -> sessionInformation.ifLoggedIn(importCollectionPopup::show))
                .style().setMarginRight("10px").setMarginBottom("20px"));

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
        noCollectionsWarning.hidden(true);
        loggedOutWarning.hidden(true);
        importCollectionPopup.activate(eventBus);
        collectionPropertiesPanel.activate(eventBus);
        importKifuPanel.activate(eventBus);
        problemsTable.activate(eventBus);
        gamesTable.activate(eventBus);
    }

    @EventHandler
    public void onListGameCollectionsEvent(final ListGameCollectionsEvent event) {
        GWT.log("MyCollectionsView: handle GameCollectionsEvent:");

        if (event.getMyCollections() != null) {
            gamesTable.setData(Arrays.asList(event.getMyCollections()));
        }

        loggedOutWarning.hidden(true);
        noCollectionsWarning.hidden(true);

        if (sessionInformation.isLoggedIn()) {
            if (event.getMyCollections().length == 0) {
                noCollectionsWarning.hidden(false);
            }
        } else {
            loggedOutWarning.hidden(false);
        }
    }

    @EventHandler
    public void onListProblemCollections(final ListProblemCollectionsEvent event) {
        GWT.log("MyCollectionsView: handle ListProblemCollectionsEvent");
        if (event.getMyCollections() != null) {
            problemsTable.setData(Arrays.asList(event.getMyCollections()));
        }
    }

}
