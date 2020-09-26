package com.playshogi.website.gwt.client.widget.problems;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.gametree.UserNavigatedBackEvent;
import com.playshogi.website.gwt.client.events.puzzles.ByoYomiSurvivalFinishedEvent;
import com.playshogi.website.gwt.client.events.puzzles.UserFinishedProblemEvent;
import com.playshogi.website.gwt.client.widget.timer.ByoYomiTimerPanel;

public class ByoYomiFeedbackPanel extends Composite {

    interface MyEventBinder extends EventBinder<ByoYomiFeedbackPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final SafeHtml chooseHtml = SafeHtmlUtils
            .fromSafeConstant("Play the correct move!<br>");
    private final SafeHtml wrongHtml = SafeHtmlUtils.fromSafeConstant("<p style=\"font-size:20px;color:red\">Wrong!</p>");
    private final SafeHtml correctHtml = SafeHtmlUtils.fromSafeConstant("<p style=\"font-size:20px;" +
            "color:green\">Correct!</p>");

    private EventBus eventBus;

    private final HTML messagePanel;
    private final HTML summaryPanel;

    private final ByoYomiTimerPanel byoTomiTimerPanel;

    public ByoYomiFeedbackPanel() {

        FlowPanel flowPanel = new FlowPanel();

        byoTomiTimerPanel = new ByoYomiTimerPanel();
        flowPanel.add(byoTomiTimerPanel);

        flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        messagePanel = new HTML();
        messagePanel.setHTML(chooseHtml);
        messagePanel.getElement().getStyle().setBackgroundColor("White");

        flowPanel.add(messagePanel);

        summaryPanel = new HTML();
        summaryPanel.getElement().getStyle().setBackgroundColor("White");

        flowPanel.add(summaryPanel);

        initWidget(flowPanel);
    }

    @EventHandler
    public void onUserFinishedProblemEvent(final UserFinishedProblemEvent event) {
        GWT.log("Problem feedback: handle UserFinishedProblemEvent");
        if (event.isSuccess()) {
            messagePanel.setHTML(correctHtml);
        } else {
            messagePanel.setHTML(wrongHtml);
        }
    }

    @EventHandler
    public void onUserNavigatedBack(final UserNavigatedBackEvent event) {
        GWT.log("Problem feedback: handle user navigated back event");
        messagePanel.setHTML(chooseHtml);
    }

    @EventHandler
    public void onByoYomiSurvivalFinishedEvent(final ByoYomiSurvivalFinishedEvent event) {
        GWT.log("Problem feedback: handle ByoYomiSurvivalFinishedEvent");
        summaryPanel.setHTML(SafeHtmlUtils.fromTrustedString("<p style=\"font-size:20px;color:black\">" +
                "Event complete!<br/>" +
                "Final score: " + event.getFinalScore() + " <br/>" +
                "Total time: " + event.getTotalTimeSec() + "s</p>"));
        summaryPanel.setVisible(true);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating Byo Yomi feedback panel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        messagePanel.setHTML(chooseHtml);
        summaryPanel.setVisible(false);
        byoTomiTimerPanel.activate(eventBus);
    }

    public void setTimerVisible(boolean visible) {
        byoTomiTimerPanel.setVisible(visible);
    }
}
