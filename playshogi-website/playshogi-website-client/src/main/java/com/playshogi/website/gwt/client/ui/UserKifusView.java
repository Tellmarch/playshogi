package com.playshogi.website.gwt.client.ui;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.playshogi.website.gwt.client.events.collections.ListGameCollectionsEvent;
import com.playshogi.website.gwt.client.events.collections.ListKifusEvent;
import com.playshogi.website.gwt.client.events.collections.RequestAddKifuToCollectionEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestKifuDeletionEvent;
import com.playshogi.website.gwt.client.place.KifuEditorPlace;
import com.playshogi.website.gwt.client.place.ProblemPlace;
import com.playshogi.website.gwt.client.place.ViewKifuPlace;
import com.playshogi.website.gwt.client.widget.TablePanel;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.KifuDetails;

import java.util.Arrays;

@Singleton
public class UserKifusView extends Composite {


    interface MyEventBinder extends EventBinder<UserKifusView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final PlaceController placeController;
    private final CellTable<KifuDetails> kifusTable;

    private GameCollectionDetails[] myCollections;
    private EventBus eventBus;

    @Inject
    public UserKifusView(final PlaceController placeController, final SessionInformation sessionInformation) {
        this.placeController = placeController;
        GWT.log("Creating UserKifusView");
        FlowPanel flowPanel = new FlowPanel();

        flowPanel.add(new HTML("<br/>"));

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

        collectionsTable.addColumn(new TextColumn<KifuDetails>() {
            @Override
            public String getValue(final KifuDetails object) {
                return String.valueOf(object.getUpdateDate());
            }
        }, "Last Modified");

        ActionCell<KifuDetails> viewActionCell = new ActionCell<>("View", this::viewKifu);

        collectionsTable.addColumn(new Column<KifuDetails, KifuDetails>(viewActionCell) {
            @Override
            public KifuDetails getValue(final KifuDetails gameDetails) {
                return gameDetails;
            }
        }, "View");

        ActionCell<KifuDetails> editActionCell = new ActionCell<>("Edit", this::editKifu);

        collectionsTable.addColumn(new Column<KifuDetails, KifuDetails>(editActionCell) {
            @Override
            public KifuDetails getValue(final KifuDetails gameDetails) {
                return gameDetails;
            }
        }, "Edit");

        ActionCell<KifuDetails> deleteActionCell = new ActionCell<>("Delete",
                this::confirmKifuDeletion);

        collectionsTable.addColumn(new Column<KifuDetails, KifuDetails>(deleteActionCell) {
            @Override
            public KifuDetails getValue(final KifuDetails gameDetails) {
                return gameDetails;
            }
        }, "Delete");

        ActionCell<KifuDetails> collectionActionCell = new ActionCell<>("Add to Collection",
                this::addKifuToCollection);

        collectionsTable.addColumn(new Column<KifuDetails, KifuDetails>(collectionActionCell) {
            @Override
            public KifuDetails getValue(final KifuDetails gameDetails) {
                return gameDetails;
            }
        }, "Add to Collection");

        return collectionsTable;
    }

    private void viewKifu(final KifuDetails details) {
        GWT.log("Going to view kifu" + details.getId());
        if (details.getType() == KifuDetails.KifuType.PROBLEM) {
            placeController.goTo(new ProblemPlace(details.getId()));
        } else {
            placeController.goTo(new ViewKifuPlace(details.getId(), 0));
        }
    }

    private void editKifu(final KifuDetails details) {
        GWT.log("Going to edit kifu" + details.getId());
        placeController.goTo(new KifuEditorPlace(details.getId(), details.getType()));
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating UserKifusView");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    private void confirmKifuDeletion(final KifuDetails details) {
        boolean confirm = Window.confirm("Are you sure you want to delete the kifu " + details.toString() + "?\n" +
                "This is not revertible.");
        if (confirm) {
            GWT.log("Deleting kifu: " + details);
            eventBus.fireEvent(new RequestKifuDeletionEvent(details.getId()));
        } else {
            GWT.log("Deletion cancelled: " + details);
        }
    }

    private void addKifuToCollection(final KifuDetails details) {
        GWT.log("Add Kifu To Collection: " + details.getId());
        DialogBox dialog = createAddToCollectionDialogBox(details);
        dialog.center();
        dialog.show();
    }

    private DialogBox createAddToCollectionDialogBox(final KifuDetails details) {
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Add Kifu to collection");
        dialogBox.setGlassEnabled(true);

        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);


        ListBox listBox = createCollectionsDropdown();
        dialogContents.add(listBox);
        dialogContents.setCellHorizontalAlignment(this, HasHorizontalAlignment.ALIGN_CENTER);

        FlowPanel buttonsPanel = new FlowPanel();
        Button closeButton = new Button("Cancel", (ClickHandler) event -> dialogBox.hide());
        Button saveButton = new Button("Add", (ClickHandler) event -> {
            addKifuToCollection(details, listBox.getSelectedValue());
            dialogBox.hide();
        });
        buttonsPanel.add(closeButton);
        buttonsPanel.add(saveButton);
        dialogContents.add(buttonsPanel);

        dialogContents.setCellHorizontalAlignment(buttonsPanel, HasHorizontalAlignment.ALIGN_RIGHT);

        return dialogBox;
    }

    private void addKifuToCollection(final KifuDetails details, final String selectedValue) {
        GWT.log("Add Kifu " + details.getId() + " to Collection: " + selectedValue);
        eventBus.fireEvent(new RequestAddKifuToCollectionEvent(details.getId(), selectedValue));
    }

    private ListBox createCollectionsDropdown() {
        ListBox list = new ListBox();
        for (GameCollectionDetails collection : myCollections) {
            list.addItem(collection.getName(), collection.getId());
        }

        list.setVisibleItemCount(1);
        return list;
    }

    @EventHandler
    public void onListKifusEvent(final ListKifusEvent event) {
        GWT.log("UserKifusView: handle ListKifusEvent");
        ListDataProvider<KifuDetails> dataProvider = new ListDataProvider<>(Arrays.asList(event.getKifus()));
        dataProvider.addDataDisplay(kifusTable);
    }

    @EventHandler
    public void onListGameCollectionsEvent(final ListGameCollectionsEvent event) {
        GWT.log("GameCollectionsView: handle GameCollectionsEvent");
        myCollections = event.getMyCollections();
    }

}
