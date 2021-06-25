package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.BoardButtons;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import org.dominokit.domino.ui.forms.SwitchButton;
import org.dominokit.domino.ui.themes.Theme;

@Singleton
public class FreeBoardView extends Composite {

    private static final String FREEBOARD = "freeboard";
    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private EventBus eventBus;

    @Inject
    public FreeBoardView(final SessionInformation sessionInformation) {
        GWT.log("Creating free board view");
        shogiBoard = new ShogiBoard(FREEBOARD, sessionInformation.getUserPreferences());
        gameNavigator = new GameNavigator(FREEBOARD);
        shogiBoard.setUpperRightPanel(null);

        FlowPanel panel = new FlowPanel();
        panel.add(BoardButtons.createSettingsWidget(shogiBoard));
        panel.add(BoardButtons.createClearArrowsWidget(shogiBoard));
        panel.add(new ElementWidget(SwitchButton.create()
                .setOffTitle("Play").setOnTitle("Edit").setColor(Theme.DEEP_PURPLE.color())
                .addChangeHandler(b -> shogiBoard.getBoardConfiguration().setPositionEditingMode(b)).element()));

        shogiBoard.setLowerLeftPanel(panel);

        initWidget(shogiBoard);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating free board view");
        this.eventBus = eventBus;
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
    }

}
