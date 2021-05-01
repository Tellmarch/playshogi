package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.tutorial.*;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.TutorialPlace;
import com.playshogi.website.gwt.client.tutorial.*;
import com.playshogi.website.gwt.client.widget.PiecesSelectorPanel;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

@Singleton
public class TutorialView extends Composite {

    private static final String TUTORIAL = "tutorial";

    interface MyEventBinder extends EventBinder<TutorialView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);
    private final TutorialMessages tutorialMessages = GWT.create(TutorialMessages.class);

    private final SessionInformation sessionInformation;
    private final ShogiBoard shogiBoard;
    private EventBus eventBus;
    private final AppPlaceHistoryMapper historyMapper;
    private final Tutorials tutorials;
    private final HTML commentHTML;
    private HTML titleHTML;
    private PiecesSelectorPanel piecesSelectorPanel;

    @Inject
    public TutorialView(final AppPlaceHistoryMapper historyMapper, final SessionInformation sessionInformation) {
        GWT.log("Creating tutorial view");

        this.sessionInformation = sessionInformation;
        this.historyMapper = historyMapper;

        AbsolutePanel absolutePanel = getRightPanel();

        shogiBoard = new ShogiBoard(TUTORIAL, sessionInformation.getUserPreferences());
        this.tutorials = new Tutorials(this);
        shogiBoard.setLowerLeftPanel(getOutline());
        shogiBoard.setUpperRightPanel(absolutePanel);

        VerticalPanel verticalPanel = new VerticalPanel();

        verticalPanel.add(shogiBoard);

        commentHTML = new HTML();
        commentHTML.setSize("782px", "150px");
        commentHTML.setStyleName("lesson-content");
        verticalPanel.add(commentHTML);

        initWidget(verticalPanel);
    }

    private AbsolutePanel getRightPanel() {
        AbsolutePanel absolutePanel = new AbsolutePanel();
        titleHTML = new HTML(tutorialMessages.introTitle());
        titleHTML.setStyleName("lesson-title");
        absolutePanel.add(titleHTML, 0, 0);

        Button nextButton = new Button(tutorialMessages.next());
        Button backButton = new Button(tutorialMessages.back());
        Button tryAgainButton = new Button(tutorialMessages.tryAgain());

        absolutePanel.add(backButton, 0, 200);
        absolutePanel.add(tryAgainButton, 50, 200);
        absolutePanel.add(nextButton, 125, 200);

        backButton.addClickHandler(clickEvent -> eventBus.fireEvent(new GoPreviousChapterEvent()));
        nextButton.addClickHandler(clickEvent -> eventBus.fireEvent(new GoNextChapterEvent()));
        tryAgainButton.addClickHandler(clickEvent -> eventBus.fireEvent(new TryChapterAgainEvent()));

        return absolutePanel;
    }

    private ScrollPanel getOutline() {
        FlowPanel flowPanel = new FlowPanel();

        piecesSelectorPanel = new PiecesSelectorPanel();
        flowPanel.add(piecesSelectorPanel);

        flowPanel.add(new Hyperlink(tutorialMessages.introTitle(), historyMapper.getToken(new TutorialPlace(1))));

        // Adds an entry for each piece movement tutorial
        Tutorial[] tutorials = this.tutorials.getTutorials();
        for (int i = 0; i < tutorials.length; i++) {
            if (tutorials[i] instanceof PieceMovementTutorial
                    || tutorials[i] instanceof PromotionTutorial
                    || tutorials[i] instanceof CaptureTutorial
                    || tutorials[i] instanceof SpecialRulesTutorial) {

                flowPanel.add(new Hyperlink(tutorials[i].getTutorialTitle(),
                        historyMapper.getToken(new TutorialPlace(i + 1))));
            }
        }

        flowPanel.getElement().getStyle().setBackgroundColor("#DBCBCB");

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(flowPanel);
        scrollPanel.setSize("100%", "100%");

        return scrollPanel;
    }

    public ShogiBoard getShogiBoard() {
        return shogiBoard;
    }

    public Tutorials getTutorials() {
        return tutorials;
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating tutorial view");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, this.eventBus);
        shogiBoard.activate(eventBus);
        piecesSelectorPanel.activate(eventBus);
        tutorials.activate(eventBus);
    }

    private void setTutorialText(String text) {
        commentHTML.setHTML(text);
    }

    public SessionInformation getSessionInformation() {
        return sessionInformation;
    }

    @EventHandler
    public void onChangeTutorialText(final ChangeTutorialTextEvent event) {
        GWT.log("TutorialView: handle ChangeTutorialTextEvent");
        setTutorialText(event.getText());
    }

    @EventHandler
    public void onChangeTutorialTitle(final ChangeTutorialTitleEvent event) {
        GWT.log("TutorialView: handle ChangeTutorialTitleEvent");
        titleHTML.setHTML(event.getText());
    }
}
