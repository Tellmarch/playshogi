package com.playshogi.website.gwt.client.ui;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
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
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.place.GameCollectionsPlace;
import com.playshogi.website.gwt.client.place.OpeningsPlace;
import com.playshogi.website.gwt.client.place.ProblemsPlace;
import com.playshogi.website.gwt.client.place.ViewKifuPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.TablePanel;
import com.playshogi.website.gwt.client.widget.kifu.CollectionPropertiesPanel;
import com.playshogi.website.gwt.client.widget.kifu.ImportCollectionPanel;
import com.playshogi.website.gwt.client.widget.kifu.ImportKifuPanel;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.GameDetails;
import org.dominokit.domino.ui.Typography.Strong;
import org.dominokit.domino.ui.alerts.Alert;
import org.dominokit.domino.ui.labels.Label;
import org.jboss.elemento.Elements;

import java.util.Arrays;

@Singleton
public class MyCollectionsView extends Composite {

    interface MyEventBinder extends EventBinder<MyCollectionsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final PlaceController placeController;
    private SessionInformation sessionInformation;
    private final CellTable<GameCollectionDetails> myCollectionsTable;
    private final TablePanel myCollectionsPanel;
    private final ImportCollectionPanel importCollectionPanel = new ImportCollectionPanel();
    private final CollectionPropertiesPanel collectionPropertiesPanel = new CollectionPropertiesPanel();
    private final ImportKifuPanel importKifuPanel = new ImportKifuPanel();
    private final ElementWidget loggedOutWarning;
    private final ElementWidget noCollectionsWarning;
    private final ElementWidget header;

    private EventBus eventBus;
    private GameCollectionDetails collectionDetails;

    @Inject
    public MyCollectionsView(final PlaceController placeController, final SessionInformation sessionInformation) {
        this.placeController = placeController;
        this.sessionInformation = sessionInformation;
        GWT.log("Creating game collections view");
        FlowPanel flowPanel = new FlowPanel();

        //TODO add margin to the left side in domino ui

        header = new ElementWidget(Elements.h(1).textContent("My Collections").element());
        loggedOutWarning = new ElementWidget(Elements.h(4).textContent("You are logged out.").element());
        noCollectionsWarning = new ElementWidget(Elements.h(4).textContent("You have no Collections. Add some using buttons!").element());
        flowPanel.add(header);
        flowPanel.add(new ElementWidget( Alert.warning()
                .appendChild(Strong.of("Warning! "))
                .appendChild("feature in development, please keep backups of your game collections to avoid accidental data deletion.").element()));
        flowPanel.add(loggedOutWarning);
        flowPanel.add(noCollectionsWarning);


//        flowPanel.add(new HTML("<b>Warning</b>: feature in development, please keep backups of your game collections" +
//                ".</br>"));
//        flowPanel.add(new HTML("Expect bugs, e.g. accidental data deletion or bad access control.</br>"));
//
//        flowPanel.add(new HTML("<br/>"));

        HorizontalPanel buttonsPanel = new HorizontalPanel();
        buttonsPanel.setSpacing(5);

        Button createButton = new Button("Create New Collection");
        createButton.addClickHandler(clickEvent -> sessionInformation.ifLoggedIn(collectionPropertiesPanel::showInCreateDialog));
        buttonsPanel.add(createButton);

//        Button importButton = new Button("Import Collection");
//        importButton.addClickHandler(clickEvent -> sessionInformation.ifLoggedIn(importCollectionPanel::showInDialog));
//        buttonsPanel.add(importButton);

        flowPanel.add(buttonsPanel);

        flowPanel.add(new HTML("<br/>"));

        myCollectionsTable = createGameCollectionTable(true);
        myCollectionsPanel = new TablePanel("My Collections List", myCollectionsTable);
        flowPanel.add(myCollectionsPanel);

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


    private void viewKifu(final GameDetails details) {
        GWT.log("Going to kifu" + details.getKifuId());
        placeController.goTo(new ViewKifuPlace(details.getKifuId(), 0));
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating game collections view");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        myCollectionsPanel.setVisible(false);
        noCollectionsWarning.setVisible(false);
        loggedOutWarning.setVisible(false);
        importCollectionPanel.activate(eventBus);
        collectionPropertiesPanel.activate(eventBus);
        importKifuPanel.activate(eventBus);
    }

    @EventHandler
    public void onListGameCollectionsEvent(final ListGameCollectionsEvent event) {
        GWT.log("GameCollectionsView: handle GameCollectionsEvent:\n" + event);

        new ListDataProvider<>(Arrays.asList(event.getMyCollections())).addDataDisplay(myCollectionsTable);

        loggedOutWarning.setVisible(false);
        noCollectionsWarning.setVisible(false);
        myCollectionsPanel.setVisible(false);

        if(sessionInformation.isLoggedIn()){
            if(event.getMyCollections().length > 0){
                myCollectionsPanel.setVisible(true);
            }else{
                noCollectionsWarning.setVisible(true);
            }

        }else{
            loggedOutWarning.setVisible(true);
        }

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

}
