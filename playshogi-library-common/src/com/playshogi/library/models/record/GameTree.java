package com.playshogi.library.models.record;

public class GameTree {

	private final Node rootNode;

	public GameTree() {
		this(new Node(null));
	}

	public GameTree(final Node rootNode) {
		this.rootNode = rootNode;
	}

	public Node getRootNode() {
		return rootNode;
	}

}
