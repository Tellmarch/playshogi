package com.playshogi.website.gwt.client.widget.problems;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import com.playshogi.website.gwt.client.place.ByoYomiPlace;

public class CustomizeSurvivalPanel extends Composite {

    private final TextBox maxFailures;
    private final TextBox raiseDifficultyEveryN;
    private final TextBox maxTimeSec;
    private final TextBox timePerMove;

    public CustomizeSurvivalPanel() {
        FlowPanel verticalPanel = new FlowPanel();

        Grid grid = new Grid(4, 2);
        grid.setHTML(0, 0, "Number of lives:");
        grid.setHTML(1, 0, "Raise difficulty every:");
        grid.setHTML(2, 0, "Maximum time in seconds:");
        grid.setHTML(3, 0, "Maximum time per move:");

        maxFailures = createTextBox("3");
        raiseDifficultyEveryN = createTextBox("5");
        maxTimeSec = createTextBox("300");
        timePerMove = createTextBox("0");

        grid.setWidget(0, 1, maxFailures);
        grid.setWidget(1, 1, raiseDifficultyEveryN);
        grid.setWidget(2, 1, maxTimeSec);
        grid.setWidget(3, 1, timePerMove);

        verticalPanel.add(grid);

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        initWidget(verticalPanel);
    }

    private TextBox createTextBox(String defaultText) {
        TextBox textBox = new TextBox();
        textBox.setText(defaultText);
        textBox.setVisibleLength(13);
        return textBox;
    }

    public ByoYomiPlace getCustomizedPlace() {
        int maxFailuresValue = Integer.parseInt(maxFailures.getText());
        int raiseDifficultyEveryNValue = Integer.parseInt(raiseDifficultyEveryN.getText());
        int maxTimeSecValue = Integer.parseInt(maxTimeSec.getText());
        int timePerMoveValue = Integer.parseInt(timePerMove.getText());
        return new ByoYomiPlace(maxFailuresValue, raiseDifficultyEveryNValue, maxTimeSecValue, false, timePerMoveValue);
    }


}
