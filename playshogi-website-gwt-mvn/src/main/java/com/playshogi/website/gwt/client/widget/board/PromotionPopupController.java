package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.moves.NormalMove;

class PromotionPopupController {

    private final PopupPanel promotionPopupPanel = createPromotionPopupPanel();
    private NormalMove promotionPopupMove;
    private Image unPromotedImage;
    private Image promotedImage;

    private ShogiBoard shogiBoard;

    PromotionPopupController(ShogiBoard shogiBoard) {
        this.shogiBoard = shogiBoard;
    }

    void showPromotionPopup(final Image image, NormalMove move) {
        promotionPopupMove = move;
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
            promotionPopupMove.setPromote(true);
            shogiBoard.playNormalMoveIfAllowed(promotionPopupMove);
            popup.hide();
        });
        unPromotedImage.addClickHandler(clickEvent -> {
            shogiBoard.playNormalMoveIfAllowed(promotionPopupMove);
            popup.hide();
        });
        return popup;
    }

}
