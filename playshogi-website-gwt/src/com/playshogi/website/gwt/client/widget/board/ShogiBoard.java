package com.playshogi.website.gwt.client.widget.board;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.PositionChangedEvent;
import com.playshogi.website.gwt.client.widget.board.Komadai.Point;

public class ShogiBoard extends Composite implements ClickHandler {

	interface MyEventBinder extends EventBinder<ShogiBoard> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private static final String STYLE_PIECE_SELECTED = "gwt-piece-selected";
	private static final String STYLE_PIECE_UNSELECTED = "gwt-piece-unselected";
	private static final int TATAMI_LEFT_MARGIN = 10;
	private static final int TATAMI_TOP_MARGIN = 10;
	private static final int TATAMI_INSIDE_MARGIN = 5;
	private static final int BOARD_LEFT_MARGIN = 11;
	private static final int BOARD_TOP_MARGIN = 11;
	public static final int SQUARE_WIDTH = 43;
	public static final int SQUARE_HEIGHT = 48;

	private final BoardConfiguration boardConfiguration;
	private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
	private ShogiPosition position;
	private PieceWrapper selectedPiece = null;
	private final List<PieceWrapper> pieceWrappers = new ArrayList<>();
	private Image[][] squareImages;
	private final Image ban;
	private final AbsolutePanel absolutePanel;
	private final Image grid;
	private final int boardLeft;
	private final int boardTop;
	private final Image goteKomadaiImage;
	private final Image senteKomadaiImage;
	private final Komadai senteKomadai;
	private final Komadai goteKomadai;
	private final int senteKomadaiX;
	private final int senteKomadaiY;

	private static final BoardBundle boardResources = GWT.create(BoardBundle.class);
	private Widget upperRightPanel;
	private EventBus eventBus;

	private final String activityId;

	public ShogiBoard(final String activityId) {
		this(activityId, new BoardConfiguration());
	}

	public ShogiBoard(final String activityId, final BoardConfiguration boardConfiguration) {
		GWT.log(activityId + ": Creating shogi board");

		this.activityId = activityId;
		this.boardConfiguration = boardConfiguration;
		absolutePanel = new AbsolutePanel();
		ban = new Image(boardResources.ban_kaya_a());
		grid = new Image(boardResources.masu_dot());
		Image tatami = new Image(boardResources.bg_tatami());
		goteKomadaiImage = new Image(boardResources.ghand());
		senteKomadaiImage = new Image(boardResources.shand());

		senteKomadai = new Komadai(true);
		goteKomadai = new Komadai(false);

		absolutePanel.setSize(tatami.getWidth() + "px", tatami.getHeight() + "px");
		absolutePanel.add(tatami, 0, 0);

		if (boardConfiguration.isShowGoteKomadai()) {
			absolutePanel.add(goteKomadaiImage, TATAMI_LEFT_MARGIN, TATAMI_TOP_MARGIN);
		}

		boardLeft = TATAMI_LEFT_MARGIN + goteKomadaiImage.getWidth() + TATAMI_INSIDE_MARGIN;
		boardTop = TATAMI_TOP_MARGIN;

		absolutePanel.add(ban, boardLeft, boardTop);
		absolutePanel.add(grid, boardLeft, boardTop);

		senteKomadaiX = boardLeft + ban.getWidth() + TATAMI_INSIDE_MARGIN;
		senteKomadaiY = TATAMI_TOP_MARGIN + ban.getHeight() - senteKomadaiImage.getHeight();

		if (boardConfiguration.isShowSenteKomadai()) {
			absolutePanel.add(senteKomadaiImage, senteKomadaiX, senteKomadaiY);
		}

		position = new ShogiInitialPositionFactory().createInitialPosition();

		initSquareImages();

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

	private void initSquareImages() {
		int rows = 9;
		int columns = 9;

		squareImages = new Image[rows][columns];

		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < columns; ++col) {
				final Image image = new Image(boardResources.empty());
				image.addClickHandler(this);

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

		int rows = position.getShogiBoardState().getHeight();
		int columns = position.getShogiBoardState().getWidth();

		for (PieceWrapper wrapper : pieceWrappers) {
			absolutePanel.remove(wrapper.getImage());
		}

		pieceWrappers.clear();

		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < columns; ++col) {
				Piece piece = getPiece(row, col);
				if (piece != null) {
					final Image image = new Image(PieceGraphics.getPieceImage(piece));

					PieceWrapper pieceWrapper = new PieceWrapper(piece, image, row, col);
					pieceWrappers.add(pieceWrapper);

					setupPieceEventHandlers(pieceWrapper);
					image.setStyleName(STYLE_PIECE_UNSELECTED);

					absolutePanel.add(image, getX(col), getY(row));
				}
			}
		}

