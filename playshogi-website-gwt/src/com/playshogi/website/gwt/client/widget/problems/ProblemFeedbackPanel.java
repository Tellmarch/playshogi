package com.playshogi.website.gwt.client.widget.problems;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.events.UserSkippedProblemEvent;
import com.playshogi.website.gwt.client.widget.board.GameNavigator;

public class ProblemFeedbackPanel extends Composite implements ClickHandler {

	interface MyEventBinder extends EventBinder<ProblemFeedbackPanel> {
	}

	private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

	private final EventBus eventBus;
	private final Button skipButton;

	public ProblemFeedbackPanel(final EventBus eventBus, final GameNavigator gameNavigator) {
		this.eventBus = eventBus;
		FlowPanel verticalPanel = new FlowPanel();
		verticalPanel.add(gameNavigator);

		skipButton = new Button("Skip");
		skipButton.addClickHandler(this);

		verticalPanel.add(skipButton);

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
}
