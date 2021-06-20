package com.playshogi.website.gwt.client.widget.problems;

import elemental2.dom.HTMLElement;
import org.dominokit.domino.ui.badges.Badge;
import org.dominokit.domino.ui.style.ColorScheme;
import org.dominokit.domino.ui.utils.ElementUtil;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

public class TagsElement {

    private final HtmlContentBuilder<HTMLElement> span;

    public TagsElement(final String[] tags) {
        span = Elements.span();

        addTags(tags);
    }

    public HTMLElement asElement() {
        return span.element();
    }

    public void setTags(final String[] tags) {
        ElementUtil.clear(span);

        addTags(tags);
    }

    private void addTags(final String[] tags) {
        if (tags != null) {
            for (String tag : tags) {
                span.add(Badge.create(tag)
                        .setBackground(ColorScheme.GREEN.color())
                        .style().setMarginRight("1em")
                        .element());
            }
        }
    }
}
