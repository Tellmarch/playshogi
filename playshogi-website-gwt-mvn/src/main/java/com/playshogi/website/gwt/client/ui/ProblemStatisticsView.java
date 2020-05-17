package com.playshogi.website.gwt.client.ui;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
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
import com.playshogi.website.gwt.client.events.ProblemStatisticsEvent;
import com.playshogi.website.gwt.client.place.TsumePlace;
import com.playshogi.website.gwt.shared.models.ProblemStatisticsDetails;

import java.util.Arrays;
import java.util.Date;

@Singleton
public class ProblemStatisticsView extends Composite {

    private final CellTable<ProblemStatisticsDetails> table;

    interface MyEventBinder extends EventBinder<ProblemStatisticsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    interface Resources extends ClientBundle {
        @Source("com/playshogi/website/gwt/resources/icons/wrong_small.png")
        ImageResource wrongIcon();

        @Source("com/playshogi/website/gwt/resources/icons/right_small.png")
        ImageResource rightIcon();
    }

    private final Resources resources = GWT.create(Resources.class);


    @Inject
    public ProblemStatisticsView(final PlaceController placeController) {
        GWT.log("Creating problem statistics view");
        FlowPanel flowPanel = new FlowPanel();

        flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant(
                "Recent problems:<br>")));

        flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));


        table = new CellTable<>();
        table.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);

        TextColumn<ProblemStatisticsDetails> nameColumn = new TextColumn<ProblemStatisticsDetails>() {
            @Override
            public String getValue(final ProblemStatisticsDetails object) {
                return String.valueOf(object.getProblemId());
            }
        };
        table.addColumn(nameColumn, "Name");

        DateCell dateCell = new DateCell();
        Column<ProblemStatisticsDetails, Date> dateColumn = new Column<ProblemStatisticsDetails, Date>(dateCell) {
            @Override
            public Date getValue(final ProblemStatisticsDetails object) {
                return object.getAttemptedDate();
            }
        };
        table.addColumn(dateColumn, "Attempted on");

        TextColumn<ProblemStatisticsDetails> timeSpentColumn = new TextColumn<ProblemStatisticsDetails>() {
            @Override
            public String getValue(final ProblemStatisticsDetails object) {
                return object.getTimeSpentMs() / 1000 + "s";
            }
        };
        table.addColumn(timeSpentColumn, "Time spent");

        Column<ProblemStatisticsDetails, ImageResource> imageColumn =
                new Column<ProblemStatisticsDetails, ImageResource>(new ImageResourceCell()) {
                    @Override
                    public ImageResource getValue(ProblemStatisticsDetails object) {
                        return (object.getCorrect() == Boolean.TRUE) ? resources.rightIcon() : resources.wrongIcon();
                    }
                };
        table.addColumn(imageColumn, "Result");


        // Add a selection model to handle user selection.
        final SingleSelectionModel<ProblemStatisticsDetails> selectionModel =
                new SingleSelectionModel<>();
        table.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(event -> {
            ProblemStatisticsDetails selected = selectionModel.getSelectedObject();
            if (selected != null) {
                GWT.log("Going to problem " + selected.getProblemId());
                placeController.goTo(new TsumePlace(String.valueOf(selected.getProblemId())));
            }
        });

        flowPanel.add(table);

        initWidget(flowPanel);
    }

    public void activate(final EventBus eventBus) {
        com.google.gwt.core.shared.GWT.log("Activating problem statistics view");
        GWT.log("Activating Problem feedback panel");
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onNewVariation(final ProblemStatisticsEvent event) {
        GWT.log("Problem statistics: handle ProblemStatisticsEvent");
        table.setRowCount(event.getDetails().length);
        table.setRowData(0, Arrays.asList(event.getDetails()));
    }

}
