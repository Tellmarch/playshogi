package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.HighlightMoveEvent;
import com.playshogi.website.gwt.client.events.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.PieceStyleSelectedEvent;
import com.playshogi.website.gwt.client.events.PositionChangedEvent;
import com.playshogi.website.gwt.client.widget.board.KomadaiPositioning.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShogiBoard extends Composite implements ClickHandler {

    interface MyEventBinder extends EventBinder<ShogiBoard> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private static final String STYLE_PIECE_SELECTED = "gwt-piece-selected";
    private static final String STYLE_PIECE_UNSELECTED = "gwt-piece-unselected";
    private static final String STYLE_SQUARE_SELECTED = "gwt-square-selected";
    private static final String STYLE_SQUARE_UNSELECTED = "gwt-square-unselected";

    private static final int TATAMI_LEFT_MARGIN = 10;
    private static final int TATAMI_TOP_MARGIN = 10;
    private static final int TATAMI_INSIDE_MARGIN = 5;
    private static final int BOARD_LEFT_MARGIN = 11;
    private static final int BOARD_TOP_MARGIN = 11;
    static final int SQUARE_WIDTH = 43;
    static final int SQUARE_HEIGHT = 48;

    private final BoardConfiguration boardConfiguration;
    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
    private ShogiPosition position;
    private PieceWrapper selectedPiece = null;
    private PieceGraphics.Style style = PieceGraphics.Style.RYOKO;

    private final List<PieceWrapper> boardPieceWrappers = new ArrayList<>();
    private final List<PieceWrapper> senteKomadaiPieceWrappers = new ArrayList<>();
    private final List<PieceWrapper> goteKomadaiPieceWrappers = new ArrayList<>();

    private Image[][] squareImages;
    private final AbsolutePanel absolutePanel;
    private final int boardLeft;
    private final int boardTop;
    private final Image goteKomadaiImage;
    private final Image senteKomadaiImage;
    private final int senteKomadaiX;
    private final int senteKomadaiY;
    private final int komadaiWidth;

    private Widget upperRightPanel;
    private final int upperRightPanelX;
    private final int upperRightPanelY;

    private Widget lowerLeftPanel;
    private final int lowerLeftPanelX;
    private final int lowerLeftPanelY;

    private final PromotionPopupController promotionPopupController;

    private EventBus eventBus;

    private final String activityId;

    public ShogiBoard(final String activityId) {
        this(activityId, new BoardConfiguration());
    }

    public ShogiBoard(final String activityId, final BoardConfiguration boardConfiguration) {
        this(activityId, boardConfiguration, ShogiInitialPositionFactory.createInitialPosition());
    }

    public ShogiBoard(final String activityId, final BoardConfiguration boardConfiguration, final ShogiPosition position) {
        GWT.log(activityId + ": Creating shogi board");

        this.activityId = activityId;
        this.boardConfiguration = boardConfiguration;
        this.position = position;
        this.promotionPopupController = new PromotionPopupController(this);

        BoardBundle boardResources = GWT.create(BoardBundle.class);
        absolutePanel = new AbsolutePanel();
        Image ban = new Image(boardResources.ban_kaya_a());
        Image grid = new Image(boardResources.masu_dot());
        Image coordinates = new Image(boardResources.scoordE());
        Image tatami = new Image(boardResources.bg_tatami());
        goteKomadaiImage = new Image(boardResources.ghand());
        senteKomadaiImage = new Image(boardResources.shand());

        komadaiWidth = goteKomadaiImage.getWidth();

        absolutePanel.setSize(tatami.getWidth() + "px", tatami.getHeight() + "px");
        absolutePanel.add(tatami, 0, 0);

        if (boardConfiguration.isShowGoteKomadai()) {
            absolutePanel.add(goteKomadaiImage, TATAMI_LEFT_MARGIN, TATAMI_TOP_MARGIN);
        }

        boardLeft = TATAMI_LEFT_MARGIN + komadaiWidth + TATAMI_INSIDE_MARGIN;
        boardTop = TATAMI_TOP_MARGIN;

        absolutePanel.add(ban, boardLeft, boardTop);
        absolutePanel.add(grid, boardLeft, boardTop);
        absolutePanel.add(coordinates, boardLeft, boardTop);

        senteKomadaiX = boardLeft + ban.getWidth() + TATAMI_INSIDE_MARGIN;
        senteKomadaiY = TATAMI_TOP_MARGIN + ban.getHeight() - senteKomadaiImage.getHeight();

        if (boardConfiguration.isShowSenteKomadai()) {
            absolutePanel.add(senteKomadaiImage, senteKomadaiX, senteKomadaiY);
        }

        upperRightPanelX = senteKomadaiX;
        upperRightPanelY = TATAMI_TOP_MARGIN;

        lowerLeftPanelX = TATAMI_LEFT_MARGIN;
        lowerLeftPanelY = TATAMI_TOP_MARGIN + goteKomadaiImage.getHeight() + TATAMI_INSIDE_MARGIN;

        initSquareImages(boardResources, position.getRows(), position.getColumns());

        goteKomadaiImage.addClickHandler(this);
        senteKomadaiImage.addClickHandler(this);

        DecoratorPanel absolutePanelWrapper = new DecoratorPanel();
        absolutePanelWrapper.setWidget(absolutePanel);

        initWidget(absolutePanelWrapper);
    }

    public void activate(final EventBus eventBus) {
        GWT.log(activityId + ": Activating Shogi Board");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, this.eventBus);
    }

    private void initSquareImages(BoardBundle boardResources, int rows, int columns) {
        squareImages = new Image[rows][columns];

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < columns; ++col) {
                final Image image = new Image(boardResources.empty());

                squareImages[row][col] = image;
                image.setStyleName(STYLE_PIECE_UNSELECTED);

                absolutePanel.add(image, getX(col), getY(row));

                setupSquareClickHandler(image, row, col);
            }
        }
    }

    public void displayPosition() {
        GWT.log(activityId + ": Displaying position");
        GWT.log(position.toString());

        unselect();

        displayBoardPieces();

        if (boardConfiguration.isShowSenteKomadai()) {
            displaySenteKomadai();
        }

        if (boardConfiguration.isShowGoteKomadai()) {
            displayGoteKomadai();
        }
    }

    private void displayBoardPieces() {

        for (PieceWrapper wrapper : boardPieceWrappers) {
            absolutePanel.remove(wrapper.getImage());
        }

        boardPieceWrappers.clear();

        for (int row = 0, rows = position.getRows(); row < rows; ++row) {
            for (int col = 0, columns = position.getColumns(); col < columns; ++col) {
                Piece piece = getPiece(row, col);
                if (piece != null) {
                    final Image image = new Image(PieceGraphics.getPieceImage(piece, style));

                    PieceWrapper pieceWrapper = new PieceWrapper(piece, image, row, col);
                    boardPieceWrappers.add(pieceWrapper);

                    setupPieceEventHandlers(pieceWrapper);
                    image.setStyleName(STYLE_PIECE_UNSELECTED);

                    absolutePanel.add(image, getX(col), getY(row));
                }
            }
        }
    }

    private void displaySenteKomadai() {

        for (PieceWrapper wrapper : senteKomadaiPieceWrappers) {
            absolutePanel.remove(wrapper.getImage());
        }

        senteKomadaiPieceWrappers.clear();

        PieceType[] pieceTypes = PieceType.values();
        int[] sentePieces = position.getSenteKomadai().getPieces();
        for (int i = 0; i < sentePieces.length; i++) {
            Point[] piecesPositions = KomadaiPositioning.getPiecesPositions(i, sentePieces[i], true, komadaiWidth);
            for (Point point : piecesPositions) {
                Piece piece = Piece.getPiece(pieceTypes[i], true);
                Image image = createKomadaiPieceImage(piece, true);

                absolutePanel.add(image, senteKomadaiX + point.x, senteKomadaiY + point.y);
            }
        }
    }

    private void displayGoteKomadai() {

        for (PieceWrapper wrapper : goteKomadaiPieceWrappers) {
            absolutePanel.remove(wrapper.getImage());
        }

        goteKomadaiPieceWrappers.clear();

        PieceType[] pieceTypes = PieceType.values();
        int[] gotePieces = position.getGoteKomadai().getPieces();
        for (int i = 0; i < gotePieces.length; i++) {
            Point[] piecesPositions = KomadaiPositioning.getPiecesPositions(i, gotePieces[i], false, komadaiWidth);
            for (Point point : piecesPositions) {
                Piece piece = Piece.getPiece(pieceTypes[i], false);
                Image image = createKomadaiPieceImage(piece, false);

                absolutePanel.add(image, TATAMI_LEFT_MARGIN + point.x, TATAMI_TOP_MARGIN + point.y);
            }
        }
    }

    private Image createKomadaiPieceImage(Piece piece, boolean sente) {
        final Image image = new Image(PieceGraphics.getPieceImage(piece, style));
        PieceWrapper pieceWrapper = new PieceWrapper(piece, image, sente ? -1 : -2, sente ? -1 : -2);
        pieceWrapper.setInKomadai(true);
        boardPieceWrappers.add(pieceWrapper);

        setupPieceEventHandlers(pieceWrapper);
        image.setStyleName(STYLE_PIECE_UNSELECTED);
        return image;
    }

    private Piece getPiece(final int row, final int col) {
        return position.getShogiBoardState().getPieceAt(getSquare(row, col)).orElse(null);
    }

    private Square getSquare(final int row, final int col) {
        return Square.of(((8 - col) + 1), row + 1);
    }

    private int getY(final int row) {
        return boardTop + BOARD_TOP_MARGIN + row * SQUARE_HEIGHT;
    }

    private int getX(final int col) {
        return boardLeft + BOARD_LEFT_MARGIN + col * SQUARE_WIDTH;
    }


    private void setupSquareClickHandler(final Image image, final int row, final int col) {
        image.addMouseDownHandler(DomEvent::preventDefault);

        image.addClickHandler(event -> {
            if (selectedPiece != null) {
                Piece piece = selectedPiece.getPiece();

                if (selectedPiece.isInKomadai()) {
                    DropMove move = new DropMove(piece.isSentePiece(), piece.getPieceType(), getSquare(row, col));
                    if (boardConfiguration.allowIllegalMoves() || shogiRulesEngine.isMoveLegalInPosition(position, move)) {
                        playMove(move);
                    }
                } else if (boardConfiguration.allowIllegalMoves() || shogiRulesEngine.getPossibleTargetSquares(position, selectedPiece.getSquare()).contains(getSquare(row, col))) {
                    NormalMove move = new NormalMove(piece, getSquare(selectedPiece.getRow(),
                            selectedPiece.getColumn()), getSquare(row, col));

                    playMoveOrShowPromotionPopup(move, image);
                }

                unselect();
            }
        });
    }

    private void setupPieceEventHandlers(final PieceWrapper pieceWrapper) {
        pieceWrapper.getImage().addMouseDownHandler(DomEvent::preventDefault);

        pieceWrapper.getImage().addClickHandler(event -> {
            if (canPlayMove()) {
                if (selectedPiece == pieceWrapper) {
                    unselect();
                } else if (selectedPiece != null) {
                    if (!selectedPiece.isInKomadai()
                            && selectedPiece.getPiece().isSentePiece() != pieceWrapper.getPiece().isSentePiece()
                            && (boardConfiguration.allowIllegalMoves() || shogiRulesEngine.getPossibleTargetSquares(position, selectedPiece.getSquare()).contains(pieceWrapper.getSquare()))) {
                        NormalMove move = new CaptureMove(selectedPiece.getPiece(),
                                selectedPiece.getSquare(), pieceWrapper.getSquare(), pieceWrapper.getPiece());
                        Image image = pieceWrapper.getImage();

                        playMoveOrShowPromotionPopup(move, image);
                        return;
                    } else {
                        unselect();
                    }
                }
                if (position.isSenteToPlay() == pieceWrapper.getPiece().isSentePiece()) {
                    selectedPiece = pieceWrapper;
                    pieceWrapper.getImage().setStyleName(STYLE_PIECE_SELECTED);

                    if (!pieceWrapper.isInKomadai()) {
                        List<Square> possibleTargets = shogiRulesEngine.getPossibleTargetSquares(position,
                                getSquare(pieceWrapper.getRow(), pieceWrapper.getColumn()));
                        for (Square square : possibleTargets) {
                            squareImages[square.getRow() - 1][8 - (square.getColumn() - 1)].setStyleName("gwt" +
                                    "-square-selected");
                        }
                    }
                }
            }
        });

        if (boardConfiguration.isShowPossibleMovesOnPieceMouseOver()) {

            pieceWrapper.getImage().addMouseOverHandler(event -> {
                // GWT.log("mouse over");
                if (selectedPiece == null) {
                    if (!pieceWrapper.isInKomadai()) {
                        if (position.isSenteToPlay() == pieceWrapper.getPiece().isSentePiece()) {
                            List<Square> possibleTargets =
                                    shogiRulesEngine.getPossibleTargetSquares(position,
                                            getSquare(pieceWrapper.getRow(), pieceWrapper.getColumn()));
                            for (Square square : possibleTargets) {
                                selectSquare(square);
                            }
                            selectPiece(pieceWrapper);
                        }
                    }
                }

            });

            pieceWrapper.getImage().addMouseOutHandler(event -> {
                if (selectedPiece == null) {
                    unselectSquares();
                }
            });
        }
    }

    private void playMoveOrShowPromotionPopup(NormalMove move, Image image) {
        Optional<NormalMove> promotionMove = shogiRulesEngine.getPromotionMove(position, move);
        if (promotionMove.isPresent() && boardConfiguration.isAllowPromotion()) {
            if (shogiRulesEngine.canMoveWithoutPromotion(position, move)) {
                promotionPopupController.showPromotionPopup(image, move, promotionMove.get());
            } else {
                playNormalMoveIfAllowed(promotionMove.get());
            }
        } else {
            playNormalMoveIfAllowed(move);
        }
    }

    void playNormalMoveIfAllowed(NormalMove move) {
        if (boardConfiguration.allowIllegalMoves() || shogiRulesEngine.isMoveLegalInPosition(position, move)) {
            playMove(move);
        }
    }

    private void selectPiece(final PieceWrapper pieceWrapper) {
        squareImages[pieceWrapper.getRow()][pieceWrapper.getColumn()].setStyleName(STYLE_SQUARE_SELECTED);
    }

    public void selectSquare(final Square square) {
        squareImages[square.getRow() - 1][8 - (square.getColumn() - 1)].setStyleName(STYLE_SQUARE_SELECTED);
    }

    private void unselect() {
        unselectSquares();
        if (selectedPiece != null) {
            selectedPiece.getImage().setStyleName(STYLE_PIECE_UNSELECTED);
            selectedPiece = null;
        }
    }

    private void unselectSquares() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                squareImages[i][j].setStyleName(STYLE_SQUARE_UNSELECTED);
            }
        }
    }

    @Override
    public void onClick(final ClickEvent event) {

        Object source = event.getSource();
        if (source == senteKomadaiImage && selectedPiece != null && boardConfiguration.isPositionEditingMode()) {
            selectedPiece.setInKomadai(true);
            displaySenteKomadai();
            unselect();
        } else if (source == goteKomadaiImage && selectedPiece != null && boardConfiguration.isPositionEditingMode()) {
            selectedPiece.setInKomadai(true);
            displayGoteKomadai();
            unselect();
        }
    }

    public void setPosition(final ShogiPosition position) {
        this.position = position;
        displayPosition();
    }

    public ShogiPosition getPosition() {
        return position;
    }

    private void playMove(final ShogiMove move) {
        eventBus.fireEvent(new MovePlayedEvent(move));
    }

    public boolean canPlayMove() {
        return position.isSenteToPlay() ? boardConfiguration.isPlaySenteMoves() : boardConfiguration.isPlayGoteMoves();
    }

    public void setPlaySenteMoves(final boolean playSenteMoves) {
        boardConfiguration.setPlaySenteMoves(playSenteMoves);
    }

    public void setPlayGoteMoves(final boolean playGoteMoves) {
        boardConfiguration.setPlayGoteMoves(playGoteMoves);
    }

    public BoardConfiguration getBoardConfiguration() {
        return boardConfiguration;
    }

    public void setUpperRightPanel(final Widget panel) {
        if (upperRightPanel != null) {
            absolutePanel.remove(upperRightPanel);
        }

        if (panel != null) {
            this.upperRightPanel = panel;
            panel.setWidth(senteKomadaiImage.getWidth() + "px");
            panel.setHeight((senteKomadaiY - boardTop - BOARD_TOP_MARGIN) + "px");
            absolutePanel.add(panel, upperRightPanelX, upperRightPanelY);
        }
    }

    public void setLowerLeftPanel(final Widget panel) {
        if (lowerLeftPanel != null) {
            absolutePanel.remove(lowerLeftPanel);
        }

        if (panel != null) {
            this.lowerLeftPanel = panel;
            panel.setWidth(senteKomadaiImage.getWidth() + "px");
            panel.setHeight((senteKomadaiY - boardTop - BOARD_TOP_MARGIN) + "px");
            absolutePanel.add(panel, lowerLeftPanelX, lowerLeftPanelY);
        }
    }

    @EventHandler
    public void onPositionChanged(final PositionChangedEvent event) {
        setPosition(event.getPosition());
    }

    @EventHandler
    public void onHighlightMove(final HighlightMoveEvent event) {
        unselectSquares();
        ShogiMove move = event.getMove();
        if (move instanceof NormalMove) {
            NormalMove normalMove = (NormalMove) move;

            selectSquare(normalMove.getFromSquare());
            selectSquare(normalMove.getToSquare());

        } else if (move instanceof DropMove) {
            DropMove dropMove = (DropMove) move;

            selectSquare(dropMove.getToSquare());

        }
    }

    @EventHandler
    public void onPieceStyleSelected(final PieceStyleSelectedEvent event) {
        style = event.getStyle();
        displayPosition();
    }
}
