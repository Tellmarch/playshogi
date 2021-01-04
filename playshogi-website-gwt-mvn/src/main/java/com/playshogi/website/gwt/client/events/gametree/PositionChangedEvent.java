package com.playshogi.website.gwt.client.events.gametree;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.decorations.BoardDecorations;
import com.playshogi.library.shogi.models.position.ShogiPosition;

import java.util.Optional;

public class PositionChangedEvent extends GenericEvent {

    private final ShogiPosition position;
    private final BoardDecorations decorations;
    private final boolean triggeredByUser;

    public PositionChangedEvent(final ShogiPosition position) {
        this(position, true);
    }


    public PositionChangedEvent(final ShogiPosition position, final boolean triggeredByUser) {
        this(position, null, triggeredByUser);
    }

    public PositionChangedEvent(final ShogiPosition position, final BoardDecorations decorations,
                                final boolean triggeredByUser) {
        this.position = position;
        this.decorations = decorations;
        this.triggeredByUser = triggeredByUser;
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
}
