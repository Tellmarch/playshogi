package com.playshogi.library.shogi.rules;

import com.playshogi.library.models.Move;
import com.playshogi.library.models.Square;
import com.playshogi.library.models.games.GameRulesEngine;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiVariant;
import com.playshogi.library.shogi.rules.movements.*;

import java.util.*;

import static com.playshogi.library.shogi.models.Piece.GOTE_KING;

public class ShogiRulesEngine implements GameRulesEngine<ShogiPosition> {

    private static final EnumMap<Piece, PieceMovement> PIECE_MOVEMENTS = new EnumMap<>(Piece.class);

    static {
        PIECE_MOVEMENTS.put(Piece.SENTE_PAWN, new PawnMovement());
        PIECE_MOVEMENTS.put(Piece.SENTE_LANCE, new LanceMovement());
        PIECE_MOVEMENTS.put(Piece.SENTE_KNIGHT, new KnightMovement());
        PIECE_MOVEMENTS.put(Piece.SENTE_SILVER, new SilverMovement());
        PIECE_MOVEMENTS.put(Piece.SENTE_GOLD, new GoldMovement());
        PIECE_MOVEMENTS.put(Piece.SENTE_KING, new KingMovement());
        PIECE_MOVEMENTS.put(Piece.SENTE_ROOK, new RookMovement());
        PIECE_MOVEMENTS.put(Piece.SENTE_BISHOP, new BishopMovement());

        PIECE_MOVEMENTS.put(Piece.SENTE_PROMOTED_PAWN, new GoldMovement());
        PIECE_MOVEMENTS.put(Piece.SENTE_PROMOTED_LANCE, new GoldMovement());
        PIECE_MOVEMENTS.put(Piece.SENTE_PROMOTED_KNIGHT, new GoldMovement());
        PIECE_MOVEMENTS.put(Piece.SENTE_PROMOTED_SILVER, new GoldMovement());
        PIECE_MOVEMENTS.put(Piece.SENTE_PROMOTED_BISHOP, new PromotedBishopMovement());
        PIECE_MOVEMENTS.put(Piece.SENTE_PROMOTED_ROOK, new PromotedRookMovement());
    }

    private final ShogiVariant shogiVariant;

    public ShogiRulesEngine() {
        this(ShogiVariant.NORMAL_SHOGI);
    }

    public ShogiRulesEngine(final ShogiVariant shogiVariant) {
        this.shogiVariant = shogiVariant;
    }

    @Override
    public void playMoveInPosition(final ShogiPosition position, final Move move) {
        if (move instanceof CaptureMove) {
            playCaptureMove(position, (CaptureMove) move);
        } else if (move instanceof DropMove) {
            playDropMove(position, (DropMove) move);
        } else if (move instanceof NormalMove) {
            playNormalMove(position, (NormalMove) move);
        }
        position.setSenteToPlay(!position.isSenteToPlay());
    }

