package com.playshogi.library.shogi.rules;

import com.playshogi.library.models.Move;
import com.playshogi.library.models.Square;
import com.playshogi.library.models.games.GameRulesEngine;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.KomadaiState;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiVariant;
import com.playshogi.library.shogi.rules.movements.*;

import java.util.*;

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
        playMoveInPosition(position, move, true);
    }

    @Override
    public void playMoveInPosition(final ShogiPosition position, final Move move, final boolean incrementMoveCount) {
        if (move instanceof CaptureMove) {
            playCaptureMove(position, (CaptureMove) move);
        } else if (move instanceof DropMove) {
            playDropMove(position, (DropMove) move);
        } else if (move instanceof NormalMove) {
            playNormalMove(position, (NormalMove) move);
        }
        if (incrementMoveCount) {
            position.incrementMoveCount();
        }
    }

    private void playCaptureMove(final ShogiPosition position, final CaptureMove move) {
        if (move.getPlayer() == Player.BLACK) {
            position.getSenteKomadai().addPiece(move.getCapturedPiece().getPieceType());
        } else {
            position.getGoteKomadai().addPiece(move.getCapturedPiece().getPieceType());
        }
        if (move.isPromote()) {
            position.getMutableShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece().getPromotedPiece());
        } else {
            position.getMutableShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece());
        }
        position.getMutableShogiBoardState().setPieceAt(move.getFromSquare(), null);
    }

    private void playDropMove(final ShogiPosition position, final DropMove move) {
        position.getMutableShogiBoardState().setPieceAt(move.getToSquare(),
                Piece.getPiece(move.getPieceType(), move.getPlayer()));
        if (move.getPlayer() == Player.BLACK) {
            position.getSenteKomadai().removePiece(move.getPieceType());
        } else {
            position.getGoteKomadai().removePiece(move.getPieceType());
        }
    }

    private void playNormalMove(final ShogiPosition position, final NormalMove move) {
        if (move.isPromote()) {
            position.getMutableShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece().getPromotedPiece());
        } else {
            position.getMutableShogiBoardState().setPieceAt(move.getToSquare(), move.getPiece());
        }
        position.getMutableShogiBoardState().setPieceAt(move.getFromSquare(), null);
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
        position.decrementMoveCount();
    }

    private void undoNormalMove(final ShogiPosition position, final NormalMove move) {
        if (move.isPromote()) {
            position.getMutableShogiBoardState().setPieceAt(move.getFromSquare(), move.getPiece().getUnpromotedPiece());
        } else {
            position.getMutableShogiBoardState().setPieceAt(move.getFromSquare(), move.getPiece());
        }
        position.getMutableShogiBoardState().setPieceAt(move.getToSquare(), null);
    }

    private void undoDropMove(final ShogiPosition position, final DropMove move) {
        if (move.getPlayer() == Player.BLACK) {
            position.getSenteKomadai().addPiece(move.getPieceType());
        } else {
            position.getGoteKomadai().addPiece(move.getPieceType());
        }
        position.getMutableShogiBoardState().setPieceAt(move.getToSquare(), null);
    }

    private void undoCaptureMove(final ShogiPosition position, final CaptureMove move) {
        if (move.getPlayer() == Player.BLACK) {
            position.getSenteKomadai().removePiece(move.getCapturedPiece().getPieceType());
        } else {
            position.getGoteKomadai().removePiece(move.getCapturedPiece().getPieceType());
        }
        if (move.isPromote()) {
            position.getMutableShogiBoardState().setPieceAt(move.getFromSquare(), move.getPiece().getUnpromotedPiece());
        } else {
            position.getMutableShogiBoardState().setPieceAt(move.getFromSquare(), move.getPiece());
        }
        position.getMutableShogiBoardState().setPieceAt(move.getToSquare(), move.getCapturedPiece());
    }

    // what are the target squares of a piece from 'from' square
    public List<Square> getPossibleTargetSquares(final ShogiPosition position, final Square from) {
        Optional<Piece> piece = position.getPieceAt(from);
        if (piece.isPresent()) {
            return getPossibleTargetSquares(position, from, piece.get());
        }
        return Collections.emptyList();
    }

    private List<Square> getPossibleTargetSquares(final ShogiPosition position, final Square from, Piece piece) {
        PieceMovement pieceMovement = PIECE_MOVEMENTS.get(piece.getSentePiece());
        if (piece.getOwner() == Player.BLACK) {
            return pieceMovement.getPossibleMoves(position.getShogiBoardState(), from);
        } else {
            return Square.opposite(
                    pieceMovement.getPossibleMoves(position.getShogiBoardState().opposite(), from.opposite()));
        }
    }

    /**
     * Doesn't check if the move is legal
     */
    public Optional<NormalMove> getPromotionMove(final ShogiPosition position, final NormalMove move) {
        Piece promotedPiece = move.getPiece().getPromotedPiece();
        if (promotedPiece != null && canMoveWithPromotion(position, move)) {
            return Optional.of(move.withPromotionPiece(promotedPiece));
        }
        return Optional.empty();
    }

    /**
     * Doesn't check if the move is legal
     */
    public boolean canMoveWithPromotion(final ShogiPosition position, final NormalMove move) {
        if (move.getPlayer() == Player.BLACK) {
            return (move.getFromSquare().getRow() <= shogiVariant.getSentePromotionHeight()
                    || move.getToSquare().getRow() <= shogiVariant.getSentePromotionHeight());
        } else {
            return (move.getFromSquare().getRow() >= shogiVariant.getGotePromotionHeight()
                    || move.getToSquare().getRow() >= shogiVariant.getGotePromotionHeight());
        }
    }

    /**
     * Doesn't check if the move is legal
     */
    public boolean canMoveWithoutPromotion(final ShogiPosition position, final NormalMove move) {
        if (move.getPiece().isPromoted()) {
            return true;
        }

        PieceMovement pieceMovement = PIECE_MOVEMENTS.get(move.getPiece().getSentePiece());
        if (move.isBlackMoving()) {
            return pieceMovement.isUnpromoteValid(position.getShogiBoardState(), move.getToSquare());
        } else {
            return pieceMovement.isUnpromoteValid(position.getShogiBoardState().opposite(),
                    move.getToSquare().opposite());
        }
    }

    @Override
    public boolean isMoveLegalInPosition(final ShogiPosition position, final Move move) {
        if (!isMoveLegalInPositionWithoutCheckingKingAttack(position, move)) {
            return false;
        }

        playMoveInPosition(position, move);
        boolean kingInCheck = isPositionCheck(position, position.getPlayerToMove().opposite());
        undoMoveInPosition(position, move);
        return !kingInCheck;
    }

    private boolean isMoveLegalInPositionWithoutCheckingKingAttack(final ShogiPosition position, final Move move) {
        if (move instanceof CaptureMove) {
            return isCaptureMoveLegalInPosition(position, (CaptureMove) move);
        } else if (move instanceof DropMove) {
            return isDropMoveLegalInPosition(position, (DropMove) move);
        } else if (move instanceof NormalMove) {
            if (position.getPieceAt(((NormalMove) move).getToSquare()).isPresent()) return false;
            return isNormalMoveLegalInPosition(position, (NormalMove) move);
        } else {
            return false;
        }
    }

    // todo validate that the piece is us, that we have piece to drop, that we
    // don't capture our piece

    private boolean isNormalMoveLegalInPosition(final ShogiPosition position, final NormalMove move) {
        PieceMovement pieceMovement = PIECE_MOVEMENTS.get(move.getPiece().getSentePiece());
        if (move.isBlackMoving()) {
            return pieceMovement.isMoveDxDyValid(position.getShogiBoardState(), move.getFromSquare(),
                    move.getToSquare());
        } else {
            return pieceMovement.isMoveDxDyValid(position.getShogiBoardState().opposite(),
                    move.getFromSquare().opposite(), move.getToSquare().opposite());
        }
    }

    private boolean isDropMoveLegalInPosition(final ShogiPosition position, final DropMove move) {
        boolean result = isDropMoveValidInPosition(position, move);
        if (result && move.getPieceType() == PieceType.PAWN) {
            if (isPawnDropCheck(position, move)) {
                this.playMoveInPosition(position, move);
                result = !isPositionCheckmate(position);
                this.undoMoveInPosition(position, move);
            }
        }
        return result;
    }

    private boolean isPawnDropCheck(final ShogiPosition position, final DropMove move) {
        if (move.isBlackMoving()) {
            Optional<Piece> piece = position.getPieceAt(move.getToSquare().above().orElse(move.getToSquare()));
            return piece.isPresent() && piece.get() == Piece.GOTE_KING;
        } else {
            Optional<Piece> piece = position.getPieceAt(move.getToSquare().below().orElse(move.getToSquare()));
            return piece.isPresent() && piece.get() == Piece.SENTE_KING;
        }
    }

    private boolean isDropMoveValidInPosition(final ShogiPosition position, final DropMove move) {
        PieceMovement pieceMovement = PIECE_MOVEMENTS.get(Piece.getPiece(move.getPieceType(), Player.BLACK));
        if (move.isBlackMoving()) {
            return pieceMovement.isDropValid(position.getShogiBoardState(), move.getToSquare());
        } else {
            return pieceMovement.isDropValid(position.getShogiBoardState().opposite(), move.getToSquare().opposite());
        }
    }

    private boolean isCaptureMoveLegalInPosition(final ShogiPosition position, final CaptureMove move) {
        Optional<Piece> capturedPiece = position.getPieceAt(move.getToSquare());
        if (capturedPiece.isPresent() && capturedPiece.get().isBlackPiece() != move.isBlackMoving()) {
            return isNormalMoveLegalInPosition(position, move);
        } else {
            return false;
        }
    }

    public List<ShogiMove> getAllPossibleMoves(final ShogiPosition position) {
        return getAllPossibleMoves(position, position.getPlayerToMove());
    }

    public List<ShogiMove> getAllPossibleMoves(final ShogiPosition position, final Player player) {

        List<ShogiMove> result = new ArrayList<>();

        result.addAll(getAllPossibleDropMoves(position, player));
        result.addAll(getAllPossibleNormalAndCaptureMoves(position, player));

        return result;
    }

    public List<ShogiMove> getAllPossibleNormalAndCaptureMoves(final ShogiPosition position, final Player player) {
        List<ShogiMove> result = new ArrayList<>();


        for (Square square : position.getAllSquares()) { //for every square on the board
            Optional<Piece> piece = position.getPieceAt(square);
            if (piece.isPresent() && piece.get().getOwner() == player) {
                //check if there is sente's piece
                List<Square> targetSquares = getPossibleTargetSquares(position, square);
                // find its possible squares to move
                for (Square targetSquare : targetSquares) { //add each of those squares as a possible move
                    Optional<Piece> targetPiece = position.getPieceAt(targetSquare);
                    NormalMove move;
                    if (targetPiece.isPresent()) { //check if it is a capturing move
                        if (targetPiece.get().getOwner() == player)
                            continue;
                        move = new CaptureMove(piece.get(), square, targetSquare, targetPiece.get());
                    } else {
                        move = new NormalMove(piece.get(), square, targetSquare);
                    }
                    if (canMoveWithoutPromotion(position, move)) {
                        result.add(move);
                    }
                    Optional<NormalMove> promotionMove = getPromotionMove(position, move);
                    promotionMove.ifPresent(result::add);
                }
            }
        }

        return result;
    }

    public List<ShogiMove> getAllPossibleDropMoves(final ShogiPosition position, final Player player) {
        List<ShogiMove> result = new ArrayList<>();

        KomadaiState komadai = player == Player.BLACK ? position.getSenteKomadai() : position.getGoteKomadai();

        for (PieceType pieceType : PieceType.values()) {
            if (komadai.getPiecesOfType(pieceType) != 0) {
                for (Square square : position.getAllSquares()) {
                    if (position.isEmptySquare(square)) {
                        DropMove move = new DropMove(player, pieceType, square);
                        if (isDropMoveLegalInPosition(position, move)) {
                            result.add(move);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Is the current position a check (the player whose turn it is to play has his King attacked)
     */
    public boolean isPositionCheck(final ShogiPosition position) {
        return isPositionCheck(position, position.getPlayerToMove());
    }

    /**
     * In the current position, is player's king attacked?
     */
    public boolean isPositionCheck(final ShogiPosition position, final Player player) {
        // Find the king square and determine if the king could move like a knight,
        // would it attack an opposing knight, etc.
        for (Square square : position.getAllSquares()) {
            Optional<Piece> occupant = position.getPieceAt(square);
            if (occupant.isPresent() && occupant.get().getOwner() == player && occupant.get().getPieceType() == PieceType.KING)
                for (Piece piece : Piece.values())
                    if (piece.isBlackPiece() == occupant.get().isBlackPiece()) {
                        for (Square to : getPossibleTargetSquares(position, square, piece))
                            if (position.getPieceAt(to).orElse(null) == piece.opposite())
                                return true;
                    }
        }
        return false;
    }

    /**
     * Check if the position is a checkmate (The player to move can't escape check)
     */
    public boolean isPositionCheckmate(final ShogiPosition position) {
        Player escapingPlayer = position.getPlayerToMove();
        if (!isPositionCheck(position, escapingPlayer)) {
            return false;
        }
        for (ShogiMove everyMove : getAllPossibleMoves(position)) {
            this.playMoveInPosition(position, everyMove);
            if (!isPositionCheck(position, escapingPlayer)) {
                this.undoMoveInPosition(position, everyMove);
                System.out.println("escape:" + everyMove);
                return false;
            }
            this.undoMoveInPosition(position, everyMove);
        }
        return true;
    }

}
