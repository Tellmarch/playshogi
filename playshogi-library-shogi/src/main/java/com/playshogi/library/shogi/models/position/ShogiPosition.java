package com.playshogi.library.shogi.models.position;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.shogivariant.ShogiVariant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShogiPosition implements ReadOnlyShogiPosition {

    // How many moves were played in the position (starts at 0)
    private int moveCount;
    private Player playerToMove;
    private final MutableShogiBoardState shogiBoardState;
    private final MutableKomadaiState senteKomadai;
    private final MutableKomadaiState goteKomadai;

    public ShogiPosition() {
        this(ShogiVariant.NORMAL_SHOGI);
    }

    public ShogiPosition(final ShogiVariant shogiVariant) {
        this(0, Player.BLACK, new ShogiBoardStateImpl(shogiVariant.getBoardWidth(), shogiVariant.getBoardHeight()),
                new MutableKomadaiState(), new MutableKomadaiState());
    }

    public ShogiPosition(int moveCount, final Player playerToMove, final MutableShogiBoardState shogiBoardState,
                         final MutableKomadaiState senteKomadai, final MutableKomadaiState goteKomadai) {
        this.moveCount = moveCount;
        this.playerToMove = playerToMove;
        this.shogiBoardState = shogiBoardState;
        this.senteKomadai = senteKomadai;
        this.goteKomadai = goteKomadai;
    }

    @Override
    public int getMoveCount() {
        return moveCount;
    }

    public void decrementMoveCount() {
        this.moveCount = this.moveCount - 1;
        this.playerToMove = playerToMove.opposite();
    }

    public void incrementMoveCount() {
        this.moveCount = this.moveCount + 1;
        this.playerToMove = playerToMove.opposite();
    }

    public void setPlayerToMove(final Player playerToMove) {
        this.playerToMove = playerToMove;
    }

    @Override
    public Player getPlayerToMove() {
        return playerToMove;
    }

    @Override
    public ShogiBoardState getShogiBoardState() {
        return shogiBoardState;
    }

    public MutableShogiBoardState getMutableShogiBoardState() {
        return shogiBoardState;
    }

    @Override
    public int getColumns() {
        return shogiBoardState.getWidth();
    }

    @Override
    public int getRows() {
        return shogiBoardState.getHeight();
    }

    @Override
    public KomadaiState getSenteKomadai() {
        return senteKomadai;
    }

    public MutableKomadaiState getMutableSenteKomadai() {
        return senteKomadai;
    }

    @Override
    public KomadaiState getGoteKomadai() {
        return goteKomadai;
    }

    public MutableKomadaiState getMutableGoteKomadai() {
        return goteKomadai;
    }

    @Override
    public Optional<Piece> getPieceAt(final Square square) {
        return shogiBoardState.getPieceAt(square);
    }

    @Override
    public boolean isEmptySquare(final Square square) {
        return !shogiBoardState.getPieceAt(square).isPresent();
    }

    @Override
    public boolean hasBlackPieceAt(final Square square) {
        return shogiBoardState.getPieceAt(square).isPresent() && shogiBoardState.getPieceAt(square).get().isBlackPiece();
    }

    @Override
    public boolean hasWhitePieceAt(final Square square) {
        return shogiBoardState.getPieceAt(square).isPresent() && shogiBoardState.getPieceAt(square).get().isWhitePiece();
    }

    @Override
    public boolean hasSenteKingOnBoard() {
        for (int i = 1; i <= getRows(); i++) {
            for (int j = 1; j <= getColumns(); j++) {
                if (shogiBoardState.getPieceAt(i, j).orElse(null) == Piece.SENTE_KING) return true;
            }
        }
        return false;
    }

    /**
     * @return list of squares of the board
     */
    @Override
    public List<Square> getAllSquares() {
        List<Square> squares = new ArrayList<>();
        for (int row = 1; row <= shogiBoardState.getLastRow(); row++) {
            for (int column = 1; column <= shogiBoardState.getLastColumn(); column++) {
                squares.add(Square.of(column, row));
            }
        }
        return squares;
    }

    /**
     * This method only works for regular 9x9 shogi without handicap
     */
    public void fillGoteKomadaiWithMissingPieces() {
        goteKomadai.setPiecesOfType(PieceType.PAWN, 18 - senteKomadai.getPiecesOfType(PieceType.PAWN));
        goteKomadai.setPiecesOfType(PieceType.LANCE, 4 - senteKomadai.getPiecesOfType(PieceType.LANCE));
        goteKomadai.setPiecesOfType(PieceType.KNIGHT, 4 - senteKomadai.getPiecesOfType(PieceType.KNIGHT));
        goteKomadai.setPiecesOfType(PieceType.SILVER, 4 - senteKomadai.getPiecesOfType(PieceType.SILVER));
        goteKomadai.setPiecesOfType(PieceType.GOLD, 4 - senteKomadai.getPiecesOfType(PieceType.GOLD));
        goteKomadai.setPiecesOfType(PieceType.BISHOP, 2 - senteKomadai.getPiecesOfType(PieceType.BISHOP));
        goteKomadai.setPiecesOfType(PieceType.ROOK, 2 - senteKomadai.getPiecesOfType(PieceType.ROOK));
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= 9; j++) {
                Optional<Piece> piece = shogiBoardState.getPieceAt(i, j);
                if (piece.isPresent() && piece.get().getPieceType() != PieceType.KING) {
                    goteKomadai.removePiece(piece.get().getPieceType());
                }
            }
        }
    }

    @Override
    public String toString() {
        return playerToMove.name() + " to move:\n" + shogiBoardState.toString();
    }

    @Override
    public Position clonePosition() {
        // TODO optimize... maybe...
        return SfenConverter.fromSFEN(SfenConverter.toSFEN(this));
    }
}
