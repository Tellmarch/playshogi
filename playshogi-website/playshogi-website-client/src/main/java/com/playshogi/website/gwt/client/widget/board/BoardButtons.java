package com.playshogi.website.gwt.client.widget.board;

import com.google.gwt.user.client.ui.Widget;
import com.playshogi.website.gwt.client.events.kifu.ClearDecorationsEvent;
import com.playshogi.website.gwt.client.util.ElementWidget;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.themes.Theme;

public class BoardButtons {

    public static Button createSettingsButton(final ShogiBoard shogiBoard) {
        return Button.createPrimary(Icons.ALL.settings_mdi())
                .setBackground(Theme.DEEP_PURPLE.color()).circle()
                .setTooltip("Settings")
                .addClickListener(e -> shogiBoard.getBoardSettingsPanel().showInDialog());
    }

    public static Widget createSettingsWidget(final ShogiBoard shogiBoard) {
        return new ElementWidget(createSettingsButton(shogiBoard).element());
    }

    public static Button createClearArrowsButton(final ShogiBoard shogiBoard) {
        return Button.createPrimary(Icons.ALL.eraser_mdi())
                .setBackground(Theme.DEEP_PURPLE.color()).circle()
                .addClickListener(e -> shogiBoard.getEventBus().fireEvent(new ClearDecorationsEvent()))
                .setTooltip("Clear arrows")
                .style().setMarginLeft("1em").get();
    }

    public static Widget createClearArrowsWidget(final ShogiBoard shogiBoard) {
        return new ElementWidget(createClearArrowsButton(shogiBoard).element());
    }

}