		PieceType[] pieceTypes = PieceType.values();

		if (boardConfiguration.isShowSenteKomadai()) {
			senteKomadai.removeAll();
			int[] sentePieces = position.getSenteKomadai().getPieces();
			for (int i = 0; i < sentePieces.length; i++) {
				for (int j = 0; j < sentePieces[i]; j++) {
					Piece piece = Piece.getPiece(pieceTypes[i], true);
					final Image image = new Image(PieceGraphics.getPieceImage(piece));
					PieceWrapper pieceWrapper = new PieceWrapper(piece, image, -1, -1);
					pieceWrapper.setInKomadai(true);
					pieceWrappers.add(pieceWrapper);

					setupPieceEventHandlers(pieceWrapper);
					image.setStyleName(STYLE_PIECE_UNSELECTED);

					Point point = senteKomadai.addPiece(piece);

					absolutePanel.add(image, senteKomadaiX + point.x, senteKomadaiY + point.y);
				}
			}
		}

		if (boardConfiguration.isShowGoteKomadai()) {
			goteKomadai.removeAll();
			int[] gotePieces = position.getGoteKomadai().getPieces();
			for (int i = 0; i < gotePieces.length; i++) {
				for (int j = 0; j < gotePieces[i]; j++) {
					Piece piece = Piece.getPiece(pieceTypes[i], false);
					final Image image = new Image(PieceGraphics.getPieceImage(piece));
					PieceWrapper pieceWrapper = new PieceWrapper(piece, image, -2, -2);
					pieceWrapper.setInKomadai(true);
					pieceWrappers.add(pieceWrapper);

					setupPieceEventHandlers(pieceWrapper);
					image.setStyleName(STYLE_PIECE_UNSELECTED);

					Point point = goteKomadai.addPiece(piece);

					absolutePanel.add(image, TATAMI_LEFT_MARGIN + point.x, TATAMI_TOP_MARGIN + point.y);
				}
			}
		}

	}

	private Piece getPiece(final int row, final int col) {
		return position.getShogiBoardState().getPieceAt(getSquare(row, col));
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
		image.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(final MouseDownEvent event) {
				event.preventDefault();
			}
		});

		image.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				if (selectedPiece != null) {
					Piece piece = selectedPiece.getPiece();

					if (selectedPiece.isInKomadai()) {
						DropMove move = new DropMove(piece.isSentePiece(), piece.getPieceType(), getSquare(row, col));
						if (!boardConfiguration.isAllowOnlyLegalMoves()
								|| shogiRulesEngine.isMoveLegalInPosition(position, move)) {
							playMove(move);
						}
					} else {
						NormalMove move = new NormalMove(piece,
								getSquare(selectedPiece.getRow(), selectedPiece.getColumn()), getSquare(row, col),
								false);
						if (!event.isControlKeyDown() && shogiRulesEngine.canMoveWithPromotion(position, move)) {
							move.setPromote(true);
						}
						if (!boardConfiguration.isAllowOnlyLegalMoves()
								|| shogiRulesEngine.isMoveLegalInPosition(position, move)) {
							playMove(move);
						}
					}

					// selectedPiece.setColumn(col);
					// selectedPiece.setRow(row);
					// selectedPiece.setInKomadai(false);

					// absolutePanel.add(selectedPiece.getImage(), getX(col),
					// getY(row));
					// position.getShogiBoardState().setPieceAt(getSquare(row,
					// col), piece);

					unselect();
				}
			}
		});
	}

	private void setupPieceEventHandlers(final PieceWrapper pieceWrapper) {
		pieceWrapper.getImage().addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(final MouseDownEvent event) {
				event.preventDefault();
			}
		});

		pieceWrapper.getImage().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				if (canPlayMove()) {
					if (selectedPiece == pieceWrapper) {
						unselect();
					} else {
						if (selectedPiece != null) {
							if (!selectedPiece.isInKomadai() && selectedPiece.getPiece().isSentePiece() != pieceWrapper
									.getPiece().isSentePiece()) {
								CaptureMove move = new CaptureMove(selectedPiece.getPiece(), selectedPiece.getSquare(),
										pieceWrapper.getSquare(), false, pieceWrapper.getPiece());
								if (!event.isControlKeyDown()
										&& shogiRulesEngine.canMoveWithPromotion(position, move)) {
									move.setPromote(true);
								}
								if (!boardConfiguration.isAllowOnlyLegalMoves()
										|| shogiRulesEngine.isMoveLegalInPosition(position, move)) {
									playMove(move);
								}
								return;
							} else {
								unselect();
							}
						}
						if (position.isSenteToPlay() == pieceWrapper.getPiece().isSentePiece()) {
							selectedPiece = pieceWrapper;
							pieceWrapper.getImage().setStyleName(STYLE_PIECE_SELECTED);

							if (!pieceWrapper.isInKomadai()) {
								List<Square> possibleTargets = shogiRulesEngine
										.getPossibleTargetSquaresFromSquareInPosition(position,
												getSquare(pieceWrapper.getRow(), pieceWrapper.getColumn()));
								for (Square square : possibleTargets) {
									squareImages[square.getRow() - 1][8 - (square.getColumn() - 1)]
											.setStyleName("gwt-square-selected");
								}
							}
						}
					}
				}
			}

		});

		if (boardConfiguration.isShowPossibleMovesOnPieceMouseOver()) {

			pieceWrapper.getImage().addMouseOverHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(final MouseOverEvent event) {
					// GWT.log("mouse over");
					if (selectedPiece == null) {
						if (!pieceWrapper.isInKomadai()) {
							if (position.isSenteToPlay() == pieceWrapper.getPiece().isSentePiece()) {
								List<Square> possibleTargets = shogiRulesEngine
										.getPossibleTargetSquaresFromSquareInPosition(position,
												getSquare(pieceWrapper.getRow(), pieceWrapper.getColumn()));
								for (Square square : possibleTargets) {
									squareImages[square.getRow() - 1][8 - (square.getColumn() - 1)]
											.setStyleName("gwt-square-selected");
								}
								squareImages[pieceWrapper.getRow()][pieceWrapper.getColumn()]
										.setStyleName("gwt-square-selected");
							}
						}
					}

				}
			});

			pieceWrapper.getImage().addMouseOutHandler(new MouseOutHandler() {

				@Override
				public void onMouseOut(final MouseOutEvent event) {
					if (selectedPiece == null) {
						unselectSquares();
					}
				}
			});
		}
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
				squareImages[i][j].setStyleName("gwt-square-unselected");
			}
		}
	}

	@Override
	public void onClick(final ClickEvent event) {

		Object source = event.getSource();
		if (source == senteKomadaiImage && selectedPiece != null) {
			Piece piece = selectedPiece.getPiece();
			selectedPiece.setInKomadai(true);

			Point point = senteKomadai.addPiece(piece);

			absolutePanel.add(selectedPiece.getImage(), senteKomadaiX + point.x, senteKomadaiY + point.y);
			unselect();
		} else if (source == goteKomadaiImage && selectedPiece != null) {
			Piece piece = selectedPiece.getPiece();
			selectedPiece.setInKomadai(true);

			Point point = goteKomadai.addPiece(piece);

			absolutePanel.add(selectedPiece.getImage(), TATAMI_LEFT_MARGIN + point.x, TATAMI_TOP_MARGIN + point.y);
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
		String usfMove = UsfMoveConverter.toUsfString(move);
		GWT.log(activityId + ": Playing " + usfMove);

		if (move instanceof DropMove) {
			playDropMove((DropMove) move);
		} else if (move instanceof CaptureMove) {
			playCaptureMove((CaptureMove) move);
		} else if (move instanceof NormalMove) {
			playNormalMove((NormalMove) move);
		} else {
			GWT.log(activityId + ": unknown move");
		}

		eventBus.fireEvent(new MovePlayedEvent(move));
	}

	public boolean canPlayMove() {
		return (position.isSenteToPlay() && boardConfiguration.isPlaySenteMoves()
				|| !position.isSenteToPlay() && boardConfiguration.isPlayGoteMoves());
	}

	private void playNormalMove(final NormalMove move) {
		// TODO Auto-generated method stub

	}

	private void playCaptureMove(final CaptureMove move) {
		// TODO Auto-generated method stub

	}

	private void playDropMove(final DropMove move) {
		// TODO Auto-generated method stub

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
			absolutePanel.add(panel, senteKomadaiX, boardTop);
		}
	}

	@EventHandler
	public void onPositionChanged(final PositionChangedEvent event) {
		setPosition(event.getPosition());
	}

}
