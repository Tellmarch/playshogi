package com.playshogi.library.models.record;

import com.playshogi.library.models.Move;
import com.playshogi.library.models.Position;
import com.playshogi.library.models.games.GameRulesEngine;

public class GameNavigation<P extends Position<P>> {

	private final GameTree gameTree;
	private Node currentNode;
	private final P position;
	private final GameRulesEngine<P> gameRulesEngine;
	private final P startPosition;

	public GameNavigation(final GameRulesEngine<P> gameRulesEngine, final GameTree gameTree, final P startPosition) {
		this.gameRulesEngine = gameRulesEngine;
		this.gameTree = gameTree;
		this.startPosition = startPosition;
		this.position = startPosition.clonePosition();
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
		if (canMoveBack()) {
			gameRulesEngine.undoMoveInPosition(position, currentNode.getMove());
			currentNode = currentNode.getParent();
		}
	}

	public void moveForward() {
		if (canMoveForward()) {
			currentNode = currentNode.getChildren().get(0);
			gameRulesEngine.playMoveInPosition(position, currentNode.getMove());
		}
	}

	public void moveToStart() {
		// currentNode = gameTree.getRootNode();
		// this.position = startPosition.clonePosition();
		while (canMoveBack()) {
			moveBack();
		}
	}

	public void moveToEndOfVariation() {
		while (canMoveForward()) {
			moveForward();
		}
	}

	public void addMove(final Move move) {
		Node childNode = currentNode.getChildWithMove(move);
		if (childNode == null) {
			Node newNode = new Node(move);
			newNode.setParent(currentNode);
			currentNode.addChild(newNode);
			currentNode = newNode;
		} else {
			currentNode = childNode;
		}
		// gameRulesEngine.playMoveInPosition(position, move);
	}
}
