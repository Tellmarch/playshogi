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
import com.playshogi.website.gwt.client.place.GameCollectionsPlace;
import com.playshogi.website.gwt.client.place.OpeningsPlace;
import com.playshogi.website.gwt.client.place.ProblemsPlace;
import com.playshogi.website.gwt.client.place.ViewKifuPlace;
import com.playshogi.website.gwt.client.widget.TablePanel;
import com.playshogi.website.gwt.client.widget.kifu.CollectionPropertiesPanel;
import com.playshogi.website.gwt.client.widget.kifu.ImportCollectionPanel;
import com.playshogi.website.gwt.client.widget.kifu.ImportKifuPanel;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.GameDetails;

import java.util.Arrays;

@Singleton
public class CollectionView extends Composite {

    interface MyEventBinder extends EventBinder<CollectionView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final PlaceController placeController;
    private final CellTable<GameCollectionDetails> myCollectionsTable;
    private final CellTable<GameCollectionDetails> publicCollectionsTable;
    private final CellTable<GameDetails> kifusTable;
    private final TablePanel myCollectionsPanel;
    private final TablePanel publicCollectionsPanel;
    private final TablePanel kifusPanel;
    private final ImportCollectionPanel importCollectionPanel = new ImportCollectionPanel();
    private final CollectionPropertiesPanel collectionPropertiesPanel = new CollectionPropertiesPanel();
    private final ImportKifuPanel importKifuPanel = new ImportKifuPanel();

    private EventBus eventBus;
    private GameCollectionDetails collectionDetails;

    @Inject
    public CollectionView(final PlaceController placeController, final SessionInformation sessionInformation) {
        this.placeController = placeController;
        GWT.log("Creating game collections view");
        FlowPanel flowPanel = new FlowPanel();

        flowPanel.add(new HTML("<b>Warning</b>: feature in development, please keep backups of your game collections" +
                ".</br>"));
        flowPanel.add(new HTML("Expect bugs, e.g. accidental data deletion or bad access control.</br>"));

        flowPanel.add(new HTML("<br/>"));

        HorizontalPanel buttonsPanel = new HorizontalPanel();
        buttonsPanel.setSpacing(5);

        Button createButton = new Button("Create New Game Collection");
        createButton.addClickHandler(clickEvent -> sessionInformation.ifLoggedIn(collectionPropertiesPanel::showInCreateDialog));
        buttonsPanel.add(createButton);

        Button importButton = new Button("Import Game Collection");
        importButton.addClickHandler(clickEvent -> sessionInformation.ifLoggedIn(importCollectionPanel::showInDialog));
        buttonsPanel.add(importButton);


        flowPanel.add(buttonsPanel);

        flowPanel.add(new HTML("<br/>"));

        myCollectionsTable = createGameCollectionTable(true);
        myCollectionsPanel = new TablePanel("My Game Collections", myCollectionsTable);
        flowPanel.add(myCollectionsPanel);

        publicCollectionsTable = createGameCollectionTable(false);
        publicCollectionsPanel = new TablePanel("Public Game Collections", publicCollectionsTable);
        flowPanel.add(publicCollectionsPanel);

        kifusTable = createKifusTable();
        kifusPanel = new TablePanel("Games in Collection: ", kifusTable);
        flowPanel.add(kifusPanel);

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

        if (allowEdit) {
            ActionCell<GameCollectionDetails> addGameActionCell = new ActionCell<>("Add Game",
                    gameCollectionDetails -> importKifuPanel.showInDialog(gameCollectionDetails.getId()));

            collectionsTable.addColumn(new Column<GameCollectionDetails, GameCollectionDetails>(addGameActionCell) {
                @Override
                public GameCollectionDetails getValue(final GameCollectionDetails gameCollectionDetails) {
                    return gameCollectionDetails;
                }
            }, "Add Game");
        }

        ActionCell<GameCollectionDetails> exploreActionCell = new ActionCell<>("Explore",
                gameCollectionDetails -> {
                    if ("games".equals(gameCollectionDetails.getType())) {
                        placeController.goTo(new OpeningsPlace(OpeningsPlace.DEFAULT_SFEN,
                                gameCollectionDetails.getId()));
                    } else {
                        placeController.goTo(new ProblemsPlace(gameCollectionDetails.getId(), 0));
                    }
                });

        collectionsTable.addColumn(new Column<GameCollectionDetails, GameCollectionDetails>(exploreActionCell) {
            @Override
            public GameCollectionDetails getValue(final GameCollectionDetails gameCollectionDetails) {
                return gameCollectionDetails;
            }
        }, "Explore");

        if (allowEdit) {
            ActionCell<GameCollectionDetails> propertiesActionCell = new ActionCell<>("Properties",
                    collectionPropertiesPanel::showInUpdateDialog);

            collectionsTable.addColumn(new Column<GameCollectionDetails, GameCollectionDetails>(propertiesActionCell) {
                @Override
                public GameCollectionDetails getValue(final GameCollectionDetails gameCollectionDetails) {
                    return gameCollectionDetails;
                }
            }, "Properties");
        }

        if (allowEdit) {
            ActionCell<GameCollectionDetails> deleteActionCell = new ActionCell<>("Delete Collection",
                    this::confirmCollectionDeletion);

            collectionsTable.addColumn(new Column<GameCollectionDetails, GameCollectionDetails>(deleteActionCell) {
                @Override
                public GameCollectionDetails getValue(final GameCollectionDetails gameCollectionDetails) {
                    return gameCollectionDetails;
                }
            }, "Delete");
        }

        return collectionsTable;
    }

