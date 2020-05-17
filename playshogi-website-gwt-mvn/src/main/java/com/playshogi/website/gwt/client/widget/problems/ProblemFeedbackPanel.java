package com.playshogi.website.gwt.client.widget.problems;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.*;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;

public class ProblemFeedbackPanel extends Composite implements ClickHandler {

    interface MyEventBinder extends EventBinder<ProblemFeedbackPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private SafeHtml chooseHtml = SafeHtmlUtils
            .fromSafeConstant("Play the correct move!<br>(Ctrl+click to play without promotion)");
    private SafeHtml wrongHtml = SafeHtmlUtils.fromSafeConstant("<p style=\"font-size:20px;color:red\">Wrong!</p>");
    private SafeHtml correctHtml = SafeHtmlUtils.fromSafeConstant("<p style=\"font-size:20px;" +
            "color:green\">Correct!</p>");

    private EventBus eventBus;
    private Button skipButton;

    private final HTML messagePanel;

    public ProblemFeedbackPanel(final GameNavigator gameNavigator, boolean allowSkip) {

        FlowPanel verticalPanel = new FlowPanel();
        if (gameNavigator != null) {
            verticalPanel.add(gameNavigator);
        }

        if (allowSkip) {
            skipButton = new Button("Skip/Next");
            skipButton.addClickHandler(this);
            verticalPanel.add(skipButton);
        }

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        messagePanel = new HTML();
        messagePanel.setHTML(chooseHtml);
        messagePanel.getElement().getStyle().setBackgroundColor("White");

        verticalPanel.add(messagePanel);

        initWidget(verticalPanel);
    }

    @Override
    public void onClick(final ClickEvent event) {
        Object source = event.getSource();
        if (source == skipButton) {
            messagePanel.setHTML(chooseHtml);
            eventBus.fireEvent(new UserSkippedProblemEvent());
        }
    }

    @EventHandler
    public void onNewVariation(final NewVariationPlayedEvent event) {
        GWT.log("Problem feedback: handle new variation played event");
        messagePanel.setHTML(wrongHtml);
        eventBus.fireEvent(new UserFinishedProblemEvent(false));

    }

    @EventHandler
    public void onEndOfVariation(final EndOfVariationReachedEvent event) {
        GWT.log("Problem feedback: handle end of variation reached event");
        messagePanel.setHTML(correctHtml);
        eventBus.fireEvent(new UserFinishedProblemEvent(true));
    }

    @EventHandler
    public void onUserNavigatedBack(final UserNavigatedBackEvent event) {
        GWT.log("Problem feedback: handle user navigated back event");
        messagePanel.setHTML(chooseHtml);
    }

    @EventHandler
    public void onByoYomiSurvivalFinishedEvent(final ByoYomiSurvivalFinishedEvent event) {
        GWT.log("Problem feedback: handle ByoYomiSurvivalFinishedEvent");
        messagePanel.setHTML(SafeHtmlUtils.fromTrustedString("<p style=\"font-size:20px;" +
                "color:red\">Event complete! </br> Final Score: " + event.getFinalScore() +
                " </br> Total time: " + event.getTotalTimeSec() + "s</p>"));
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating Problem feedback panel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        messagePanel.setHTML(chooseHtml);
    }
}
