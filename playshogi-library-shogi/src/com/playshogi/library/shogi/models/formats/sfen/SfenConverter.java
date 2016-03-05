package com.playshogi.library.shogi.models.formats.sfen;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.position.ShogiBoardState;
import com.playshogi.library.shogi.models.position.KomadaiState;
import com.playshogi.library.shogi.models.position.ShogiBoardStateImpl;
import com.playshogi.library.shogi.models.position.ShogiPosition;

public class SfenConverter {

	private static final PieceType[] PIECE_TYPE_VALUES = PieceType.values();

	public static String toSFEN(final ShogiPosition pos) {
		String res = "";
		int numspace = 0;
		// First, the pieces on board
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				Piece piece = pos.getShogiBoardState().getPieceAt(1 + (8 - j), 1 + i);
				if (piece == null) {
					numspace++;
				} else {
					if (numspace != 0) {
						res += numspace;
					}
					numspace = 0;
					res += pieceToString(piece);
				}
			}
			if (numspace != 0) {
				res += numspace;
			}
			numspace = 0;
			if (i != 8) {

				res += "/";
			}
		}
		res += " ";

		// Which side to move?
		if (pos.isSenteToPlay()) {
			res += "b";
		} else {
			res += "w";
		}

		// Captured pieces
		int[] capture1 = pos.getSenteKomadai().getPieces();
		String c = "";
		for (int i = capture1.length - 1; i >= 0; i--) {
			int n = capture1[i];
			if (n != 0) {
				if (n != 1) {
					c += n;
				}
				c += pieceToString(Piece.getPiece(PIECE_TYPE_VALUES[i], true));
			}
		}
		int[] capture2 = pos.getGoteKomadai().getPieces();
		for (int i = capture2.length - 1; i >= 0; i--) {
			int n = capture2[i];
			if (n != 0) {
				if (n != 1) {
					c += n;
				}
				c += pieceToString(Piece.getPiece(PIECE_TYPE_VALUES[i], false));
			}
		}
		if (c.equals("")) {
			c = "-";
		}

		// Should we add the move count?

		return res + " " + c;
	}

	public static ShogiPosition fromSFEN(final String sfen) {
		ShogiBoardState shogiBoardState = new ShogiBoardStateImpl(9, 9);
		String[] fields = sfen.split(" ");

		// Reading board pieces
		String[] rows = fields[0].split("/");
		for (int i = 0; i < 9; i++) {
			String r = rows[i];
			boolean prom = false;
			int k = 0;
			for (int j = 0; j < r.length(); j++) {
				char x = r.charAt(j);
				if (x == '+') {
					prom = true;
					j++;
					x = r.charAt(j);
				}

				Piece p = pieceFromChar(x);

				if (p == null) {
					int s = x - '0';
					if (1 <= s && s <= 9) {
						for (int w = 0; w < s; w++) {
							shogiBoardState.setPieceAt(1 + (8 - k++), 1 + i, null);
						}
					}
				} else {
					if (prom) {
						p = p.getPromotedPiece();
					}
					shogiBoardState.setPieceAt(1 + (8 - k++), 1 + i, p);
				}
				prom = false;
			}
		}

		KomadaiState senteKomadai = new KomadaiState();
		KomadaiState goteKomadai = new KomadaiState();

		boolean senteTurn = true;
		if (fields[1].equalsIgnoreCase("w")) {
			senteTurn = false;
		}

		// TODO : more validation?
		// Read captured pieces
		if (!fields[2].equals("-")) {
			String r = fields[2];
			char x;
			Piece p;
			int s;
			for (int j = 0; j < r.length(); j++) {
				x = r.charAt(j);
				p = pieceFromChar(x);
				// If not a piece, should be a number
				if (p == null) {
					s = x - '0';
					if (1 <= s && s <= 9) {
						j++;
						x = r.charAt(j);
						p = pieceFromChar(x);

						// If not a piece, should be a number
						if (p == null) {
							s = 10 * s + (x - '0');
							if (1 <= s && s <= 99) {
								j++;
								x = r.charAt(j);
								p = pieceFromChar(x);
							} else {
								System.out.println("Error parsing SFEN " + sfen);
								return new ShogiPosition();
							}
						}
					} else {
						System.out.println("Error parsing SFEN " + sfen);
						return new ShogiPosition();
					}
				} else {
					s = 1;
				}
				if (p.isSentePiece()) {
					senteKomadai.setPiecesOfType(p.getPieceType(), s);
				} else {
					goteKomadai.setPiecesOfType(p.getPieceType(), s);
				}

			}
		}

		return new ShogiPosition(senteTurn, shogiBoardState, senteKomadai, goteKomadai);
	}

	public static Piece pieceFromChar(final char x) {
		switch (x) {
		case 'P':
			return Piece.SENTE_PAWN;
		case 'L':
			return Piece.SENTE_LANCE;
		case 'N':
			return Piece.SENTE_KNIGHT;
		case 'S':
			return Piece.SENTE_SILVER;
		case 'G':
			return Piece.SENTE_GOLD;
		case 'B':
			return Piece.SENTE_BISHOP;
		case 'R':
			return Piece.SENTE_ROOK;
		case 'K':
			return Piece.SENTE_KING;
		case 'p':
			return Piece.GOTE_PAWN;
		case 'l':
			return Piece.GOTE_LANCE;
		case 'n':
			return Piece.GOTE_KNIGHT;
		case 's':
			return Piece.GOTE_SILVER;
		case 'g':
			return Piece.GOTE_GOLD;
		case 'b':
			return Piece.GOTE_BISHOP;
		case 'r':
			return Piece.GOTE_ROOK;
		case 'k':
			return Piece.GOTE_KING;
		}
		return null;
	}

	public static String pieceToString(final Piece x) {
		switch (x) {
		case GOTE_BISHOP:
			return "b";
		case GOTE_GOLD:
			return "g";
		case GOTE_KING:
			return "k";
		case GOTE_KNIGHT:
			return "n";
		case GOTE_LANCE:
			return "l";
		case GOTE_PAWN:
			return "p";
		case GOTE_PROMOTED_BISHOP:
			return "+b";
		case GOTE_PROMOTED_KNIGHT:
			return "+n";
		case GOTE_PROMOTED_LANCE:
			return "+l";
		case GOTE_PROMOTED_PAWN:
			return "+p";
		case GOTE_PROMOTED_ROOK:
			return "+r";
		case GOTE_PROMOTED_SILVER:
			return "+s";
		case GOTE_ROOK:
			return "r";
		case GOTE_SILVER:
			return "s";
		case SENTE_BISHOP:
			return "B";
		case SENTE_GOLD:
			return "G";
		case SENTE_KING:
			return "K";
		case SENTE_KNIGHT:
			return "N";
		case SENTE_LANCE:
			return "L";
		case SENTE_PAWN:
			return "P";
		case SENTE_PROMOTED_BISHOP:
			return "+B";
		case SENTE_PROMOTED_KNIGHT:
			return "+N";
		case SENTE_PROMOTED_LANCE:
			return "+L";
		case SENTE_PROMOTED_PAWN:
			return "+P";
		case SENTE_PROMOTED_ROOK:
			return "+R";
		case SENTE_PROMOTED_SILVER:
			return "+S";
		case SENTE_ROOK:
			return "R";
		case SENTE_SILVER:
			return "S";
		default:
			return "";

		}
	}

	public static void main(final String[] args) {
		String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
		String s = toSFEN(fromSFEN(sfen));
		System.out.println(sfen);
		System.out.println(s);
		System.out.println(s.equals(sfen));
	}

}
