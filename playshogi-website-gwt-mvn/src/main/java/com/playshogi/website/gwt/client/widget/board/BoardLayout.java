package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

class BoardLayout {
    static final int TATAMI_LEFT_MARGIN = 10;
    static final int TATAMI_TOP_MARGIN = 10;
    private static final int TATAMI_INSIDE_MARGIN = 5;
    private static final int BOARD_LEFT_MARGIN = 11;
    private static final int BOARD_TOP_MARGIN = 11;
    static final int SQUARE_WIDTH = 43;
    static final int SQUARE_HEIGHT = 48;

    private final int boardLeft;
    private final int boardTop;
    private final int senteKomadaiX;
    private final int senteKomadaiY;
    private final int komadaiWidth;

    private final int upperRightPanelX;
    private final int upperRightPanelY;

    private final int lowerLeftPanelX;
    private final int lowerLeftPanelY;

    private final AbsolutePanel absolutePanel;

    BoardLayout(final BoardBundle boardResources, final AbsolutePanel absolutePanel, final Image goteKomadaiImage,
                final Image senteKomadaiImage) {
        this.absolutePanel = absolutePanel;

        komadaiWidth = goteKomadaiImage.getWidth();

        boardLeft = TATAMI_LEFT_MARGIN + komadaiWidth + TATAMI_INSIDE_MARGIN;
        boardTop = TATAMI_TOP_MARGIN;

        Image grid = new Image(boardResources.masu_dot());
        Image coordinates = new Image(boardResources.scoordE());
        Image ban = new Image(boardResources.ban_kaya_a());
        Image tatami = new Image(boardResources.bg_tatami());

        absolutePanel.setSize(tatami.getWidth() + "px", tatami.getHeight() + "px");
        absolutePanel.add(tatami, 0, 0);
        absolutePanel.add(ban, boardLeft, boardTop);
        absolutePanel.add(grid, boardLeft, boardTop);
        absolutePanel.add(coordinates, boardLeft, boardTop);

        senteKomadaiX = boardLeft + ban.getWidth() + TATAMI_INSIDE_MARGIN;
        senteKomadaiY = TATAMI_TOP_MARGIN + ban.getHeight() - senteKomadaiImage.getHeight();

        upperRightPanelX = senteKomadaiX;
        upperRightPanelY = TATAMI_TOP_MARGIN;

        lowerLeftPanelX = TATAMI_LEFT_MARGIN;
        lowerLeftPanelY = TATAMI_TOP_MARGIN + goteKomadaiImage.getHeight() + TATAMI_INSIDE_MARGIN;
    }

    void addGoteKomadai(final Image goteKomadaiImage) {
        absolutePanel.add(goteKomadaiImage, TATAMI_LEFT_MARGIN, TATAMI_TOP_MARGIN);
    }

    void addSenteKomadai(final Image senteKomadaiImage) {
        absolutePanel.add(senteKomadaiImage, senteKomadaiX, senteKomadaiY);
    }

    void addUpperRightPanel(final Widget panel) {
        panel.setWidth(komadaiWidth + "px");
        panel.setHeight((senteKomadaiY - boardTop - BOARD_TOP_MARGIN) + "px");
        absolutePanel.add(panel, upperRightPanelX, upperRightPanelY);
    }

    void addLowerLeftPanel(final Widget panel) {
        panel.setWidth(komadaiWidth + "px");
        panel.setHeight((senteKomadaiY - boardTop - BOARD_TOP_MARGIN) + "px");
        absolutePanel.add(panel, lowerLeftPanelX, lowerLeftPanelY);
    }

    int getSenteKomadaiX() {
        return senteKomadaiX;
    }

    int getSenteKomadaiY() {
        return senteKomadaiY;
    }

    int getKomadaiWidth() {
        return komadaiWidth;
    }

    int getY(final int row) {
        return boardTop + BOARD_TOP_MARGIN + row * SQUARE_HEIGHT;
    }

    int getX(final int col) {
        return boardLeft + BOARD_LEFT_MARGIN + col * SQUARE_WIDTH;
    }
}
