package com.playshogi.website.gwt.client.widget.lessons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.gametree.VisitedProgressEvent;
import com.playshogi.website.gwt.client.events.tutorial.MarkLessonCompleteEvent;
import com.playshogi.website.gwt.client.util.ElementWidget;
import elemental2.dom.HTMLDivElement;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.progress.Progress;
import org.dominokit.domino.ui.progress.ProgressBar;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.utils.DominoElement;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

public class LessonFeedbackPanel {

    interface MyEventBinder extends EventBinder<LessonFeedbackPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);


    private final HtmlContentBuilder<HTMLDivElement> div;
    private final Button completeButton;
    private final Progress progress;
    private final ProgressBar progressBar;
    private final HtmlContentBuilder<HTMLDivElement> completeText;

    private EventBus eventBus;

    public LessonFeedbackPanel() {
        div = Elements.div();
        DominoElement.of(div).style().setBackgroundColor(Color.WHITE.getHex() + "a0")
                .setPadding("0.5em");

        progressBar = ProgressBar.create(100).showText();
        progress = Progress.create().appendChild(progressBar.setValue(0)).style().setMargin("0.5em").get();
        completeButton = Button.create("Mark Complete").style().setMargin("0.5em").get();
        completeButton.addClickListener(evt -> {
            eventBus.fireEvent(new MarkLessonCompleteEvent());
            completeButton.hide();
        });

        div.add(TextNode.of("Progress:"));
        div.add(progress);
        div.add(completeButton);

        completeText = Elements.div();
        completeText.add(TextNode.of("Lesson complete!"));
        completeText.hidden(true);

        div.add(completeText);
    }

    public Widget getAsWidget() {
        return new ElementWidget(div.element());
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onVisitedProgress(final VisitedProgressEvent event) {
        GWT.log("LessonFeedbackPanel: Handling VisitedProgressEvent");
        progressBar.setMaxValue(event.getTotal());
        progressBar.setValue(event.getVisited());

        if (event.getTotal() == event.getVisited()) {
            completeText.hidden(false);
            completeButton.hide();
        } else {
            completeText.hidden(true);
            completeButton.show();
        }
    }
}
