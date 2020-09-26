package com.playshogi.website.gwt.client.widget.gamenavigator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameTree;
import com.playshogi.library.shogi.models.GameRecordUtils;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.*;

import java.util.Objects;

public class GameNavigator extends Composite implements ClickHandler {

    interface MyEventBinder extends EventBinder<GameNavigator> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final Button firstButton;
    private final Button previousButton;
    private final Button nextButton;
    private final Button lastButton;
    private final GameNavigation<ShogiPosition> gameNavigation;
    private final ShogiRulesEngine shogiRulesEngine;

    private EventBus eventBus;

    private final NavigatorConfiguration navigatorConfiguration;

    private final String activityId;

    public GameNavigator(final String activityId) {
        this(activityId, new NavigatorConfiguration());
    }

    private GameNavigator(final String activityId, final NavigatorConfiguration navigatorConfiguration) {
        GWT.log(activityId + ": Creating game navigator");

        this.activityId = activityId;
        shogiRulesEngine = new ShogiRulesEngine();
        GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(shogiRulesEngine, new GameTree(),
                ShogiInitialPositionFactory.createInitialPosition());

        this.navigatorConfiguration = navigatorConfiguration;
        this.gameNavigation = gameNavigation;
        firstButton = new Button("<<");
        previousButton = new Button("<");
        nextButton = new Button(">");
        lastButton = new Button(">>");

        firstButton.addClickHandler(this);
        previousButton.addClickHandler(this);
        nextButton.addClickHandler(this);
        lastButton.addClickHandler(this);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(firstButton);
        horizontalPanel.add(previousButton);
        horizontalPanel.add(nextButton);
        horizontalPanel.add(lastButton);

        initWidget(horizontalPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log(activityId + ": Activating Game Navigator");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, this.eventBus);
        Scheduler.get().scheduleDeferred(() -> {
            GWT.log(activityId + ": Game Navigator defered execution");
            firePositionChanged(false);
        });
    }

    @EventHandler
    public void onGameTreeChanged(final GameTreeChangedEvent gameTreeChangedEvent) {
        GWT.log(activityId + ": Handling game tree changed event");
        GameTree gameTree = gameTreeChangedEvent.getGameTree();
        gameNavigation.setGameTree(gameTree, GameRecordUtils.getinitialPosition(gameTree),
                gameTreeChangedEvent.getGoToMove());

        firePositionChanged(false);
    }

    @EventHandler
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
        GWT.log(activityId + ": Handling move played event");
        ShogiMove move = movePlayedEvent.getMove();
        String usfMove = UsfMoveConverter.toUsfString(move);
        GWT.log("Move played: " + usfMove);
        boolean existingMove = gameNavigation.hasMoveInCurrentPosition(move);
        boolean mainMove = Objects.equals(gameNavigation.getMainVariationMove(), move);

        gameNavigation.addMove(move);
        if (!existingMove) {
            GWT.log("New variation");
            boolean positionCheckmate = shogiRulesEngine.isPositionCheckmate(gameNavigation.getPosition());
            if (move instanceof DropMove) {
                DropMove dropMove = (DropMove) move;
                if (dropMove.getPieceType() == PieceType.PAWN) {
                    positionCheckmate = false;
                }
            }
            GWT.log("Checkmate: " + positionCheckmate);
            eventBus.fireEvent(new NewVariationPlayedEvent(positionCheckmate));
        } else if (gameNavigation.isEndOfVariation()) {
            eventBus.fireEvent(new EndOfVariationReachedEvent(mainMove));
            // } else if (isSenteToPlay() &&
            // !boardConfiguration.isPlaySenteMoves()) {
            // gameNavigation.moveForward();
        } else if (!isSenteToPlay() && navigatorConfiguration.isProblemMode()) {
            gameNavigation.moveForward();
        }

        firePositionChanged(true);
    }

    private boolean isSenteToPlay() {
        return gameNavigation.getPosition().isSenteToPlay();
    }

    @Override
    public void onClick(final ClickEvent event) {
        Object source = event.getSource();
        if (source == firstButton) {
            gameNavigation.moveToStart();
            eventBus.fireEvent(new UserNavigatedBackEvent());
        } else if (source == nextButton) {
            gameNavigation.moveForward();
        } else if (source == previousButton) {
            gameNavigation.moveBack();
            eventBus.fireEvent(new UserNavigatedBackEvent());
        } else if (source == lastButton) {
            gameNavigation.moveToEndOfVariation();
        }
        firePositionChanged(true);
    }

    private void firePositionChanged(final boolean triggeredByUser) {
        GWT.log(activityId + ": firing position changed");
        eventBus.fireEvent(new PositionChangedEvent(gameNavigation.getPosition(), triggeredByUser));
    }

    public NavigatorConfiguration getNavigatorConfiguration() {
        return navigatorConfiguration;
    }

    public GameNavigation<ShogiPosition> getGameNavigation() {
        return gameNavigation;
    }

}
