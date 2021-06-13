package com.playshogi.website.gwt.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.util.ElementWidget;
import elemental2.dom.HTMLDivElement;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.cards.Card;
import org.dominokit.domino.ui.forms.TextBox;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.style.Styles;
import org.gwtproject.safehtml.shared.SafeHtmlBuilder;
import org.jboss.elemento.Elements;

import static org.jboss.elemento.Elements.div;

@Singleton
public class LoginView extends Composite {


    private final TextBox password;
    private final TextBox userName;
    @Inject
    private SessionInformation sessionInformation;

    public LoginView() {

        password = TextBox.password("Password")
                .addLeftAddOn(Icons.ALL.security())
                .setRequired(true)
                .setAutoValidation(true);
        userName = TextBox.create("User name")
                .addLeftAddOn(Icons.ALL.person())
                .setRequired(true)
                .setAutoValidation(true)
                .setHelperText("Only alphanumerical or underscore, 3 to 25 characters");


        HTMLDivElement element = Row.create()
                .appendChild(
                        Column.span4()
                                .offset4()
                                .appendChild(
                                        Card.create("LOGIN")
                                                .setHeaderBackground(Color.DEEP_PURPLE_LIGHTEN_2)
                                                .appendChild(
                                                        userName)
                                                .appendChild(
                                                        password)
                                                .appendChild(Elements.p().innerHtml(new SafeHtmlBuilder().appendHtmlConstant("By registering, you agree to be bound by our <a href=\"terms.html\" " +
                                                        "target=\"_blank\">Terms of Service and Privacy Policy</a>" +
                                                        ".</br>").toSafeHtml()))
                                                .appendChild(
                                                        div()
                                                                .add(
                                                                        Button.create(Icons.ALL.lock_open())
                                                                                .setBackground(Color.DEEP_PURPLE_LIGHTEN_2)
                                                                                .setContent("Login")
                                                                                .addClickListener(evt -> login())
                                                                                .styler(style -> style.setMinWidth(
                                                                                        "120px")))
                                                                .add(
                                                                        Button.create("Register")
                                                                                .setContent("Register")
                                                                                .linkify()
                                                                                .addClickListener(evt -> register())
                                                                                .style()
                                                                                .add(Styles.pull_right)))))
                .element();
        initWidget(new ElementWidget(element));
    }

    private void login() {
        sessionInformation.login(userName.getValue(), password.getValue());

    }

    private void register() {
        sessionInformation.register(userName.getValue(), password.getValue());
    }

}
