package com.playshogi.website.gwt.client.events.gametree;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class EndOfVariationReachedEvent extends GenericEvent {

    private final ShogiPosition position;
    private final boolean isNewNode;
    private final boolean isProblemWrongAnswerNode;

    public EndOfVariationReachedEvent(final ShogiPosition position, final boolean isNewNode,
                                      final boolean isProblemWrongAnswerNode) {
        this.position = position;
        this.isNewNode = isNewNode;
        this.isProblemWrongAnswerNode = isProblemWrongAnswerNode;
    }

    public ShogiPosition getPosition() {
        return position;
    }

    public boolean isNewNode() {
        return isNewNode;
    }

    public boolean isProblemWrongAnswerNode() {
        return isProblemWrongAnswerNode;
    }
}
