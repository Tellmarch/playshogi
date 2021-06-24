package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.tables.KifuTable;
import com.playshogi.website.gwt.client.util.ElementWidget;
import elemental2.dom.HTMLDivElement;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

@Singleton
public class UserKifusView extends Composite {

    interface MyEventBinder extends EventBinder<UserKifusView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final PlaceController placeController;
    private KifuTable kifuTable;

    private EventBus eventBus;

    @Inject
    public UserKifusView(final PlaceController placeController, final SessionInformation sessionInformation,
                         final AppPlaceHistoryMapper historyMapper) {
        this.placeController = placeController;
        GWT.log("Creating UserKifusView");

        kifuTable = new KifuTable(historyMapper, sessionInformation.getUserPreferences());

        HtmlContentBuilder<HTMLDivElement> div = Elements.div();
        div.add(Elements.h(2).textContent("User Kifus"));
        div.add(kifuTable.getTable());

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(new ElementWidget(div.element()));
        scrollPanel.setSize("100%", "100%");
        initWidget(scrollPanel);
    }


    public void activate(final EventBus eventBus) {
        GWT.log("Activating UserKifusView");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        kifuTable.activate(eventBus);
    }
}
