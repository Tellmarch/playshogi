package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LoginView extends Composite implements ClickHandler {
	interface Binder extends UiBinder<Widget, LoginView> {
	}

	private final Binder uiBinder = GWT.create(Binder.class);

	@UiField InputElement email;
	@UiField InputElement password;
	@UiField ButtonElement login;
	@UiField ButtonElement register;

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
