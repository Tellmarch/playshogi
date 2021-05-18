package com.playshogi.website.gwt.client.events.gametree;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.decorations.BoardDecorations;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;

import java.util.Optional;

public class PositionChangedEvent extends GenericEvent {

    private final ShogiPosition position;
    private final BoardDecorations decorations;
    private final boolean triggeredByUser;
    private final ShogiMove previousMove;

    public PositionChangedEvent(final ShogiPosition position, final boolean triggeredByUser) {
        this(position, null, null, triggeredByUser);
    }

    public PositionChangedEvent(final ShogiPosition position, final BoardDecorations decorations,
                                final Move previousMove,
                                final boolean triggeredByUser) {
        this.position = position;
        this.decorations = decorations;
        this.triggeredByUser = triggeredByUser;
        if (previousMove instanceof ShogiMove) {
            this.previousMove = (ShogiMove) previousMove;
        } else {
            this.previousMove = null;
        }
    }


    public ShogiPosition getPosition() {
        return position;
    }

    public boolean isTriggeredByUser() {
        return triggeredByUser;
    }

    public Optional<BoardDecorations> getDecorations() {
        return Optional.ofNullable(decorations);
    }

    public ShogiMove getPreviousMove() {
        return previousMove;
    }
}
