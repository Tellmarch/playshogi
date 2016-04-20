package com.playshogi.website.gwt.client.ui;

import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;

public class LoginView extends Composite implements ClickHandler {

	public LoginView() {

		initWidget(uiBinder.createAndBindUi(this));

		this.addDomHandler(this, ClickEvent.getType());
	}

	@Override
	public void onClick(final ClickEvent event) {
		Node target = event.getNativeEvent().getEventTarget().<Node> cast();
		if (login.isOrHasChild(target)) {
			// presenter.login(email.getValue(), password.getValue());
		} else if (register.isOrHasChild(target)) {
			// presenter.register(email.getValue(), password.getValue());
		}
	}

}
