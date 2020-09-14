package com.playshogi.website.gwt.client.ui;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.ListCollectionGamesEvent;
import com.playshogi.website.gwt.client.events.ListGameCollectionsEvent;
import com.playshogi.website.gwt.client.place.GameCollectionsPlace;
import com.playshogi.website.gwt.client.place.OpeningsPlace;
import com.playshogi.website.gwt.client.place.ViewKifuPlace;
import com.playshogi.website.gwt.client.widget.kifu.ImportCollectionPanel;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.KifuDetails;

import java.util.Arrays;

import static com.google.gwt.user.client.ui.HasHorizontalAlignment.ALIGN_CENTER;

@Singleton
public class GameCollectionsView extends Composite {

    private final PlaceController placeController;

    interface MyEventBinder extends EventBinder<GameCollectionsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final CellTable<GameCollectionDetails> collectionsTable;
    private final CellTable<KifuDetails> kifusTable;
    private final VerticalPanel collectionsPanel;
    private final VerticalPanel kifusPanel;
    private final ImportCollectionPanel importCollectionPanel = new ImportCollectionPanel();

    @Inject
    public GameCollectionsView(final PlaceController placeController) {
        this.placeController = placeController;
        GWT.log("Creating game collections view");
        FlowPanel flowPanel = new FlowPanel();

        flowPanel.add(new HTML("<br/>"));

        Button importButton = new Button("Import Game Collection");
        importButton.addClickHandler(clickEvent -> importCollectionPanel.showInDialog());
        flowPanel.add(importButton);

        flowPanel.add(new HTML("<br/>"));


        collectionsPanel = new VerticalPanel();
        collectionsPanel.setHorizontalAlignment(ALIGN_CENTER);
        collectionsTable = createGameCollectionTable();
        SimplePager collectionsPager = new SimplePager();
        collectionsPager.setDisplay(collectionsTable);
        collectionsPanel.add(collectionsTable);
        collectionsPanel.add(collectionsPager);

        kifusPanel = new VerticalPanel();
        kifusPanel.setHorizontalAlignment(ALIGN_CENTER);
        kifusTable = createKifusTable();
        SimplePager kifusPager = new SimplePager();
        kifusPager.setDisplay(kifusTable);
        kifusPanel.add(new HTML("<br/><br/>Games in collection:<br/><br/>"));
        kifusPanel.add(kifusTable);
        kifusPanel.add(kifusPager);

        flowPanel.add(collectionsPanel);
        flowPanel.add(kifusPanel);

        initWidget(flowPanel);
    }

    private CellTable<GameCollectionDetails> createGameCollectionTable() {
        final CellTable<GameCollectionDetails> collectionsTable;
        collectionsTable = new CellTable<>(20);
        collectionsTable.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);

        TextColumn<GameCollectionDetails> idColumn = new TextColumn<GameCollectionDetails>() {
            @Override
            public String getValue(final GameCollectionDetails object) {
                return String.valueOf(object.getId());
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

        ActionCell<GameCollectionDetails> listActionCell = new ActionCell<>("List Games",
                gameCollectionDetails -> placeController.goTo(new GameCollectionsPlace(gameCollectionDetails.getId())));

        collectionsTable.addColumn(new Column<GameCollectionDetails, GameCollectionDetails>(listActionCell) {
            @Override
            public GameCollectionDetails getValue(final GameCollectionDetails gameCollectionDetails) {
                return gameCollectionDetails;
            }
        }, "List Games");

        ActionCell<GameCollectionDetails> exploreActionCell = new ActionCell<>("Explore",
                gameCollectionDetails -> placeController.goTo(new OpeningsPlace(OpeningsPlace.DEFAULT_SFEN,
                        gameCollectionDetails.getId())));

        collectionsTable.addColumn(new Column<GameCollectionDetails, GameCollectionDetails>(exploreActionCell) {
            @Override
            public GameCollectionDetails getValue(final GameCollectionDetails gameCollectionDetails) {
                return gameCollectionDetails;
            }
        }, "Explore");

        return collectionsTable;
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
                return String.valueOf(object.getSente());
            }
        }, "Sente");

        collectionsTable.addColumn(new TextColumn<KifuDetails>() {
            @Override
            public String getValue(final KifuDetails object) {
                return String.valueOf(object.getGote());
            }
        }, "Gote");

        final SingleSelectionModel<KifuDetails> selectionModel = new SingleSelectionModel<>();
        collectionsTable.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(event -> {
            KifuDetails selected = selectionModel.getSelectedObject();
            if (selected != null) {
                GWT.log("Going to kifu" + selected.getId());
                placeController.goTo(new ViewKifuPlace(selected.getId(), 0));
            }
        });

        return collectionsTable;
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating game collections view");
        eventBinder.bindEventHandlers(this, eventBus);
        collectionsPanel.setVisible(false);
        kifusPanel.setVisible(false);
        importCollectionPanel.activate(eventBus);
    }

    @EventHandler
    public void onListGameCollectionsEvent(final ListGameCollectionsEvent event) {
        GWT.log("GameCollectionsView: handle GameCollectionsEvent");
        ListDataProvider<GameCollectionDetails> dataProvider =
                new ListDataProvider<>(Arrays.asList(event.getDetails()));
        dataProvider.addDataDisplay(collectionsTable);
        collectionsPanel.setVisible(true);
    }

    @EventHandler
    public void onListCollectionGamesEvent(final ListCollectionGamesEvent event) {
        GWT.log("GameCollectionsView: handle GameCollectionsEvent");
        ListDataProvider<KifuDetails> dataProvider = new ListDataProvider<>(Arrays.asList(event.getDetails()));
        dataProvider.addDataDisplay(kifusTable);
        kifusPanel.setVisible(true);
    }
}