    private void playCaptureMove(final ShogiPosition position, final CaptureMove move) {
        if (move.isSenteMoving()) {
            position.getSenteKomadai().addPiece(move.getCapturedPiece().getPieceType());
        } else {
            position.getGoteKomadai().addPiece(move.getCapturedPiece().getPieceType());
        }
        if (move.isPromote()) {
            position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece().getPromotedPiece());
        } else {
            position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece());
        }
        position.getShogiBoardState().setPieceAt(move.getFromSquare(), null);
    }

    private void playDropMove(final ShogiPosition position, final DropMove move) {
        position.getShogiBoardState().setPieceAt(move.getToSquare(),
                Piece.getPiece(move.getPieceType(), move.isSenteMoving()));
        if (move.isSenteMoving()) {
            position.getSenteKomadai().removePiece(move.getPieceType());
        } else {
            position.getGoteKomadai().removePiece(move.getPieceType());
        }
    }

    private void playNormalMove(final ShogiPosition position, final NormalMove move) {
        if (move.isPromote()) {
            position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece().getPromotedPiece());
        } else {
            position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece());
        }
        position.getShogiBoardState().setPieceAt(move.getFromSquare(), null);
    }

    @Override
    public void undoMoveInPosition(final ShogiPosition position, final Move move) {
        Objects.requireNonNull(move);
        Objects.requireNonNull(position);
        if (move instanceof CaptureMove) {
            undoCaptureMove(position, (CaptureMove) move);
        } else if (move instanceof DropMove) {
            undoDropMove(position, (DropMove) move);
        } else if (move instanceof NormalMove) {
            undoNormalMove(position, (NormalMove) move);
        }
        position.setSenteToPlay(!position.isSenteToPlay());
    }

    private void undoNormalMove(final ShogiPosition position, final NormalMove move) {
        if (move.isPromote()) {
            position.getShogiBoardState().setPieceAt(move.getFromSquare(), move.getPiece().getUnpromotedPiece());
        } else {
            position.getShogiBoardState().setPieceAt(move.getFromSquare(), move.getPiece());
        }
        position.getShogiBoardState().setPieceAt(move.getToSquare(), null);
    }

    private void undoDropMove(final ShogiPosition position, final DropMove move) {
        if (move.isSenteMoving()) {
            position.getSenteKomadai().addPiece(move.getPieceType());
        } else {
            position.getGoteKomadai().addPiece(move.getPieceType());
        }
        position.getShogiBoardState().setPieceAt(move.getToSquare(), null);
    }

    private void undoCaptureMove(final ShogiPosition position, final CaptureMove move) {
        if (move.isSenteMoving()) {
            position.getSenteKomadai().removePiece(move.getCapturedPiece().getPieceType());
        } else {
            position.getGoteKomadai().removePiece(move.getCapturedPiece().getPieceType());
        }
        if (move.isPromote()) {
            position.getShogiBoardState().setPieceAt(move.getFromSquare(), move.getPiece().getUnpromotedPiece());
        } else {
            position.getShogiBoardState().setPieceAt(move.getFromSquare(), move.getPiece());
        }
        position.getShogiBoardState().setPieceAt(move.getToSquare(), move.getCapturedPiece());
    }

    // what are target squares of a piece from 'from' square
    public List<Square> getPossibleTargetSquares(final ShogiPosition position, final Square from) {
        Piece piece = position.getPieceAt(from);
        if (piece == null) {
            return Collections.emptyList();
        }
        PieceMovement pieceMovement = PIECE_MOVEMENTS.get(piece.getSentePiece());
        if (piece.isSentePiece()) {
            return pieceMovement.getPossibleMoves(position.getShogiBoardState(), from);
        } else {
            return Square.opposite(
                    pieceMovement.getPossibleMoves(position.getShogiBoardState().opposite(), from.opposite()));
        }

    }

    public boolean canMoveWithPromotion(final ShogiPosition position, final Move move) {
        if (move instanceof NormalMove) {
            NormalMove normalMove = (NormalMove) move;

            if (normalMove.getPiece().isPromoted() || !normalMove.getPiece().canPromote()) {
                return false;
            }

            if (normalMove.isSenteMoving()) {
                return (normalMove.getFromSquare().getRow() <= shogiVariant.getSentePromotionHeight()
                        || normalMove.getToSquare().getRow() <= shogiVariant.getSentePromotionHeight());
            } else {
                return (normalMove.getFromSquare().getRow() >= shogiVariant.getGotePromotionHeight()
                        || normalMove.getToSquare().getRow() >= shogiVariant.getGotePromotionHeight());
            }

        }

        return false;
    }

    @Override
    public boolean isMoveLegalInPosition(final ShogiPosition position, final Move move) {
        if (move instanceof CaptureMove) {
            return isCaptureMoveLegalInPosition(position, (CaptureMove) move);
        } else if (move instanceof DropMove) {
            return isDropMoveLegalInPosition(position, (DropMove) move);
        } else if (move instanceof NormalMove) {
            return isNormalMoveLegalInPosition(position, (NormalMove) move);
        } else {
            return false;
        }
    }

    // todo validate that the piece is us, that we have piece to drop, that we
    // don't capture our piece

    private boolean isNormalMoveLegalInPosition(final ShogiPosition position, final NormalMove move) {
        PieceMovement pieceMovement = PIECE_MOVEMENTS.get(move.getPiece().getSentePiece());
        if (move.isSenteMoving()) {
            return pieceMovement.isMoveDxDyValid(position.getShogiBoardState(), move.getFromSquare(),
                    move.getToSquare());
        } else {
            return pieceMovement.isMoveDxDyValid(position.getShogiBoardState().opposite(),
                    move.getFromSquare().opposite(), move.getToSquare().opposite());
        }
    }

    private boolean isDropMoveLegalInPosition(final ShogiPosition position, final DropMove move) {
        PieceMovement pieceMovement = PIECE_MOVEMENTS.get(Piece.getPiece(move.getPieceType(), true));
        if (move.isSenteMoving()) {
            return pieceMovement.isDropValid(position.getShogiBoardState(), move.getToSquare());
        } else {
            return pieceMovement.isDropValid(position.getShogiBoardState().opposite(), move.getToSquare().opposite());
        }
    }

    private boolean isCaptureMoveLegalInPosition(final ShogiPosition position, final CaptureMove move) {
        Piece capturedPiece = position.getPieceAt(move.getToSquare());
        if (capturedPiece == null || capturedPiece.isSentePiece() == move.isSenteMoving()) {
            return false;
        } else {
            return isNormalMoveLegalInPosition(position, move);
        }
    }

    private List<ShogiMove> getAllPossibleMoves(final ShogiPosition position, final boolean isSente) {

        List<ShogiMove> result = new ArrayList<>();

        Square square = Square.of(1, 1);

        //magical while list iterating through position
        ShogiMove move = null; //includes all types of moves

        //find a possible move for all the squares
        //save the piece and movements to list

        getPossibleTargetSquares(position, square); //list squares
        position.getPieceAt(square);
        //list of squares turned into possible move list one by one
        //by making subclasses for each type of move

        CaptureMove captureMove = (CaptureMove) move;
        NormalMove normalMove = (NormalMove) move;
        DropMove dropMove = (DropMove) move;

        for (Square possibleTargetSquare : getPossibleTargetSquares(position, square)) {
            NormalMove normalMove1 = new NormalMove(position.getPieceAt(square), square, possibleTargetSquare, false);
        } //list of unpromoted normal moves
        return result;
    }

    public List<ShogiMove> getAllPossibleDropMoves(final ShogiPosition position, final boolean isSente) {

        List<ShogiMove> result = new ArrayList<>();

        if (isSente) {
            for (PieceType pieceType : PieceType.values()) { //iterating enum; rook, bishop, generals, light pieces, pawns
                if (position.getSenteKomadai().getPiecesOfType(pieceType) != 0) { //if have piece in hand
                    for (Square everySquare : position.getAllSquares()) { //check every squared of the board
                        if (position.getPieceAt(everySquare) == null) { //if its empty
                            if (isDropMoveLegalInPosition(position, new DropMove(true, pieceType, everySquare))) {
                                //can we drop it legally (like pawns/lance/knight)
                                result.add(new DropMove(true, pieceType, everySquare)); //add it to possible moves
                            }
                        }
                    }
                }
            }
        }

        if (!isSente) {
            for (PieceType pieceType : PieceType.values()) { //iterating enum
                if (position.getGoteKomadai().getPiecesOfType(pieceType) != 0) { //if have piece in hand
                    for (Square everySquare : position.getAllSquares()) { //check every squared of the board
                        if (position.getPieceAt(everySquare) == null) { //if its empty
                            if (isDropMoveLegalInPosition(position, new DropMove(false, pieceType, everySquare))) {
                                //can we drop it legally (like pawns/lance/knight)
                                result.add(new DropMove(false, pieceType, everySquare)); //add it to possible moves
                            }
                        }
                    }
                }
            }
        }

        return result; //include moves that would put you in check ***(1)
    }

    private boolean isPositionCheck(final ShogiPosition position) {
        //loop over list of shogimoves
        //is piece sente
        //is there king is taken by the piece

        for (ShogiMove move : getAllPossibleMoves(position, true)) {
            if (move instanceof CaptureMove) { //check only captures
                CaptureMove captureMove = (CaptureMove) move;
                if (captureMove.getCapturedPiece() == GOTE_KING) { //check if captured piece is a king
                    return true; //check found
                }

            }
        }
        return false;  //no checks for all possible moves
    }

    private boolean isPositionCheckmate(final ShogiPosition position) {
        //check all moves for gote
        //check if its check
        //if there is always check, checkmate
        return false;
    }

    public static void main(String[] args) {
        ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
        String sfenNoCheckmate = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        String sfenCheckmate = "4k4/4G4/4P4/9/9/9/9/9/9 w 2r2b3g4s4n4l17p";
        System.out.println(shogiRulesEngine.isPositionCheckmate(SfenConverter.fromSFEN(sfenNoCheckmate)));
        System.out.println(shogiRulesEngine.isPositionCheckmate(SfenConverter.fromSFEN(sfenCheckmate)));
    }

}
