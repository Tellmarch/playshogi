package com.playshogi.website.gwt.client.widget.problems;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.events.gametree.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.gametree.UserNavigatedBackEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestPositionEvaluationEvent;
import com.playshogi.website.gwt.client.events.puzzles.UserFinishedProblemEvent;
import com.playshogi.website.gwt.client.events.puzzles.UserSkippedProblemEvent;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigatorPanel;

import java.util.Optional;

public class ProblemFeedbackPanel extends Composite implements ClickHandler {


    interface MyEventBinder extends EventBinder<ProblemFeedbackPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final SafeHtml chooseHtml = SafeHtmlUtils
            .fromSafeConstant("Play the correct move!<br/>");
    private final SafeHtml wrongHtml = SafeHtmlUtils.fromSafeConstant("<p style=\"font-size:20px;" +
            "color:red\">Wrong!</p>");
    private final SafeHtml correctHtml = SafeHtmlUtils.fromSafeConstant("<p style=\"font-size:20px;" +
            "color:green\">Correct!</p>");

    private EventBus eventBus;
    private ShogiPosition currentPosition = null;

    private final Button skipButton;
    private Button tellMeWhyButton;
    private final HTML messagePanel;
    private boolean enableTellMeWhy;

    public ProblemFeedbackPanel(final GameNavigatorPanel gameNavigatorPanel, final boolean enableTellMeWhy) {
        this.enableTellMeWhy = enableTellMeWhy;

        FlowPanel flowPanel = new FlowPanel();
        if (gameNavigatorPanel != null) {
            flowPanel.add(gameNavigatorPanel);
        }

        skipButton = new Button("Skip/Next");
        skipButton.addClickHandler(this);
        flowPanel.add(skipButton);

        Button sfenButton = new Button("SFEN");
        sfenButton.addClickHandler(clickEvent -> {
            if (currentPosition != null) Window.alert(SfenConverter.toSFENWithMoveCount(currentPosition));
        });
        flowPanel.add(sfenButton);

        flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

        messagePanel = new HTML();
        messagePanel.setHTML(chooseHtml);
        messagePanel.getElement().getStyle().setBackgroundColor("White");

        flowPanel.add(messagePanel);

        tellMeWhyButton = new Button("Tell me why!");
        tellMeWhyButton.addClickHandler(clickEvent -> eventBus.fireEvent(new RequestPositionEvaluationEvent()));

        flowPanel.add(tellMeWhyButton);

        tellMeWhyButton.setVisible(false);

        if (enableTellMeWhy) {
            tellMeWhyButton.setVisible(true);
        }

        initWidget(flowPanel);
    }

    public void setEnableTellMeWhy(final boolean enableTellMeWhy) {
        this.enableTellMeWhy = enableTellMeWhy;
    }

    @Override
    public void onClick(final ClickEvent event) {
        Object source = event.getSource();
        if (source == skipButton) {
            messagePanel.setHTML(chooseHtml);
            setTellMeWhyVisibility(false);
            eventBus.fireEvent(new UserSkippedProblemEvent());
        }
    }

    @EventHandler
    public void onUserFinishedProblemEvent(final UserFinishedProblemEvent event) {
        GWT.log("Problem feedback: handle UserFinishedProblemEvent");
        if (event.isSuccess()) {
            messagePanel.setHTML(correctHtml);
            setTellMeWhyVisibility(false);
        } else {
            messagePanel.setHTML(wrongHtml);
            setTellMeWhyVisibility(true);
        }
    }

    @EventHandler
    public void onUserNavigatedBack(final UserNavigatedBackEvent event) {
        GWT.log("Problem feedback: handle user navigated back event");
        messagePanel.setHTML(chooseHtml);
        setTellMeWhyVisibility(false);
    }

    @EventHandler
    public void onPositionChanged(final PositionChangedEvent event) {
        GWT.log("Problem feedback: position changed");
        currentPosition = event.getPosition();
    }

    @EventHandler
    public void onGameTreeChanged(final GameTreeChangedEvent gameTreeChangedEvent) {
        GWT.log("Problem feedback: Handling game tree changed event - move " + gameTreeChangedEvent.getGoToMove());
        Optional<String> tags = gameTreeChangedEvent.getGameTree().getRootNode().getAdditionalTags();
        if (tags.isPresent()) {
            if (tags.get().contains("X:PLAYSHOGI:PROBLEMTYPE:WINNING_OR_LOSING")) {
                messagePanel.setHTML("Only one move is winning - play the correct move!");
            } else if (tags.get().contains("X:PLAYSHOGI:PROBLEMTYPE:MATE_OR_LOSING")) {
                messagePanel.setHTML("You have a forced checkmate - play the correct move!");
            } else if (tags.get().contains("X:PLAYSHOGI:PROBLEMTYPE:MATE_OR_BE_MATED")) {
                messagePanel.setHTML("One move leads to a win by checkmate, others will get you mated - play the " +
                        "correct move!");
            } else if (tags.get().contains("X:PLAYSHOGI:PROBLEMTYPE:WINNING_OR_BE_MATED")) {
                messagePanel.setHTML("One move leads to a win, others will get you mated - play the correct move!");
            } else if (tags.get().contains("X:PLAYSHOGI:PROBLEMTYPE:ESCAPE_MATE")) {
                messagePanel.setHTML("Escape the mate - play the correct move!");
            }
        }
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating Problem feedback panel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        messagePanel.setHTML(chooseHtml);
        setTellMeWhyVisibility(false);
    }

    private void setTellMeWhyVisibility(final boolean b) {
        if (enableTellMeWhy) {
            tellMeWhyButton.setVisible(b);
        }
    }
}
