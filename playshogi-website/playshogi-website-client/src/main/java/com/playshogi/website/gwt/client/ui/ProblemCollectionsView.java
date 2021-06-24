package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.collections.AddSearchEvent;
import com.playshogi.website.gwt.client.events.collections.ListProblemCollectionsEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.tables.ProblemCollectionsTable;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import elemental2.dom.HTMLDivElement;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.Arrays;

@Singleton
public class ProblemCollectionsView extends Composite {

    interface MyEventBinder extends EventBinder<ProblemCollectionsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ProblemCollectionsTable problemsTable;

    @Inject
    public ProblemCollectionsView(final AppPlaceHistoryMapper historyMapper) {
        GWT.log("Creating public collections view");

        problemsTable = new ProblemCollectionsTable(historyMapper, false, false);

        HtmlContentBuilder<HTMLDivElement> div = Elements.div();
        div.add(Elements.h(2).textContent("Public Problem Collections"));
        div.add(problemsTable.getTable());

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(new ElementWidget(div.element()));
        scrollPanel.setSize("100%", "100%");
        initWidget(scrollPanel);
    }


    public void activate(final EventBus eventBus) {
        GWT.log("Activating game collections view");
        eventBinder.bindEventHandlers(this, eventBus);
        problemsTable.activate(eventBus);
    }

    @EventHandler
    public void onListProblemCollections(final ListProblemCollectionsEvent event) {
        GWT.log("ProblemCollectionsView: handle ListProblemCollectionsEvent:\n" + event);

        ProblemCollectionDetails[] collections = event.getPublicCollections();
        if (collections != null) {
            problemsTable.setData(Arrays.asList(collections));
        }
    }

    @EventHandler
    public void onAddSearch(final AddSearchEvent event) {
        GWT.log("ProblemCollectionsView: handle onAddSearch:\n" + event);

        problemsTable.addSearch(event.getSearch());
    }
}
