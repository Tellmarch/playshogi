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
import com.playshogi.library.shogi.models.moves.*;
import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;
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

    private static final BoardBundle BOARD_RESOURCES = GWT.create(BoardBundle.class);

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
    private final List<PieceWrapper> senteKomadaiPieceWrappers = new ArrayList<>();
    private final List<PieceWrapper> goteKomadaiPieceWrappers = new ArrayList<>();

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

        layout = new BoardLayout(BOARD_RESOURCES, absolutePanel, goteKomadaiImage, senteKomadaiImage, coordinates);

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
            GWT.log("CLICK - " + row + " " + col);
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
                        shogiRulesEngine.playMoveInPosition(position, getRealMove(move), false);
                        displayPosition();
                    } else {
                        if (boardConfiguration.allowIllegalMoves() ||
                                shogiRulesEngine.isMoveLegalInPosition(position, getRealMove(move))) {
                            playMove(move);
                        }
                    }
                } else {
                    Square selectedSquare = selectedPieceWrapper.getSquare();
                    NormalMove move = new NormalMove(piece, selectedSquare, clickedSquare);
                    if (boardConfiguration.isPositionEditingMode()) {
                        shogiRulesEngine.playMoveInPosition(position, getRealMove(move), false);
                        displayPosition();
                    } else {
                        if (boardConfiguration.allowIllegalMoves() ||
                                shogiRulesEngine.getPossibleTargetSquares(getDisplayPosition(), selectedSquare).contains(clickedSquare)) {
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
        GWT.log(activityId + ": Displaying position");
        GWT.log(getDisplayPosition().toString());

        selectionController.unselect();
        decorationController.clear();

        displayBoardPieces();

        if (boardConfiguration.isShowSenteKomadai()) {
            if (boardConfiguration.isInverted()) {
                displayGoteKomadai();
            } else {
                displaySenteKomadai();
            }
        }

        if (boardConfiguration.isShowGoteKomadai()) {
            if (boardConfiguration.isInverted()) {
                displaySenteKomadai();
            } else {
                displayGoteKomadai();
            }
        }
    }

    private void displayBoardPieces() {

        for (PieceWrapper wrapper : boardPieceWrappers) {
            absolutePanel.remove(wrapper.getImage());
        }

        boardPieceWrappers.clear();

        for (int row = 0, rows = getDisplayPosition().getRows(); row < rows; ++row) {
            for (int col = 0, columns = getDisplayPosition().getColumns(); col < columns; ++col) {
                Piece piece = getPiece(row, col);
                if (piece != null) {
                    final Image image = new Image(PieceGraphics.getPieceImage(piece, getPieceStyle()));

                    PieceWrapper pieceWrapper = new PieceWrapper(piece, image, row, col);
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

    private void displaySenteKomadai() {

        for (PieceWrapper wrapper : senteKomadaiPieceWrappers) {
            absolutePanel.remove(wrapper.getImage());
        }

        senteKomadaiPieceWrappers.clear();

        PieceType[] pieceTypes = PieceType.values();
        int[] sentePieces = getDisplayPosition().getSenteKomadai().getPieces();
        for (int i = 0; i < sentePieces.length; i++) {
            Point[] piecesPositions = KomadaiPositioning.getPiecesPositions(i, sentePieces[i], true,
                    layout.getKomadaiWidth());
            for (Point point : piecesPositions) {
                Piece piece = Piece.getPiece(pieceTypes[i], Player.BLACK);
                Image image = createKomadaiPieceImage(piece, true);

                absolutePanel.add(image, layout.getSenteKomadaiX() + point.x, layout.getSenteKomadaiY() + point.y);
            }
        }
    }

    private void displayGoteKomadai() {

        for (PieceWrapper wrapper : goteKomadaiPieceWrappers) {
            absolutePanel.remove(wrapper.getImage());
        }

        goteKomadaiPieceWrappers.clear();

        PieceType[] pieceTypes = PieceType.values();
        int[] gotePieces = getDisplayPosition().getGoteKomadai().getPieces();
        for (int i = 0; i < gotePieces.length; i++) {
            Point[] piecesPositions = KomadaiPositioning.getPiecesPositions(i, gotePieces[i], false,
                    layout.getKomadaiWidth());
            for (Point point : piecesPositions) {
                Piece piece = Piece.getPiece(pieceTypes[i], Player.WHITE);
                Image image = createKomadaiPieceImage(piece, false);

                absolutePanel.add(image, layout.getGoteKomadaiX() + point.x, layout.getGoteKomadaiY() + point.y);
            }
        }
    }

    private Image createKomadaiPieceImage(Piece piece, boolean sente) {
        final Image image = new Image(PieceGraphics.getPieceImage(piece, getPieceStyle()));
        PieceWrapper pieceWrapper = new PieceWrapper(piece, image, sente ? BLACK_KOMADAI_ROW : WHITE_KOMADAI_ROW,
                sente ? BLACK_KOMADAI_ROW : WHITE_KOMADAI_ROW);
        pieceWrapper.setInKomadai(true);
        boardPieceWrappers.add(pieceWrapper);

        setupPieceEventHandlers(pieceWrapper);
        return image;
    }

    private Piece getPiece(final int row, final int col) {
        return getDisplayPosition().getShogiBoardState().getPieceAt(getSquare(row, col)).orElse(null);
    }

    private static Square getSquare(final int row, final int col) {
        return Square.of(((8 - col) + 1), row + 1);
    }

    private void setupPieceEventHandlers(final PieceWrapper pieceWrapper) {
        pieceWrapper.getImage().addMouseDownHandler(mouseDownEvent -> {
            mouseDownEvent.preventDefault();
            if (pieceWrapper.isInKomadai()) {
                mouseDownUpStartSquare = null;
                mouseDownUpStartPieceWrapper = pieceWrapper;
            } else {
                mouseDownUpStartSquare = getSquare(pieceWrapper.getRow(), pieceWrapper.getColumn());
                mouseDownUpStartPieceWrapper = null;
            }
        });

        pieceWrapper.getImage().addMouseUpHandler(event -> {
            if (pieceWrapper.isInKomadai()) {
                //TODO
            } else {
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
                        && (boardConfiguration.allowIllegalMoves() || shogiRulesEngine.getPossibleTargetSquares(getDisplayPosition(), selectedPiece.getSquare()).contains(pieceWrapper.getSquare()))) {
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
            if (getDisplayPosition().getPlayerToMove() == pieceWrapper.getPiece().getOwner() || boardConfiguration.isPositionEditingMode()) {
                selectionController.selectPiece(pieceWrapper);

                if (!pieceWrapper.isInKomadai() && !boardConfiguration.isPositionEditingMode()) {
                    selectionController.selectPossibleMoves(pieceWrapper, getDisplayPosition());
                }
            }
        });

        if (boardConfiguration.isShowPossibleMovesOnPieceMouseOver() && !boardConfiguration.isPositionEditingMode()) {
            selectionController.setupMouseOverHandler(pieceWrapper);
        }

        if (boardConfiguration.isPositionEditingMode()) {
            pieceWrapper.getImage().addMouseWheelHandler(mouseWheelEvent -> {
                if (!pieceWrapper.isInKomadai()) {
                    position.getMutableShogiBoardState().setPieceAt(getRealSquare(pieceWrapper.getSquare()),
                            pieceWrapper.getPiece().getNextPieceInEditCycle());
                    displayPosition();
                }
            });
        }
    }

    private void playMoveOrShowPromotionPopup(NormalMove move, Image image) {
        Optional<NormalMove> promotionMove = shogiRulesEngine.getPromotionMove(getDisplayPosition(), move);
        if (promotionMove.isPresent() && boardConfiguration.isAllowPromotion()) {
            if (shogiRulesEngine.canMoveWithoutPromotion(getDisplayPosition(), move)) {
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
                getRealMove(move))) {
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
                if (wrapper.getRow() == BLACK_KOMADAI_ROW) { // Already in sente Komadai
                    return;
                } else {
                    if (boardConfiguration.isInverted()) {
                        position.getMutableSenteKomadai().removePiece(wrapper.getPiece().getPieceType());
                    } else {
                        position.getMutableGoteKomadai().removePiece(wrapper.getPiece().getPieceType());
                    }
                }
            } else {
                position.getMutableShogiBoardState().setPieceAt(getRealSquare(wrapper.getSquare()), null);
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
                if (wrapper.getRow() == WHITE_KOMADAI_ROW) { // Already in gote Komadai
                    return;
                } else {
                    if (boardConfiguration.isInverted()) {
                        position.getMutableGoteKomadai().addPiece(wrapper.getPiece().getPieceType());
                    } else {
                        position.getMutableSenteKomadai().addPiece(wrapper.getPiece().getPieceType());
                    }
                }
            } else {
                position.getMutableShogiBoardState().setPieceAt(getRealSquare(wrapper.getSquare()), null);
            }
            if (boardConfiguration.isInverted()) {
                position.getMutableSenteKomadai().removePiece(wrapper.getPiece().getPieceType());
            } else {
                position.getMutableGoteKomadai().removePiece(wrapper.getPiece().getPieceType());
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
        eventBus.fireEvent(new MovePlayedEvent(getRealMove(move)));
    }

    private ShogiMove getRealMove(final ShogiMove move) {
        if (boardConfiguration.isInverted()) {
            return MoveUtils.opposite(move);
        } else {
            return move;
        }
    }

    private Square getRealSquare(final Square square) {
        if (boardConfiguration.isInverted()) {
            return square.opposite();
        } else {
            return square;
        }
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

    public ReadOnlyShogiPosition getDisplayPosition() {
        return boardConfiguration.isInverted() ? position.opposite() : position;
    }

    @EventHandler
    public void onPositionChanged(final PositionChangedEvent event) {
        setPosition(event.getPosition());
        if (event.getDecorations().isPresent()) {
            Scheduler.get().scheduleDeferred(() -> drawDecorations(event.getDecorations().get()));
        }
    }

    @EventHandler
    public void onHighlightMove(final HighlightMoveEvent event) {
        GWT.log("Handling HighlightMoveEvent: " + event.getMove());
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
        GWT.log("Handling FlipBoardEvent: " + event.isInverted());
        boardConfiguration.setInverted(event.isInverted());
        refreshCoordinates();
        Scheduler.get().scheduleDeferred(this::displayPosition);
    }
}
