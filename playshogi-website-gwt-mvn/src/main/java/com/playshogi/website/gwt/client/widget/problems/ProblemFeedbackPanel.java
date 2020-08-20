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
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.website.gwt.client.events.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.UserFinishedProblemEvent;
import com.playshogi.website.gwt.client.events.UserNavigatedBackEvent;
import com.playshogi.website.gwt.client.events.UserSkippedProblemEvent;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;

public class ProblemFeedbackPanel extends Composite implements ClickHandler {

    interface MyEventBinder extends EventBinder<ProblemFeedbackPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final SafeHtml chooseHtml = SafeHtmlUtils
            .fromSafeConstant("Play the correct move!<br>");
    private final SafeHtml wrongHtml = SafeHtmlUtils.fromSafeConstant("<p style=\"font-size:20px;color:red\">Wrong!</p>");
    private final SafeHtml correctHtml = SafeHtmlUtils.fromSafeConstant("<p style=\"font-size:20px;" +
            "color:green\">Correct!</p>");

    private EventBus eventBus;
    private Button skipButton;

    private final HTML messagePanel;
    private final HTML positionPanel;

    public ProblemFeedbackPanel(final GameNavigator gameNavigator) {

        FlowPanel flowPanel = new FlowPanel();
        if (gameNavigator != null) {
            flowPanel.add(gameNavigator);
        }

        skipButton = new Button("Skip/Next");
        skipButton.addClickHandler(this);
        flowPanel.add(skipButton);

        flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        messagePanel = new HTML();
        messagePanel.setHTML(chooseHtml);
        messagePanel.getElement().getStyle().setBackgroundColor("White");

        flowPanel.add(messagePanel);

        positionPanel = new HTML();
        positionPanel.getElement().getStyle().setBackgroundColor("White");

        flowPanel.add(positionPanel);

        initWidget(flowPanel);
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

    public void activate(final EventBus eventBus) {
        GWT.log("Activating Problem feedback panel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        messagePanel.setHTML(chooseHtml);
        positionPanel.setVisible(false);
    }

    @EventHandler
    public void onPositionChanged(final PositionChangedEvent event) {
        GWT.log("Problem feedback: position changed");
        SafeHtml html = SafeHtmlUtils.fromTrustedString("<nobr>" + SfenConverter.toSFENWithMoveCount(event.getPosition()) + "</nobr>");
        positionPanel.setHTML(html);
        positionPanel.setVisible(true);
    }
}
