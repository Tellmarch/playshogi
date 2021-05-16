package com.playshogi.website.gwt.client.ui;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.DeleteGameCollectionEvent;
import com.playshogi.website.gwt.client.events.collections.ListCollectionGamesEvent;
import com.playshogi.website.gwt.client.events.collections.ListGameCollectionsEvent;
import com.playshogi.website.gwt.client.events.collections.RemoveGameFromCollectionEvent;
import com.playshogi.website.gwt.client.place.*;
import com.playshogi.website.gwt.client.widget.TablePanel;
import com.playshogi.website.gwt.client.widget.kifu.CollectionPropertiesPanel;
import com.playshogi.website.gwt.client.widget.kifu.ImportCollectionPanel;
import com.playshogi.website.gwt.client.widget.kifu.ImportKifuPanel;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.GameDetails;

import java.util.Arrays;

@Singleton
public class PublicCollectionsView extends Composite {

    interface MyEventBinder extends EventBinder<PublicCollectionsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final PlaceController placeController;
    private final CellTable<GameCollectionDetails> publicCollectionsTable;
    private final TablePanel publicCollectionsPanel;

    private EventBus eventBus;
    private GameCollectionDetails collectionDetails;

    @Inject
    public PublicCollectionsView(final PlaceController placeController, final SessionInformation sessionInformation) {
        this.placeController = placeController;
        GWT.log("Creating public collections view");
        FlowPanel flowPanel = new FlowPanel();

        publicCollectionsTable = createGameCollectionTable(false);
        publicCollectionsPanel = new TablePanel("Public Game Collections", publicCollectionsTable);
        flowPanel.add(publicCollectionsPanel);

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(flowPanel);
        scrollPanel.setSize("100%", "100%");
        initWidget(scrollPanel);
    }

    private CellTable<GameCollectionDetails> createGameCollectionTable(boolean allowEdit) {
        final CellTable<GameCollectionDetails> collectionsTable;
        collectionsTable = new CellTable<>(20);
        collectionsTable.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);

        TextColumn<GameCollectionDetails> idColumn = new TextColumn<GameCollectionDetails>() {
            @Override
            public String getValue(final GameCollectionDetails object) {
                return String.valueOf(object.getRow());
            }
        };
        collectionsTable.addColumn(idColumn, "Id");

        TextColumn<GameCollectionDetails> nameColumn = new TextColumn<GameCollectionDetails>() {
            @Override
            public String getValue(final GameCollectionDetails object) {
                return String.valueOf(object.getName());
            }
        };
        collectionsTable.addColumn(nameColumn, "Name");

        TextColumn<GameCollectionDetails> dscColumn = new TextColumn<GameCollectionDetails>() {
            @Override
            public String getValue(final GameCollectionDetails object) {
                return String.valueOf(object.getDescription()); //TODO set up max number of characters
            }
        };
        collectionsTable.addColumn(dscColumn, "Description");

        TextColumn<GameCollectionDetails> typeColumn = new TextColumn<GameCollectionDetails>() {
            @Override
            public String getValue(final GameCollectionDetails object) {
                return String.valueOf(object.getType()); //TODO set up max number of characters
            }
        };
        collectionsTable.addColumn(typeColumn, "Type");

        ActionCell<GameCollectionDetails> listActionCell = new ActionCell<>("Open",
                gameCollectionDetails -> placeController.goTo(new CollectionPlace(gameCollectionDetails.getId())));

        collectionsTable.addColumn(new Column<GameCollectionDetails, GameCollectionDetails>(listActionCell) {
            @Override
            public GameCollectionDetails getValue(final GameCollectionDetails gameCollectionDetails) {
                return gameCollectionDetails;
            }
        }, "Open Collection");

//        ActionCell<GameCollectionDetails> exploreActionCell = new ActionCell<>("Explore",
//                gameCollectionDetails -> {
//                    if ("games".equals(gameCollectionDetails.getType())) {
//                        placeController.goTo(new OpeningsPlace(OpeningsPlace.DEFAULT_SFEN,
//                                gameCollectionDetails.getId()));
//                    } else {
//                        placeController.goTo(new ProblemsPlace(gameCollectionDetails.getId(), 0));
//                    }
//                });
//
//        collectionsTable.addColumn(new Column<GameCollectionDetails, GameCollectionDetails>(exploreActionCell) {
//            @Override
//            public GameCollectionDetails getValue(final GameCollectionDetails gameCollectionDetails) {
//                return gameCollectionDetails;
//            }
//        }, "Explore");

        return collectionsTable;
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating game collections view");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onListGameCollectionsEvent(final ListGameCollectionsEvent event) {
        GWT.log("PublicCollectionsView: handle PublicCollectionsEvent:\n" + event);

        new ListDataProvider<>(Arrays.asList(event.getPublicCollections())).addDataDisplay(publicCollectionsTable);
        publicCollectionsPanel.setVisible(true);
    }
}
