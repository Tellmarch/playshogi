package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.GameCollectionsEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;

import java.util.Arrays;

@Singleton
public class GameCollectionsView extends Composite {

    interface MyEventBinder extends EventBinder<GameCollectionsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final CellTable<GameCollectionDetails> table;

    @Inject
    public GameCollectionsView(final AppPlaceHistoryMapper historyMapper) {
        GWT.log("Creating game collections view");
        FlowPanel flowPanel = new FlowPanel();

        flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("Game collections<br/>")));


        table = new CellTable<>();
        table.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);

        TextColumn<GameCollectionDetails> idColumn = new TextColumn<GameCollectionDetails>() {
            @Override
            public String getValue(final GameCollectionDetails object) {
                return String.valueOf(object.getId());
            }
        };
        table.addColumn(idColumn, "Id");

        TextColumn<GameCollectionDetails> nameColumn = new TextColumn<GameCollectionDetails>() {
            @Override
            public String getValue(final GameCollectionDetails object) {
                return String.valueOf(object.getName());
            }
        };
        table.addColumn(nameColumn, "Name");

        final SingleSelectionModel<GameCollectionDetails> selectionModel = new SingleSelectionModel<>();
        table.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(event -> {
            GameCollectionDetails selected = selectionModel.getSelectedObject();
            if (selected != null) {
                GWT.log("Going to game collection " + selected.getId());
//                placeController.goTo(new TsumePlace(String.valueOf(selected.getProblemId())));
            }
        });

        flowPanel.add(table);

        initWidget(flowPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating game collections view");
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onProblemStatisticsEvent(final GameCollectionsEvent event) {
        GWT.log("GameCollectionsView: handle GameCollectionsEvent");
        table.setRowCount(event.getDetails().length);
        table.setRowData(0, Arrays.asList(event.getDetails()));
    }

}
