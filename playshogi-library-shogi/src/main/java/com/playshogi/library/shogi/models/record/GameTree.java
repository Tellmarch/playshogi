package com.playshogi.library.shogi.models.record;

import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;

public class GameTree {

    private final Node rootNode;

    public GameTree() {
        this(new Node(null));
    }

    public GameTree(final ReadOnlyShogiPosition startingPosition) {
        this(new Node(new EditMove(startingPosition)));
    }

    private GameTree(final Node rootNode) {
        this.rootNode = rootNode;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public ReadOnlyShogiPosition getInitialPosition() {
        if (rootNode.getMove() instanceof EditMove) {
            EditMove editMove = (EditMove) rootNode.getMove();
            return editMove.getPosition();
        } else {
            return ShogiInitialPositionFactory.READ_ONLY_INITIAL_POSITION;
        }
    }
}
