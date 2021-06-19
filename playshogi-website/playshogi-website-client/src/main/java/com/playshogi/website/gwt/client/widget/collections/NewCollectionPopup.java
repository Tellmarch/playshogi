package com.playshogi.website.gwt.client.widget.collections;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.events.collections.SaveDraftCollectionEvent;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.modals.ModalDialog;
import org.dominokit.domino.ui.tabs.Tab;
import org.dominokit.domino.ui.tabs.TabsPanel;
import org.jboss.elemento.Elements;

import static org.jboss.elemento.Elements.b;

public class NewCollectionPopup {

    interface MyEventBinder extends EventBinder<NewCollectionPopup> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ModalDialog dialog;
    private TabsPanel tabs;
    private EventBus eventBus;

    public NewCollectionPopup() {
        dialog = createModalDialog();
    }

    private ModalDialog createModalDialog() {

        ModalDialog modal = ModalDialog.create("Create new Collection").setAutoClose(false).large();
        tabs = TabsPanel.create()
                .appendChild(createGameCollectionsForm())
                .appendChild(createProblemCollectionsForm());

        modal.appendChild(tabs);

        Button closeButton = Button.create("CANCEL").linkify();
        closeButton.addClickListener(evt -> modal.close());
        modal.appendFooterChild(closeButton);
        return modal;

    }

    private Tab createGameCollectionsForm() {
        GameCollectionPropertiesForm properties = new GameCollectionPropertiesForm();

        return Tab.create("New Game Collection")
                .appendChild(b().textContent("Create a new Game Collection, where you can save your personal Kifu or " +
                        "any game you would like."))
                .appendChild(Elements.p())
                .appendChild(properties.getForm())
                .appendChild(Button.createPrimary("Create new Game Collection").addClickListener(
                        evt -> {
                            eventBus.fireEvent(SaveDraftCollectionEvent.ofGames(null, properties.getTitle(),
                                    properties.getDescription(), properties.getVisibility()));
                            dialog.close();
                        }));
    }

    private Tab createProblemCollectionsForm() {
        ProblemCollectionPropertiesForm properties = new ProblemCollectionPropertiesForm();
        return Tab.create("New Problem Collection")
                .appendChild(b().textContent("Create a new Problem Collection, for you to practice on your favorite " +
                        "problems, or to share with your friends."))
                .appendChild(Elements.p())
                .appendChild(properties.getForm())
                .appendChild(Button.createPrimary("Create new Problem Collection").addClickListener(
                        evt -> {
                            eventBus.fireEvent(SaveDraftCollectionEvent.ofProblems(null, properties.getTitle(),
                                    properties.getDescription(), properties.getVisibility(), properties.getDifficulty(),
                                    properties.getTags()));
                            dialog.close();
                        }));
    }

    public void show() {
        reset();
        dialog.open();
    }

    private void reset() {
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }
}
