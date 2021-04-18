package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.moves.NormalMove;

class PromotionPopupController {

    private final PopupPanel promotionPopupPanel = createPromotionPopupPanel();
    private NormalMove move;
    private NormalMove promotionMove;
    private Image unPromotedImage;
    private Image promotedImage;

    private ShogiBoard shogiBoard;

    PromotionPopupController(ShogiBoard shogiBoard) {
        this.shogiBoard = shogiBoard;
    }

    void showPromotionPopup(final Image image, NormalMove move, NormalMove promotionMove) {
        this.move = move;
        this.promotionMove = promotionMove;
        // promotionMove contains the promotion piece, so it may be possible to simplify this
        unPromotedImage.setResource(PieceGraphics.getPieceImage(move.getPiece().getPieceType(), false));
        promotedImage.setResource(PieceGraphics.getPieceImage(move.getPiece().getPieceType(), true));
        promotionPopupPanel.setPopupPosition(image.getAbsoluteLeft() - 5, image.getAbsoluteTop() - 5);
        promotionPopupPanel.show();
    }

    private PopupPanel createPromotionPopupPanel() {
        PopupPanel popup = new PopupPanel(false, true);
        FlowPanel flowPanel = new FlowPanel();
        unPromotedImage = new Image(PieceGraphics.getPieceImage(PieceType.PAWN, false));
        promotedImage = new Image(PieceGraphics.getPieceImage(PieceType.PAWN, true));
        flowPanel.add(promotedImage);
        flowPanel.add(unPromotedImage);
        popup.add(flowPanel);
        promotedImage.addClickHandler(clickEvent -> {
            shogiBoard.playNormalMoveIfAllowed(promotionMove);
            popup.hide();
        });
        unPromotedImage.addClickHandler(clickEvent -> {
            shogiBoard.playNormalMoveIfAllowed(move);
            popup.hide();
        });
        return popup;
    }

}
