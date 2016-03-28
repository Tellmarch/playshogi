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
import com.playshogi.website.gwt.client.events.UserSkippedProblemEvent;
import com.playshogi.website.gwt.client.widget.board.GameNavigator;

public class ProblemFeedbackPanel extends Composite implements ClickHandler {

	interface MyEventBinder extends EventBinder<ProblemFeedbackPanel> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	SafeHtml chooseHtml = SafeHtmlUtils.fromSafeConstant("Play the correct move!");
	SafeHtml correctHtml = SafeHtmlUtils.fromSafeConstant("Correct!");
	SafeHtml wrongHtml = SafeHtmlUtils.fromSafeConstant("Wrong!");

	private final EventBus eventBus;
	private final Button skipButton;

	private final HTML messagePanel;

	public ProblemFeedbackPanel(final EventBus eventBus, final GameNavigator gameNavigator) {
		this.eventBus = eventBus;
		FlowPanel verticalPanel = new FlowPanel();
		verticalPanel.add(gameNavigator);

		skipButton = new Button("Skip");
		skipButton.addClickHandler(this);

		verticalPanel.add(skipButton);

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
			eventBus.fireEvent(new UserSkippedProblemEvent());
		}
	}

	@EventHandler
	public void onNewVariation(final NewVariationPlayedEvent event) {
		messagePanel.setHTML(wrongHtml);
	}

	@EventHandler
	public void onEndOfVariation(final EndOfVariationReachedEvent event) {
		messagePanel.setHTML(correctHtml);
	}
}
