package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.decorations.Arrow;
import com.playshogi.library.shogi.models.decorations.BoardDecorations;
import com.playshogi.library.shogi.models.decorations.Color;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.UserPreferences;
import com.playshogi.website.gwt.client.events.gametree.HighlightMoveEvent;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.ArrowDrawnEvent;
import com.playshogi.website.gwt.client.events.kifu.FlipBoardEvent;
import com.playshogi.website.gwt.client.events.user.ArrowModeSelectedEvent;
import com.playshogi.website.gwt.client.events.user.NotationStyleSelectedEvent;
import com.playshogi.website.gwt.client.events.user.PieceStyleSelectedEvent;
import com.playshogi.website.gwt.client.widget.board.KomadaiPositioning.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.playshogi.website.gwt.client.widget.board.PieceWrapper.BLACK_KOMADAI_ROW;
import static com.playshogi.website.gwt.client.widget.board.PieceWrapper.WHITE_KOMADAI_ROW;

public class ShogiBoard extends Composite implements ClickHandler {

    public static final BoardBundle BOARD_RESOURCES = GWT.create(BoardBundle.class);

    private final UserPreferences userPreferences;
    private BoardSettingsPanel boardSettingsPanel;

    interface MyEventBinder extends EventBinder<ShogiBoard> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();

    private final String activityId;
    private final BoardConfiguration boardConfiguration;

    private final AbsolutePanel absolutePanel;
    private final BoardLayout layout;

    private final Image goteKomadaiImage;
    private final Image senteKomadaiImage;
    private final Image coordinates;

    private final List<PieceWrapper> boardPieceWrappers = new ArrayList<>();
    private final List<PieceWrapper> lowerKomadaiPieceWrappers = new ArrayList<>();
    private final List<PieceWrapper> upperKomadaiPieceWrappers = new ArrayList<>();

    private final PromotionPopupController promotionPopupController;
    private BoardSelectionController selectionController;
    private final BoardDecorationController decorationController;

    private Widget upperRightPanel;
    private Widget lowerLeftPanel;

    private EventBus eventBus;
    private ShogiPosition position;

    private Square mouseDownUpStartSquare = null;
    private PieceWrapper mouseDownUpStartPieceWrapper = null;

    public ShogiBoard(final String activityId, final UserPreferences userPreferences) {
        this(activityId, userPreferences, new BoardConfiguration());
    }

    public ShogiBoard(final String activityId, final UserPreferences userPreferences,
                      final BoardConfiguration boardConfiguration) {
        this(activityId, userPreferences, boardConfiguration, ShogiInitialPositionFactory.createInitialPosition());
    }

    public ShogiBoard(final String activityId, final UserPreferences userPreferences,
                      final BoardConfiguration boardConfiguration,
                      final ShogiPosition position) {
        GWT.log(activityId + ": Creating shogi board");

        this.userPreferences = userPreferences;
        this.activityId = activityId;
        this.boardConfiguration = boardConfiguration;
        this.position = position;
        this.promotionPopupController = new PromotionPopupController(this);

        absolutePanel = new AbsolutePanel();

        goteKomadaiImage = new Image(BOARD_RESOURCES.ghand());
        senteKomadaiImage = new Image(BOARD_RESOURCES.shand());
        coordinates = new Image(getCoordinatesImage(userPreferences.getNotationStyle()));

        layout = new BoardLayout(BOARD_RESOURCES, absolutePanel, goteKomadaiImage, senteKomadaiImage, coordinates,
                boardConfiguration);

        decorationController = new BoardDecorationController(this, layout);

        if (boardConfiguration.isShowGoteKomadai()) {
            layout.addGoteKomadai(goteKomadaiImage);
        }

        if (boardConfiguration.isShowSenteKomadai()) {
            layout.addSenteKomadai(senteKomadaiImage);
        }

        initSquareImages(BOARD_RESOURCES, position.getRows(), position.getColumns());

        goteKomadaiImage.addClickHandler(this);
        senteKomadaiImage.addClickHandler(this);

        DecoratorPanel absolutePanelWrapper = new DecoratorPanel();
        absolutePanelWrapper.setWidget(absolutePanel);

        initWidget(absolutePanelWrapper);
    }

    public BoardSettingsPanel getBoardSettingsPanel() {
        if (boardSettingsPanel == null) {
            boardSettingsPanel = new BoardSettingsPanel(userPreferences);
            if (eventBus != null) {
                boardSettingsPanel.activate(eventBus);
            }
        }
        return boardSettingsPanel;
    }

