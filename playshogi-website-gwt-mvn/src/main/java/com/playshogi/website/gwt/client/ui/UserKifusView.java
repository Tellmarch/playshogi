package com.playshogi.website.gwt.client.ui;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListKifusEvent;
import com.playshogi.website.gwt.client.place.ViewKifuPlace;
import com.playshogi.website.gwt.client.widget.TablePanel;
import com.playshogi.website.gwt.shared.models.KifuDetails;

import java.util.Arrays;

@Singleton
public class UserKifusView extends Composite {

    interface MyEventBinder extends EventBinder<UserKifusView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final PlaceController placeController;
    private final CellTable<KifuDetails> kifusTable;

    private EventBus eventBus;

    @Inject
    public UserKifusView(final PlaceController placeController, final SessionInformation sessionInformation) {
        this.placeController = placeController;
        GWT.log("Creating UserKifusView");
        FlowPanel flowPanel = new FlowPanel();

        kifusTable = createKifusTable();
        TablePanel kifusPanel = new TablePanel("My Kifus", kifusTable);
        flowPanel.add(kifusPanel);

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(flowPanel);
        scrollPanel.setSize("100%", "100%");
        initWidget(scrollPanel);
    }

    private CellTable<KifuDetails> createKifusTable() {
        final CellTable<KifuDetails> collectionsTable;
        collectionsTable = new CellTable<>(20);
        collectionsTable.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);

        collectionsTable.addColumn(new TextColumn<KifuDetails>() {
            @Override
            public String getValue(final KifuDetails object) {
                return String.valueOf(object.getId());
            }
        }, "Id");

        collectionsTable.addColumn(new TextColumn<KifuDetails>() {
            @Override
            public String getValue(final KifuDetails object) {
                return String.valueOf(object.getName());
            }
        }, "Name");

        collectionsTable.addColumn(new TextColumn<KifuDetails>() {
            @Override
            public String getValue(final KifuDetails object) {
                return String.valueOf(object.getType());
            }
        }, "Type");

        ActionCell<KifuDetails> viewActionCell = new ActionCell<>("View", this::viewKifu);

        collectionsTable.addColumn(new Column<KifuDetails, KifuDetails>(viewActionCell) {
            @Override
            public KifuDetails getValue(final KifuDetails gameDetails) {
                return gameDetails;
            }
        }, "View");

        ActionCell<KifuDetails> deleteActionCell = new ActionCell<>("Delete",
                this::confirmKifuDeletion);

        collectionsTable.addColumn(new Column<KifuDetails, KifuDetails>(deleteActionCell) {
            @Override
            public KifuDetails getValue(final KifuDetails gameDetails) {
                return gameDetails;
            }
        }, "Delete");

        return collectionsTable;
    }

    private void viewKifu(final KifuDetails details) {
        GWT.log("Going to kifu" + details.getId());
        placeController.goTo(new ViewKifuPlace(details.getId(), 0));
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating game collections view");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onListKifusEvent(final ListKifusEvent event) {
        GWT.log("UserKifusView: handle ListKifusEvent");
        ListDataProvider<KifuDetails> dataProvider = new ListDataProvider<>(Arrays.asList(event.getKifus()));
        dataProvider.addDataDisplay(kifusTable);
    }

    public void confirmKifuDeletion(final KifuDetails details) {
        boolean confirm = Window.confirm("Are you sure you want to delete the kifu " + details.toString() + "?\n" +
                "This is not revertible.");
        if (confirm) {
            GWT.log("Deleting kifu: " + details);
            // TODO
        } else {
            GWT.log("Deletion cancelled: " + details);
        }
    }
}
