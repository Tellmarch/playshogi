package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.website.gwt.client.UserPreferences;

class PromotionPopupController {

    private final PopupPanel promotionPopupPanel;
    private NormalMove move;
    private NormalMove promotionMove;
    private Image unPromotedImage;
    private Image promotedImage;

    private ShogiBoard shogiBoard;
    private UserPreferences userPreferences;

    PromotionPopupController(ShogiBoard shogiBoard, final UserPreferences userPreferences) {
        this.shogiBoard = shogiBoard;
        this.userPreferences = userPreferences;
        this.promotionPopupPanel = createPromotionPopupPanel();
    }

    void showPromotionPopup(final Image image, NormalMove move, NormalMove promotionMove) {
        this.move = move;
        this.promotionMove = promotionMove;
        // promotionMove contains the promotion piece, so it may be possible to simplify this
        //TODO if not blind - blind mode different
        unPromotedImage.setResource(PieceGraphics.getPieceImage(move.getPiece().getPieceType(), false, userPreferences.getPieceStyle()));
        promotedImage.setResource(PieceGraphics.getPieceImage(move.getPiece().getPieceType(), true, userPreferences.getPieceStyle()));
        promotionPopupPanel.setPopupPosition(image.getAbsoluteLeft() - 5, image.getAbsoluteTop() - 5);
        promotionPopupPanel.show();
    }

    private PopupPanel createPromotionPopupPanel() {
        FocusPanel focusPanel = new FocusPanel();
        PopupPanel popup = new PopupPanel(false, true) {
            @Override
            public void show() {
                super.show();
                focusPanel.setFocus(true);
            }
        };
        FlowPanel flowPanel = new FlowPanel();
        GWT.log("User preferences " + userPreferences);
        unPromotedImage = new Image(PieceGraphics.getPieceImage(PieceType.PAWN, false, userPreferences.getPieceStyle()));
        promotedImage = new Image(PieceGraphics.getPieceImage(PieceType.PAWN, true, userPreferences.getPieceStyle()));
        flowPanel.add(promotedImage);
        flowPanel.add(unPromotedImage);


        focusPanel.setWidget(flowPanel);
        popup.add(focusPanel);
        promotedImage.addClickHandler(clickEvent -> {
            shogiBoard.playNormalMoveIfAllowed(promotionMove);
            popup.hide();
        });
        unPromotedImage.addClickHandler(clickEvent -> {
            shogiBoard.playNormalMoveIfAllowed(move);
            popup.hide();
        });
        focusPanel.addKeyDownHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                popup.hide();
            }
        });
        return popup;
    }

}