    private void initSquareImages(BoardBundle boardResources, int rows, int columns) {
        Image[][] squareImages = new Image[rows][columns];
        selectionController = new BoardSelectionController(squareImages, this);

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < columns; ++col) {
                final Image image = new Image(boardResources.empty());

                squareImages[row][col] = image;
                absolutePanel.add(image, layout.getX(col), layout.getY(row));

                setupSquareClickHandler(image, row, col);
            }
        }
    }

    private void setupSquareClickHandler(final Image image, final int row, final int col) {
        image.addMouseDownHandler(mouseDownEvent -> {
            mouseDownEvent.preventDefault();
            mouseDownUpStartSquare = getSquare(row, col);
            mouseDownUpStartPieceWrapper = null;
        });

        image.addMouseUpHandler(event -> {
            Square to = getSquare(row, col);
            drawArrowToSquare(to);
            mouseDownUpStartSquare = null;
            mouseDownUpStartPieceWrapper = null;
        });

        image.addClickHandler(event -> {
            if (event.isControlKeyDown()) {
                drawCircleAtSquare(getSquare(row, col));
                return;
            }

            if (selectionController.hasPieceSelected()) {
                PieceWrapper selectedPieceWrapper = selectionController.getSelectedPieceWrapper();
                Piece piece = selectedPieceWrapper.getPiece();
                Square clickedSquare = getSquare(row, col);

                if (selectedPieceWrapper.isInKomadai()) {
                    DropMove move = new DropMove(piece.getOwner(), piece.getPieceType(), clickedSquare);
                    if (boardConfiguration.isPositionEditingMode()) {
                        shogiRulesEngine.playMoveInPosition(position, move, false);
                        displayPosition();
                    } else {
                        if (boardConfiguration.allowIllegalMoves() ||
                                shogiRulesEngine.isMoveLegalInPosition(position, move)) {
                            playMove(move);
                        }
                    }
                } else {
                    Square selectedSquare = selectedPieceWrapper.getSquare();
                    NormalMove move = new NormalMove(piece, selectedSquare, clickedSquare);
                    if (boardConfiguration.isPositionEditingMode()) {
                        shogiRulesEngine.playMoveInPosition(position, move, false);
                        displayPosition();
                    } else {
                        if (boardConfiguration.allowIllegalMoves() ||
                                shogiRulesEngine.getPossibleTargetSquares(position, selectedSquare).contains(clickedSquare)) {
                            playMoveOrShowPromotionPopup(move, image);
                        }
                    }
                }

                selectionController.unselect();
            }
        });
    }

    private void drawArrowToSquare(final Square to) {
        if (!boardConfiguration.isAllowDrawArrows()) {
            return;
        }
        Arrow arrow = null;
        if (mouseDownUpStartSquare != null && !to.equals(mouseDownUpStartSquare)) {
            arrow = new Arrow(mouseDownUpStartSquare, to, Color.RED);
        } else if (mouseDownUpStartPieceWrapper != null) {
            arrow = new Arrow(mouseDownUpStartPieceWrapper.getPiece(), to, Color.RED);
        }
        if (arrow != null) {
            decorationController.drawArrow(arrow);
            eventBus.fireEvent(new ArrowDrawnEvent(arrow));
        }
    }

    private void drawCircleAtSquare(final Square square) {
        if (!boardConfiguration.isAllowDrawArrows()) {
            return;
        }
        decorationController.drawCircle(square, Color.RED);
    }

    public void activate(final EventBus eventBus) {
        GWT.log(activityId + ": Activating Shogi Board");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, this.eventBus);
        if (boardSettingsPanel != null) {
            boardSettingsPanel.activate(eventBus);
        }
        refreshCoordinates();
    }

    public void displayPosition() {
//        GWT.log(activityId + ": Displaying position");
//        GWT.log(position.toString());

        selectionController.unselect();
        decorationController.clear();

        displayBoardPieces();

        if (boardConfiguration.isShowSenteKomadai()) {
            if (boardConfiguration.isInverted()) {
                displayUpperKomadai();
            } else {
                displayLowerKomadai();
            }
        }

        if (boardConfiguration.isShowGoteKomadai()) {
            if (boardConfiguration.isInverted()) {
                displayLowerKomadai();
            } else {
                displayUpperKomadai();
            }
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
                    final Image image = new Image(PieceGraphics.getPieceImage(piece, getPieceStyle(),
                            boardConfiguration.isInverted()));

                    PieceWrapper pieceWrapper = new PieceWrapper(piece, image, row, col, getSquare(row, col), false);
                    boardPieceWrappers.add(pieceWrapper);

                    setupPieceEventHandlers(pieceWrapper);

                    absolutePanel.add(image, layout.getX(col), layout.getY(row));
                }
            }
        }
    }

    private PieceGraphics.Style getPieceStyle() {
        return userPreferences.getPieceStyle();
    }

    private void displayLowerKomadai() {

        for (PieceWrapper wrapper : lowerKomadaiPieceWrappers) {
            absolutePanel.remove(wrapper.getImage());
        }

        lowerKomadaiPieceWrappers.clear();

        PieceType[] pieceTypes = PieceType.values();
        int[] pieces = boardConfiguration.isInverted() ?
                position.getGoteKomadai().getPieces()
                : position.getSenteKomadai().getPieces();
        for (int i = 0; i < pieces.length; i++) {
            Point[] piecesPositions = KomadaiPositioning.getPiecesPositions(i, pieces[i], true,
                    layout.getKomadaiWidth());
            for (Point point : piecesPositions) {
                Piece piece = Piece.getPiece(pieceTypes[i], boardConfiguration.isInverted() ? Player.WHITE :
                        Player.BLACK);
                Image image = createKomadaiPieceImage(piece, !boardConfiguration.isInverted());

                absolutePanel.add(image, layout.getSenteKomadaiX() + point.x, layout.getSenteKomadaiY() + point.y);
            }
        }
    }

    private void displayUpperKomadai() {

        for (PieceWrapper wrapper : upperKomadaiPieceWrappers) {
            absolutePanel.remove(wrapper.getImage());
        }

        upperKomadaiPieceWrappers.clear();

        PieceType[] pieceTypes = PieceType.values();
        int[] pieces = boardConfiguration.isInverted() ?
                position.getSenteKomadai().getPieces()
                : position.getGoteKomadai().getPieces();
        for (int i = 0; i < pieces.length; i++) {
            Point[] piecesPositions = KomadaiPositioning.getPiecesPositions(i, pieces[i], false,
                    layout.getKomadaiWidth());
            for (Point point : piecesPositions) {
                Piece piece = Piece.getPiece(pieceTypes[i], boardConfiguration.isInverted() ? Player.BLACK :
                        Player.WHITE);
                Image image = createKomadaiPieceImage(piece, boardConfiguration.isInverted());

                absolutePanel.add(image, layout.getGoteKomadaiX() + point.x, layout.getGoteKomadaiY() + point.y);
            }
        }
    }

    private Image createKomadaiPieceImage(Piece piece, boolean sente) {
        final Image image = new Image(PieceGraphics.getPieceImage(piece, getPieceStyle(),
                boardConfiguration.isInverted()));
        PieceWrapper pieceWrapper = new PieceWrapper(piece, image, sente ? BLACK_KOMADAI_ROW : WHITE_KOMADAI_ROW,
                sente ? BLACK_KOMADAI_ROW : WHITE_KOMADAI_ROW, null, true);
        boardPieceWrappers.add(pieceWrapper);

        setupPieceEventHandlers(pieceWrapper);
        return image;
    }

    private Piece getPiece(final int row, final int col) {
        return position.getShogiBoardState().getPieceAt(getSquare(row, col)).orElse(null);
    }

    /**
     * @param row : index (starting from 0) of the row, from the top
     * @param col : index (starting from 0) of the column, from the left
     */
    private Square getSquare(final int row, final int col) {
        if (boardConfiguration.isInverted()) {
            return Square.of(col + 1, 9 - row);
        } else {
            return Square.of(9 - col, row + 1);
        }
    }

    private void setupPieceEventHandlers(final PieceWrapper pieceWrapper) {
        pieceWrapper.getImage().addMouseDownHandler(mouseDownEvent -> {
            mouseDownEvent.preventDefault();
            if (pieceWrapper.isInKomadai()) {
                mouseDownUpStartSquare = null;
                mouseDownUpStartPieceWrapper = pieceWrapper;
            } else {
                mouseDownUpStartSquare = pieceWrapper.getSquare();
                mouseDownUpStartPieceWrapper = null;
            }
        });

        pieceWrapper.getImage().addMouseUpHandler(event -> {
            if (!pieceWrapper.isInKomadai()) {
                Square to = pieceWrapper.getSquare();
                drawArrowToSquare(to);
            }
            mouseDownUpStartSquare = null;
            mouseDownUpStartPieceWrapper = null;
        });

        pieceWrapper.getImage().addClickHandler(event -> {
            if (event.isControlKeyDown()) {
                drawCircleAtSquare(pieceWrapper.getSquare());
                return;
            }

            if (!canPlayMove() && !boardConfiguration.isPositionEditingMode()) {
                return;
            }

            PieceWrapper selectedPiece = selectionController.getSelectedPieceWrapper();

            if (selectedPiece == pieceWrapper) {
                selectionController.unselect();
                return;
            }

            if (selectedPiece != null) {
                // Capture an opponent piece?
                if (!selectedPiece.isInKomadai()
                        && selectedPiece.getPiece().getOwner() != pieceWrapper.getPiece().getOwner()
                        && (boardConfiguration.allowIllegalMoves() || shogiRulesEngine.getPossibleTargetSquares(position, selectedPiece.getSquare()).contains(pieceWrapper.getSquare()))) {
                    NormalMove move = new CaptureMove(selectedPiece.getPiece(),
                            selectedPiece.getSquare(), pieceWrapper.getSquare(), pieceWrapper.getPiece());
                    Image image = pieceWrapper.getImage();

                    playMoveOrShowPromotionPopup(move, image);
                    return;
                } else {
                    selectionController.unselect();
                }
            }

            // Select one of our pieces?
            if (position.getPlayerToMove() == pieceWrapper.getPiece().getOwner() || boardConfiguration.isPositionEditingMode()) {
                selectionController.selectPiece(pieceWrapper);

                if (!pieceWrapper.isInKomadai() && !boardConfiguration.isPositionEditingMode()) {
                    selectionController.selectPossibleMoves(pieceWrapper, position);
                }
            }
        });

        if (boardConfiguration.isShowPossibleMovesOnPieceMouseOver() && !boardConfiguration.isPositionEditingMode()) {
            selectionController.setupMouseOverHandler(pieceWrapper);
        }

        if (boardConfiguration.isPositionEditingMode()) {
            pieceWrapper.getImage().addMouseWheelHandler(mouseWheelEvent -> {
                if (!pieceWrapper.isInKomadai()) {
                    position.getMutableShogiBoardState().setPieceAt(pieceWrapper.getSquare(),
                            pieceWrapper.getPiece().getNextPieceInEditCycle());
                    displayPosition();
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
        if (boardConfiguration.allowIllegalMoves() || shogiRulesEngine.isMoveLegalInPosition(position,
                move)) {
            playMove(move);
        }
    }


    @Override
    public void onClick(final ClickEvent event) {
        Object source = event.getSource();
        if (source == senteKomadaiImage && selectionController.hasPieceSelected()
                && boardConfiguration.isPositionEditingMode()) {
            PieceWrapper wrapper = selectionController.getSelectedPieceWrapper();
            GWT.log("Moving piece to sente komadai: " + wrapper);
            if (wrapper.isInKomadai()) {
                if (wrapper.getRow() == (boardConfiguration.isInverted() ? WHITE_KOMADAI_ROW : BLACK_KOMADAI_ROW)) {
                    // Already in sente Komadai
                    return;
                } else {
                    if (boardConfiguration.isInverted()) {
                        position.getMutableSenteKomadai().removePiece(wrapper.getPiece().getPieceType());
                    } else {
                        position.getMutableGoteKomadai().removePiece(wrapper.getPiece().getPieceType());
                    }
                }
            } else {
                position.getMutableShogiBoardState().setPieceAt(wrapper.getSquare(), null);
            }
            if (boardConfiguration.isInverted()) {
                position.getMutableGoteKomadai().addPiece(wrapper.getPiece().getPieceType());
            } else {
                position.getMutableSenteKomadai().addPiece(wrapper.getPiece().getPieceType());
            }
            selectionController.unselect();
            displayPosition();
        } else if (source == goteKomadaiImage && selectionController.hasPieceSelected()
                && boardConfiguration.isPositionEditingMode()) {
            PieceWrapper wrapper = selectionController.getSelectedPieceWrapper();
            GWT.log("Moving piece to gote komadai: " + wrapper);
            if (wrapper.isInKomadai()) {
                if (wrapper.getRow() == (boardConfiguration.isInverted() ? BLACK_KOMADAI_ROW : WHITE_KOMADAI_ROW)) {
                    // Already in gote Komadai
                    return;
                } else {
                    if (boardConfiguration.isInverted()) {
                        position.getMutableGoteKomadai().removePiece(wrapper.getPiece().getPieceType());
                    } else {
                        position.getMutableSenteKomadai().removePiece(wrapper.getPiece().getPieceType());
                    }
                }
            } else {
                position.getMutableShogiBoardState().setPieceAt(wrapper.getSquare(), null);
            }
            if (boardConfiguration.isInverted()) {
                position.getMutableSenteKomadai().addPiece(wrapper.getPiece().getPieceType());
            } else {
                position.getMutableGoteKomadai().addPiece(wrapper.getPiece().getPieceType());
            }
            selectionController.unselect();
            displayPosition();
        }
    }

    public void setPosition(final ShogiPosition position) {
        this.position = position;

        if (boardConfiguration.isFillGoteKomadaiWithMissingPieces()) {
            position.fillGoteKomadaiWithMissingPieces();
        }

        displayPosition();
    }

    public ShogiPosition getPosition() {
        return position;
    }

    private void playMove(final ShogiMove move) {
        eventBus.fireEvent(new MovePlayedEvent(move));
    }

    public boolean canPlayMove() {
        return position.getPlayerToMove() == Player.BLACK ? boardConfiguration.isPlayBlackMoves() :
                boardConfiguration.isPlayWhiteMoves();
    }

    public void setPlaySenteMoves(final boolean playSenteMoves) {
        boardConfiguration.setPlayBlackMoves(playSenteMoves);
    }

    public void setPlayGoteMoves(final boolean playGoteMoves) {
        boardConfiguration.setPlayWhiteMoves(playGoteMoves);
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
            layout.addUpperRightPanel(panel);
        }
    }

    public void setLowerLeftPanel(final Widget panel) {
        if (lowerLeftPanel != null) {
            absolutePanel.remove(lowerLeftPanel);
        }

        if (panel != null) {
            this.lowerLeftPanel = panel;
            layout.addLowerLeftPanel(panel);
        }
    }

    public BoardSelectionController getSelectionController() {
        return selectionController;
    }

    public BoardDecorationController getDecorationController() {
        return decorationController;
    }

    private void drawDecorations(final BoardDecorations decorations) {
        GWT.log("Drawing decorations: " + decorations);
        for (Arrow arrow : decorations.getArrows()) {
            decorationController.drawArrow(arrow);
        }
    }

    private void refreshCoordinates() {
        coordinates.setResource(getCoordinatesImage(userPreferences.getNotationStyle()));
    }

    private ImageResource getCoordinatesImage(final UserPreferences.NotationStyle style) {
        if (boardConfiguration.isInverted()) {
            switch (style) {
                case TRADITIONAL:
                    return BOARD_RESOURCES.gcoordKanji();
                case KK_NOTATION:
                case WESTERN_NUMERICAL:
                case NUMERICAL_JAPANESE:
                    return BOARD_RESOURCES.gcoordNumbers();
                case WESTERN_ALPHABETICAL:
                    return BOARD_RESOURCES.gcoordLetters();
                default:
                    throw new IllegalStateException("Unexpected value: " + style);
            }
        } else {
            switch (style) {
                case TRADITIONAL:
                    return BOARD_RESOURCES.scoordKanji();
                case KK_NOTATION:
                case WESTERN_NUMERICAL:
                case NUMERICAL_JAPANESE:
                    return BOARD_RESOURCES.scoordNumbers();
                case WESTERN_ALPHABETICAL:
                    return BOARD_RESOURCES.scoordLetters();
                default:
                    throw new IllegalStateException("Unexpected value: " + style);
            }
        }
    }

    EventBus getEventBus() {
        return eventBus;
    }

    @EventHandler
    public void onPositionChanged(final PositionChangedEvent event) {
        GWT.log("ShogiBoard Handling PositionChangedEvent");
        selectionController.setPreviousMove(event.getPreviousMove());
        setPosition(event.getPosition());
        if (event.getDecorations().isPresent()) {
            Scheduler.get().scheduleDeferred(() -> drawDecorations(event.getDecorations().get()));
        }
    }

    @EventHandler
    public void onHighlightMove(final HighlightMoveEvent event) {
        //GWT.log("ShogiBoard Handling HighlightMoveEvent: " + event.getMove());
        selectionController.highlightMove(event.getMove());
        decorationController.highlightMove(event.getMove());
    }

    @EventHandler
    public void onPieceStyleSelected(final PieceStyleSelectedEvent event) {
        Scheduler.get().scheduleDeferred(this::displayPosition);
    }

    @EventHandler
    public void onNotationStyleSelected(final NotationStyleSelectedEvent event) {
        refreshCoordinates();
    }

    @EventHandler
    public void onArrowModeSelected(final ArrowModeSelectedEvent event) {
        boardConfiguration.setAllowDrawArrows(event.isEnabled());
    }

    @EventHandler
    public void onFlipBoard(final FlipBoardEvent event) {
        GWT.log("ShogiBoard Handling FlipBoardEvent: " + event.isInverted());
        boardConfiguration.setInverted(event.isInverted());
        refreshCoordinates();
        Scheduler.get().scheduleDeferred(this::displayPosition);
    }
}