    private CellTable<GameDetails> createKifusTable() {
        final CellTable<GameDetails> collectionsTable;
        collectionsTable = new CellTable<>(20);
        collectionsTable.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);

        collectionsTable.addColumn(new TextColumn<GameDetails>() {
            @Override
            public String getValue(final GameDetails object) {
                return String.valueOf(object.getId());
            }
        }, "Id");

        collectionsTable.addColumn(new TextColumn<GameDetails>() {
            @Override
            public String getValue(final GameDetails object) {
                return String.valueOf(object.getSente());
            }
        }, "Sente");

        collectionsTable.addColumn(new TextColumn<GameDetails>() {
            @Override
            public String getValue(final GameDetails object) {
                return String.valueOf(object.getGote());
            }
        }, "Gote");

        ActionCell<GameDetails> viewActionCell = new ActionCell<>("View",
                this::viewKifu);

        collectionsTable.addColumn(new Column<GameDetails, GameDetails>(viewActionCell) {
            @Override
            public GameDetails getValue(final GameDetails gameDetails) {
                return gameDetails;
            }
        }, "View");

        ActionCell<GameDetails> deleteActionCell = new ActionCell<>("Remove",
                this::confirmKifuRemoval);

        collectionsTable.addColumn(new Column<GameDetails, GameDetails>(deleteActionCell) {
            @Override
            public GameDetails getValue(final GameDetails gameDetails) {
                return gameDetails;
            }
        }, "Remove");

        return collectionsTable;
    }

    private void viewKifu(final GameDetails details) {
        GWT.log("Going to kifu" + details.getKifuId());
        placeController.goTo(new ViewKifuPlace(details.getKifuId(), 0));
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating game collections view");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        myCollectionsPanel.setVisible(false);
        kifusPanel.setVisible(false);
        importCollectionPanel.activate(eventBus);
        collectionPropertiesPanel.activate(eventBus);
        importKifuPanel.activate(eventBus);
    }

    @EventHandler
    public void onListGameCollectionsEvent(final ListGameCollectionsEvent event) {
        GWT.log("GameCollectionsView: handle GameCollectionsEvent:\n" + event);

        new ListDataProvider<>(Arrays.asList(event.getMyCollections())).addDataDisplay(myCollectionsTable);
        myCollectionsPanel.setVisible(event.getMyCollections().length > 0);

        new ListDataProvider<>(Arrays.asList(event.getPublicCollections())).addDataDisplay(publicCollectionsTable);
        publicCollectionsPanel.setVisible(true);
    }

    @EventHandler
    public void onListCollectionGamesEvent(final ListCollectionGamesEvent event) {
        GWT.log("GameCollectionsView: handle GameCollectionsEvent");
        ListDataProvider<GameDetails> dataProvider = new ListDataProvider<>(Arrays.asList(event.getDetails()));
        dataProvider.addDataDisplay(kifusTable);
        kifusPanel.setVisible(true);
        kifusPanel.setTableTitle(event.getCollectionDetails().getName());
        collectionDetails = event.getCollectionDetails();
    }

    private void confirmCollectionDeletion(final GameCollectionDetails details) {
        boolean confirm = Window.confirm("Are you sure you want to delete the collection " + details.getName() + "?\n" +
                "All the games from the collection will be inaccessible. This is not revertible.");
        if (confirm) {
            GWT.log("Deleting collection: " + details);
            eventBus.fireEvent(new DeleteGameCollectionEvent(details.getId()));
        } else {
            GWT.log("Deletion cancelled: " + details);
        }
    }

    public void confirmKifuRemoval(final GameDetails details) {
        boolean confirm = Window.confirm("Are you sure you want to remove the game " + details.toString() + "?\n" +
                "This is not revertible.");
        if (confirm) {
            GWT.log("Removing game: " + details);
            eventBus.fireEvent(new RemoveGameFromCollectionEvent(details.getId(), collectionDetails.getId()));
        } else {
            GWT.log("Removed game: " + details);
        }
    }
}
