package com.playshogi.website.gwt.client.widget.board;

import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.website.gwt.client.UserPreferences;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.KeyboardEvent;
import elemental2.dom.MouseEvent;
import org.jboss.elemento.Elements;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HtmlContentBuilder;

public class GamePreview {

    private final BoardPreview boardPreview;
    private final GameNavigation gameNavigation;
    private final HtmlContentBuilder<HTMLDivElement> div;

    public GamePreview(final UserPreferences userPreferences, final GameRecord gameRecord, final double scale) {
        div = Elements.div();
        gameNavigation = new GameNavigation(gameRecord.getGameTree());
        for (int i = 0; i < 20; i++) {
            gameNavigation.moveForward();
        }
        boardPreview = new BoardPreview(gameNavigation.getPosition(), false,
                userPreferences, scale);
        div.add(boardPreview.asElement());
        div.on(EventType.keydown, this::handleKeyDownEvent);
        div.on(EventType.click, this::handleClick);
        div.attr("tabindex", "-1");
    }

    public ReadOnlyShogiPosition getCurrentPosition() {
        return gameNavigation.getPosition();
    }

    private void handleClick(final MouseEvent e) {
        gameNavigation.moveForward();
        boardPreview.showPosition(gameNavigation.getPosition());
    }

    private void handleKeyDownEvent(final KeyboardEvent event) {
        String code = event.code;
        if ("ArrowRight".equals(code) || "ArrowDown".equals(code) || "KeyD".equals(code)) {
            gameNavigation.moveForward();
            boardPreview.showPosition(gameNavigation.getPosition());
        } else if ("ArrowLeft".equals(code) || "ArrowUp".equals(code) || "KeyA".equals(code)) {
            gameNavigation.moveBack();
            boardPreview.showPosition(gameNavigation.getPosition());
        }
    }

    public Element asElement() {
        return div.element();
    }
}
