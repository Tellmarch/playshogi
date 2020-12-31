package com.playshogi.library.shogi.models.record;

import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.position.Position;

public class GameTree {

    private final Node rootNode;

    public GameTree() {
        this(new Node(null));
    }

    public GameTree(final Position startingPosition) {
        this(new Node(new EditMove(startingPosition)));
    }

    public GameTree(final Node rootNode) {
        this.rootNode = rootNode;
    }

    public Node getRootNode() {
        return rootNode;
    }

}
