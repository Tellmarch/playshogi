package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.record.GameInformation;
import com.playshogi.website.gwt.client.events.kifu.GameInformationChangedEvent;

public class KifuInformationPanel extends Composite {
    interface MyEventBinder extends EventBinder<KifuInformationPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;

    private final TextBox senteTextBox;
    private final TextBox goteTextBox;
    private final TextBox dateTextBox;
    private final TextBox venueTextBox;

    public KifuInformationPanel() {
        FlowPanel verticalPanel = new FlowPanel();

        Grid grid = new Grid(4, 2);
        grid.setHTML(0, 0, "Sente:");
        grid.setHTML(1, 0, "Gote:");
        grid.setHTML(2, 0, "Date:");
        grid.setHTML(3, 0, "Venue:");

        senteTextBox = createTextBox();
        goteTextBox = createTextBox();
        dateTextBox = createTextBox();
        venueTextBox = createTextBox();

        grid.setWidget(0, 1, senteTextBox);
        grid.setWidget(1, 1, goteTextBox);
        grid.setWidget(2, 1, dateTextBox);
        grid.setWidget(3, 1, venueTextBox);

        verticalPanel.add(grid);

        initWidget(verticalPanel);
    }

    private TextBox createTextBox() {
        TextBox senteTextBox = new TextBox();
        senteTextBox.setVisibleLength(13);
        return senteTextBox;
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating kifu information panel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onGameInformationChangedEvent(final GameInformationChangedEvent event) {
        GWT.log("Kifu editor: handle GameInformationChangedEvent");
        refreshInformation(event.getGameInformation());
    }

    private void refreshInformation(final GameInformation gameInformation) {
        GWT.log("Displaying game information: " + gameInformation);
        if (gameInformation != null) {
            senteTextBox.setText(gameInformation.getSente());
            goteTextBox.setText(gameInformation.getGote());
            dateTextBox.setText(gameInformation.getDate());
            venueTextBox.setText(gameInformation.getVenue());
        }
    }

    public GameInformation getGameInformation() {
        GameInformation info = new GameInformation();
        info.setSente(senteTextBox.getText());
        info.setGote(goteTextBox.getText());
        info.setDate(dateTextBox.getText());
        info.setVenue(venueTextBox.getText());
        return info;
    }
}
