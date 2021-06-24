package com.playshogi.website.gwt.client.ui;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.Handicap;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.kifu.ClearDecorationsEvent;
import com.playshogi.website.gwt.client.i18n.PlayMessages;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.themes.Theme;

@Singleton
public class PlayView extends Composite {

    private static final String PLAY = "play";
    private final ShogiBoard shogiBoard;
    private final GameNavigator gameNavigator;

    private final PlayMessages messages = GWT.create(PlayMessages.class);
    private EventBus eventBus;

    @Inject
    public PlayView(final SessionInformation sessionInformation) {
        GWT.log("Creating Play view");
        shogiBoard = new ShogiBoard(PLAY, sessionInformation.getUserPreferences());
        gameNavigator = new GameNavigator(PLAY);
        shogiBoard.setUpperRightPanel(null);
        shogiBoard.setLowerLeftPanel(createLowerLeftPanel());

        FlowPanel panel = new FlowPanel();

        final ListBox handicaps = new ListBox();

        handicaps.addItem(messages.handicapEven(), Handicap.EVEN.name());
        handicaps.addItem(messages.handicapSente(), Handicap.SENTE.name());
        handicaps.addItem(messages.handicapLance(), Handicap.LANCE.name());
        handicaps.addItem(messages.handicapBishop(), Handicap.BISHOP.name());
        handicaps.addItem(messages.handicapRook(), Handicap.ROOK.name());
        handicaps.addItem(messages.handicapRookLance(), Handicap.ROOK_LANCE.name());
        handicaps.addItem(messages.handicapTwoPieces(), Handicap.TWO_PIECES.name());
        handicaps.addItem(messages.handicapFourPieces(), Handicap.FOUR_PIECES.name());
        handicaps.addItem(messages.handicapSixPieces(), Handicap.SIX_PIECES.name());
        handicaps.addItem(messages.handicapEightPieces(), Handicap.EIGHT_PIECES.name());
        handicaps.addItem(messages.handicapNinePieces(), Handicap.NINE_PIECES.name());
        handicaps.addItem(messages.handicapTenPieces(), Handicap.TEN_PIECES.name());
        handicaps.addItem(messages.handicapThreePawns(), Handicap.THREE_PAWNS.name());
        handicaps.addItem(messages.handicapNakedKing(), Handicap.NAKED_KING.name());

        handicaps.setVisibleItemCount(1);

        Button newGameButton = new Button("New game",
                (ClickHandler) clickEvent -> gameNavigator.reset(Handicap.valueOf(handicaps.getSelectedValue())));

        Button loadSfenButton = new Button("Load SFEN", (ClickHandler) clickEvent -> {
            String sfen = Window.prompt("Enter SFEN:", "9/3+P4k/5pg1g/7pP/p2P4L/4G4/PP3P3/1+B2p4/LNS1K2R1 b " +
                    "G2S3N7Psp");
            gameNavigator.reset(SfenConverter.fromSFEN(sfen));
        });

        panel.add(newGameButton);
        panel.add(loadSfenButton);
        panel.add(handicaps);

        shogiBoard.setUpperRightPanel(panel);

        initWidget(shogiBoard);
    }

    private FlowPanel createLowerLeftPanel() {

        FlowPanel panel = new FlowPanel();
        panel.add(new ElementWidget(org.dominokit.domino.ui.button.Button.createPrimary(Icons.ALL.settings_mdi())
                .setBackground(Theme.DEEP_PURPLE.color()).circle()
                .setTooltip("Settings")
                .addClickListener(e -> shogiBoard.getBoardSettingsPanel().showInDialog()).element()));
        panel.add(new ElementWidget(org.dominokit.domino.ui.button.Button.createPrimary(Icons.ALL.eraser_mdi())
                .setBackground(Theme.DEEP_PURPLE.color()).circle()
                .addClickListener(e -> eventBus.fireEvent(new ClearDecorationsEvent()))
                .setTooltip("Clear arrows")
                .style().setMarginLeft("1em").element()));
        return panel;
    }


    public void activate(final EventBus eventBus) {
        com.google.gwt.core.shared.GWT.log("Activating Play view");
        this.eventBus = eventBus;
        shogiBoard.activate(eventBus);
        gameNavigator.activate(eventBus);
    }

    public GameNavigator getGameNavigator() {
        return gameNavigator;
    }

    public ShogiPosition getPosition() {
        return gameNavigator.getGameNavigation().getPosition();
    }
}
