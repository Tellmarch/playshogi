package com.playshogi.library.models.record;

import com.playshogi.library.models.EditMove;
import com.playshogi.library.models.Position;

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
