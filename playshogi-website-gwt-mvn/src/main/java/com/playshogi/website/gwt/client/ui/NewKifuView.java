package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import com.playshogi.website.gwt.client.widget.kifu.KifuEditorPanel;
import com.playshogi.website.gwt.client.widget.kifu.KifuInformationPanel;

@Singleton
public class NewKifuView extends Composite {

    private static final String VIEWKIFU = "viewkifu";
    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;
    private final KifuEditorPanel kifuEditorPanel;
    private final KifuInformationPanel kifuInformationPanel;

    @Inject
    public NewKifuView() {
        GWT.log("Creating view kifu view");
        shogiBoard = new ShogiBoard(VIEWKIFU);
        gameNavigator = new GameNavigator(VIEWKIFU);

        kifuEditorPanel = new KifuEditorPanel(gameNavigator);
        kifuInformationPanel = new KifuInformationPanel();

        shogiBoard.setUpperRightPanel(kifuEditorPanel);
        shogiBoard.setLowerLeftPanel(kifuInformationPanel);

        initWidget(shogiBoard);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating view kifu view");
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
        kifuEditorPanel.activate(eventBus);
        kifuInformationPanel.activate(eventBus);
    }

}
