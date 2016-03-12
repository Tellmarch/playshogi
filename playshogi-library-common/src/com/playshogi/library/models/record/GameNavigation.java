package com.playshogi.library.models.record;

import com.playshogi.library.models.Position;
import com.playshogi.library.models.games.GameRulesEngine;

public class GameNavigation<P extends Position> {

	private final GameTree gameTree;
	private Node currentNode;
	private final P position;
	private final GameRulesEngine<P> gameRulesEngine;

	public GameNavigation(final GameRulesEngine<P> gameRulesEngine, final GameTree gameTree, final P position) {
		this.gameRulesEngine = gameRulesEngine;
		this.gameTree = gameTree;
		this.position = position;
		this.currentNode = gameTree.getRootNode();
	}

	public P getPosition() {
		return position;
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	public boolean canMoveBack() {
		return currentNode.getParent() != null;
	}

	public boolean canMoveForward() {
		return currentNode.hasChildren();
	}

	public void moveBack() {
		if (currentNode.getParent() != null) {
			currentNode = currentNode.getParent();
		}
	}
}
