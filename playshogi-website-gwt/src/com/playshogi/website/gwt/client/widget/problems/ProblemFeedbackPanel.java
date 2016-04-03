package com.playshogi.website.gwt.client.widget.problems;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.EndOfVariationReachedEvent;
import com.playshogi.website.gwt.client.events.NewVariationPlayedEvent;
import com.playshogi.website.gwt.client.events.UserNavigatedBackEvent;
import com.playshogi.website.gwt.client.events.UserSkippedProblemEvent;
import com.playshogi.website.gwt.client.widget.gamenavigator.GameNavigator;

public class ProblemFeedbackPanel extends Composite implements ClickHandler {

	interface MyEventBinder extends EventBinder<ProblemFeedbackPanel> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	SafeHtml chooseHtml = SafeHtmlUtils
			.fromSafeConstant("Play the correct move!<br>(Ctrl+click to play without promotion)");
	SafeHtml correctHtml = SafeHtmlUtils.fromSafeConstant("<p style=\"font-size:20px;color:green\">Correct!</p>");
	SafeHtml wrongHtml = SafeHtmlUtils.fromSafeConstant("<p style=\"font-size:20px;color:red\">Wrong!</p>");

	private final EventBus eventBus;
	private final Button skipButton;

	private final HTML messagePanel;

	public ProblemFeedbackPanel(final EventBus eventBus, final GameNavigator gameNavigator) {
		this.eventBus = eventBus;
		FlowPanel verticalPanel = new FlowPanel();
		verticalPanel.add(gameNavigator);

		skipButton = new Button("Skip/Next");
		skipButton.addClickHandler(this);

		verticalPanel.add(skipButton);

		verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

		messagePanel = new HTML();
		messagePanel.setHTML(chooseHtml);
		messagePanel.getElement().getStyle().setBackgroundColor("White");

		verticalPanel.add(messagePanel);

		eventBinder.bindEventHandlers(this, eventBus);
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
	}

	@EventHandler
	public void onEndOfVariation(final EndOfVariationReachedEvent event) {
		GWT.log("Problem feedback: handle end of variation reached event");
		messagePanel.setHTML(correctHtml);
	}

	@EventHandler
	public void onUserNavigatedBack(final UserNavigatedBackEvent event) {
		GWT.log("Problem feedback: handle user navigated back event");
		messagePanel.setHTML(chooseHtml);
	}
}
