/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.playshogi.library.shogi.models.formats.usf;

import com.playshogi.library.models.Move;
import com.playshogi.library.models.record.GameInformation;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.models.record.GameResult;
import com.playshogi.library.models.record.GameTree;
import com.playshogi.library.models.record.Node;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;

public class UsfFormat {

	public static GameRecord read(final String usfGame) {
		String[] lines = usfGame.split("\n");
		int currentLine = 0;
		String l = lines[currentLine++];
		// First, check that the file is indeed USF.
		if (l.indexOf("USF:") == -1) {
			throw (new IllegalArgumentException("Not a recognized USF File. Maybe wrong encoding?"));
		}

		// Go to the start of the next game
		while (!l.startsWith("^")) {
			l = lines[currentLine++];
		}

		// This line contains the "preview" line

		// Reads the result
		// TODO : do something with it
		char resultc = l.charAt(1);
		switch (resultc) {
		case 's':
		case 'b':
		case 'g':
		case 'w':
		case 'd':
		case '*':
			break;
		default:
			throw (new IllegalArgumentException("Error parsing the USF File (not a valid result)"));
		}

		// We will know start building the game tree
		GameTree gameTree = new GameTree();

		ShogiPosition startingPosition;
		// If the next character is ":", the game is starting from start
		// position.
		if (l.charAt(2) == ':') {
			startingPosition = new ShogiInitialPositionFactory().createInitialPosition();
		} else {
			// We read the starting position, in a SFEN that goes up to ":"
			String sfen = l.substring(2, l.indexOf(':'));
			startingPosition = SfenConverter.fromSFEN(sfen);
		}

		GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<ShogiPosition>(new ShogiRulesEngine(),
				gameTree, startingPosition);

		// What follows is the move sequence
		String moves = l.substring(l.indexOf(':') + 1);

		playMoveSequence(gameNavigation, moves);

		// Now, we read tag lines. We read every lines until the end of the
		// file,
		// or the start of another game, identified by "^",.
		while (currentLine < lines.length) {
			l = lines[currentLine++];

			// If the line is empty, skip it
			if (l.isEmpty()) {
				continue;
			}

			if (l.charAt(0) == '^') {
				// New line
				break;
			}

			// A line starting with . changes the current node
			if (l.charAt(0) == '.') {
				String r = l.substring(1);
				// Next can be 1)Nothing 2)Move number 3)New move
				// 4)MoveNumber:new move
				if (r.isEmpty()) {
					// Empty : we just go to the next move
					gameNavigation.moveForwardInLastVariation();
				} else {
					String[] rs = r.split(":");
					if (rs.length == 1) {
						// No :, case 2 or 3.
						// We try to read a move number
						try {
							int mn = Integer.parseInt(r);
							// We found a move number, we go there
							gameNavigation.goToNodeUSF(mn);
						} catch (NumberFormatException e) {
							// If it wasn't a number, it should be a move
							// sequence
							playMoveSequence(gameNavigation, r);
						}
					} else if (rs.length == 2) {
						// it should be a move number + a move sequence
						int mn = Integer.parseInt(rs[0]);
						// We found a move number, we go there
						gameNavigation.goToNodeUSF(mn);
						// next should be a move sequence
						playMoveSequence(gameNavigation, rs[1]);
					} else {
						// More that one ":" in one line? shouldn't happen
						throw (new IllegalArgumentException("Error parsing the USF File (in the line " + l + " )"));
					}

				}
				continue;
			}

			// A line starting with # is a comment
			if (l.charAt(0) == '#') {
				// we just add the comment line to the current node's
				// comment
				gameNavigation.getCurrentNode()
						.setComment(gameNavigation.getCurrentNode().getComment() + "\n" + l.substring(1));
				continue;
			}

			// Next, we search for header tags or node tags
			if (l.startsWith("SFEN:")) {
				// Changes the position
				String sfen = l.substring(5);
				// gameTree.addEdit(sfen);
				continue;
			}

			// A line starting with ~ is an object
			if (l.charAt(0) == '~') {
				// USFObject object = new USFObject(l.substring(1));
				// gameTree.getCurrent().addObject(object);
				continue;
			}

			// A line starting with "X" is a custom tag
			if (l.charAt(0) == 'X') {
				// String tag = l.substring(1);
				// gameTree.getCurrent().addTag(l);
				continue;
			}
		}

		String date = "1000-1-1";
		String tournament = "UNKNOWN";
		String opening = "UNKNOWN";
		String place = "UNKNOWN";
		String time = "UNKNOWN";
		String handicap = "UNKNOWN";
		String gote = "UNKNOWN";
		String sente = "UNKNOWN";

		int result = 0;
		// TODO
		// switch (gameTree.getCurrent().getMove().getSpecialType()) {
		// case Move.SPECIAL_BREK:
		// result = Game.RESULT_BREAK;
		// break;
		// case Move.SPECIAL_JISO:
		// result = Game.RESULT_JISHOGI;
		// break;
		// case Move.SPECIAL_REPT:
		// result = Game.RESULT_SENNICHITE;
		// break;
		// case Move.SPECIAL_RSGN:
		// if (bsente) {
		// result = Game.RESULT_SENTE;
		// } else {
		// result = Game.RESULT_GOTE;
		// }
		// }

		GameInformation gameInformation = new GameInformation();
		GameResult gameResult = new GameResult();
		return new GameRecord(gameInformation, gameTree, gameResult);
	}

	/**
	 * plays a move sequence represented by a String, with each move occupying 4
	 * characters.
	 */
	private static void playMoveSequence(final GameNavigation<ShogiPosition> gameNavigation, final String moves) {
		// each move takes exactly 4 characters
		int numberOfMoves = moves.length() / 4;
		for (int i = 0; i < numberOfMoves; i++) {
			Move curMove = UsfMoveConverter.fromUsfString(moves.substring(4 * i, 4 * i + 4),
					gameNavigation.getPosition());
			gameNavigation.addMove(curMove, true);
		}
	}

	public static String write(final GameRecord gameRecord) {
		GameTree gameTree = gameRecord.getGameTree();
		return write(gameTree);
	}

	public static String write(final GameTree gameTree) {
		return "USF:1.0\n" + toUSFString(gameTree);
	}

	private static String toUSFString(final GameTree gameTree) {
		String res = "^*:";
		Node n = gameTree.getRootNode();
		while (n.hasChildren()) {
			n = n.getChildren().get(0);
			res += UsfMoveConverter.toUsfString((ShogiMove) n.getMove());
		}
		return res;
	}

	/**
	 * Gives the USF string representing the whole tree.
	 *
	 * @return
	 */
	// public String toUSFString(final GameTree gameTree) {
	// String endline = System.getProperty("line.separator");
	// String res = "^*:";
	// List<Integer> var = mainLine();
	// // First, preview string (main line)
	// res += varToUSFString(var) + endline;
	//
	// // Then, for every variation
	// while (true) {
	// List<Integer> var2 = nextVariation(var);
	// if (var2 == null) {
	// // no more variation? end of loop
	// break;
	// }
	// // We write the USF string of this variation
	// res += varToUSFString(var2, firstDiff(var, var2)) + endline;
	// var = var2;
	// }
	// return res;
	// }

	public static void main(final String[] args) {
		String s = "USF:1.0\n^*:7g7f3c3d";
		GameRecord gameRecord = read(s);
		System.out.println(write(gameRecord));
	}

}
