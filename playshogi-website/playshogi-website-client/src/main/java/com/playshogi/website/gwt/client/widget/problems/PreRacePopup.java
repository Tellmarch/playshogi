package com.playshogi.website.gwt.client.widget.problems;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.races.JoinRaceEvent;
import com.playshogi.website.gwt.client.events.races.RaceEvent;
import com.playshogi.website.gwt.client.events.races.StartRaceEvent;
import com.playshogi.website.gwt.client.events.races.WithdrawFromRaceEvent;
import com.playshogi.website.gwt.shared.models.RaceDetails;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;
import org.dominokit.domino.ui.Typography.Paragraph;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.button.ButtonSize;
import org.dominokit.domino.ui.cards.Card;
import org.dominokit.domino.ui.chips.Chip;
import org.dominokit.domino.ui.chips.ChipsGroup;
import org.dominokit.domino.ui.counter.Counter;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.modals.ModalDialog;
import org.dominokit.domino.ui.notifications.Notification;
import org.dominokit.domino.ui.style.ColorScheme;

import java.util.Arrays;
import java.util.Objects;

import static org.dominokit.domino.ui.style.Unit.px;

public class PreRacePopup {
    interface MyEventBinder extends EventBinder<PreRacePopup> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);
    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);

    private final SessionInformation sessionInformation;
    private final ModalDialog modal;

    private Card participantsCard;
    private Button startRace;
    private Button joinRace;
    private Button withdrawFromRace;
    private EventBus eventBus;


    public PreRacePopup(final SessionInformation sessionInformation) {
        this.sessionInformation = sessionInformation;
        this.modal = createModalDialog();
    }

    private ModalDialog createModalDialog() {
        ModalDialog modal = ModalDialog.create("Shogi Tsume Race").setAutoClose(false);

        Card optionsCard = Card.create("Race options");
        ChipsGroup chipsGroup = ChipsGroup.create()
                .appendChild(Chip.create("To the end"))
                .appendChild(Chip.create("Time limit"))
                .appendChild(Chip.create("Combo race"))
                .setColorScheme(ColorScheme.TEAL).selectAt(0);
        optionsCard.appendChild(chipsGroup);
        modal.appendChild(optionsCard);

        Card urlCard = Card.create("Invite Friends");

        urlCard.appendChild(Paragraph.create("To invite someone to play, give them the URL of this page:"));
        org.dominokit.domino.ui.forms.TextBox textBox = org.dominokit.domino.ui.forms.TextBox.create();
        textBox.setValue(com.google.gwt.user.client.Window.Location.getHref());
        urlCard.appendChild(textBox);

        modal.appendChild(urlCard);

        participantsCard = Card.create("Race Participants");

        modal.appendChild(participantsCard);

        startRace = Button.createSuccess(Icons.ALL.flag_checkered_mdi())
                .setContent("START RACE!")
                .setSize(ButtonSize.LARGE)
                .style()
                .setMargin(px.of(5))
                .setMinWidth(px.of(200))
                .get();

        startRace.addClickListener(evt -> {
            eventBus.fireEvent(new StartRaceEvent());
            Counter.countFrom(0).countTo(5).every(1000).incrementBy(1)
                    .onCount(c -> Notification.createDanger("RACE STARTING IN " + (5 - c) + " SECONDS!")
                            .setPosition(Notification.TOP_CENTER)
                            .show()).startCounting();
        });

        modal.appendChild(startRace);

        joinRace = Button.createSuccess(Icons.ALL.flag_checkered_mdi())
                .setContent("JOIN RACE!")
                .setSize(ButtonSize.LARGE)
                .style()
                .setMargin(px.of(5))
                .setMinWidth(px.of(200))
                .get();

        joinRace.addClickListener(evt -> eventBus.fireEvent(new JoinRaceEvent()));

        modal.appendChild(joinRace);

        withdrawFromRace = Button.createWarning(Icons.ALL.cancel_mdi())
                .setContent("WITHDRAW")
                .setSize(ButtonSize.LARGE)
                .style()
                .setMargin(px.of(5))
                .setMinWidth(px.of(200))
                .get();

        withdrawFromRace.addClickListener(evt -> eventBus.fireEvent(new WithdrawFromRaceEvent()));

        modal.appendChild(withdrawFromRace);

        Button closeButton = Button.create("CANCEL RACE").addClickListener(evt -> modal.close()).linkify();

        modal.appendFooterChild(closeButton);
        return modal;
    }

    private void reset() {

    }

    public void show() {
        reset();
        modal.open();
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onRaceEvent(final RaceEvent event) {
        GWT.log("ProblemsRaceView: handle RaceEvent");

        RaceDetails raceDetails = event.getRaceDetails();

        if (raceDetails.getRaceStatus() != RaceDetails.RaceStatus.PRE_RACE) {
            modal.close();
        }

        String username = sessionInformation.getUsername();

        boolean isRaceOwner = Objects.equals(username, raceDetails.getOwner());
        boolean isJoined = Arrays.asList(raceDetails.getPlayers()).contains(username);

        GWT.log("is race owner: " + isRaceOwner);

        if (isRaceOwner) {
            startRace.show();
        } else {
            startRace.hide();
        }

        if (isJoined) {
            joinRace.hide();
            withdrawFromRace.show();
        } else {
            joinRace.show();
            withdrawFromRace.hide();
        }

        participantsCard.clearBody();
        for (String player : raceDetails.getPlayers()) {
            participantsCard.appendChild(Chip.create().setValue(player).setLeftIcon(Icons.ALL.car_sports_mdi()));
        }

    }

}
