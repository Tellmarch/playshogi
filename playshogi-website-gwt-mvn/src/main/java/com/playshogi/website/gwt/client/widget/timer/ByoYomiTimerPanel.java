package com.playshogi.website.gwt.client.widget.timer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.MoveTimerEvent;

public class ByoYomiTimerPanel extends Composite {

    interface MyEventBinder extends EventBinder<ByoYomiTimerPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private EventBus eventBus;
    private final HTML timerHTML;

    public ByoYomiTimerPanel() {
        timerHTML = new HTML(getTimeHTML(30));
        timerHTML.getElement().getStyle().setBackgroundColor("yellow");
        initWidget(timerHTML);
    }

    @EventHandler
    void onMoveTimerEvent(final MoveTimerEvent event) {
        GWT.log("ByoYomi timer panel: timer event " + event.getTimeMs());
        int timeInSeconds = event.getTimeMs() / 1000;
        timerHTML.setHTML(SafeHtmlUtils.fromTrustedString(getTimeHTML(timeInSeconds)));
        if (timeInSeconds <= 10) {
            timerHTML.getElement().getStyle().setBackgroundColor("red");
        } else {
            timerHTML.getElement().getStyle().setBackgroundColor("yellow");
        }
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating Byo Yomi timer panel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    private String getTimeHTML(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        String minutesStr = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        String secondsStr = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
        return "<p style=\"font-size:20px;color:black\"><b>" + minutesStr + ":" + secondsStr + "</b></p>";
    }
}
