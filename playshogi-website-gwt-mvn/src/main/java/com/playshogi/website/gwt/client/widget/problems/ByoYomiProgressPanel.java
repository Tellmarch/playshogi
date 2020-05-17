package com.playshogi.website.gwt.client.widget.problems;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.ActivityTimerEvent;
import com.playshogi.website.gwt.client.events.UserFinishedProblemEvent;

public class ByoYomiProgressPanel extends Composite {


    interface MyEventBinder extends EventBinder<ByoYomiProgressPanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    interface Resources extends ClientBundle {
        @Source("com/playshogi/website/gwt/resources/icons/wrong_small.png")
        ImageResource wrongIcon();

        @Source("com/playshogi/website/gwt/resources/icons/right_small.png")
        ImageResource rightIcon();
    }

    private final Resources resources = GWT.create(Resources.class);

    private EventBus eventBus;

    private final FlowPanel flowPanel;
    private final HTML timerHTML;

    public ByoYomiProgressPanel() {
        flowPanel = new FlowPanel();

        VerticalPanel verticalPanel = new VerticalPanel();
        timerHTML = new HTML(getTimeHTML(300));
        verticalPanel.add(timerHTML);
        verticalPanel.add(flowPanel);

        initWidget(verticalPanel);
    }

    private String getTimeHTML(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        String minutesStr = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        String secondsStr = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
        return "<p style=\"font-size:20px;color:blue\"><b>" + minutesStr + ":" + secondsStr + "</b></p>";
    }

    @EventHandler
    void onUserFinishedProblemEvent(final UserFinishedProblemEvent event) {
        GWT.log("ByoYomi progress: Finished problem. Success: " + event.isSuccess());
        if (event.isSuccess()) {
            flowPanel.add(new Image(resources.rightIcon()));
        } else {
            flowPanel.add(new Image(resources.wrongIcon()));
        }
    }

    @EventHandler
    void onActivityTimerEvent(final ActivityTimerEvent event) {
        GWT.log("ByoYomi progress: timer event " + event.getTimems());
        int timeInSeconds = event.getTimems() / 1000;
        timerHTML.setHTML(SafeHtmlUtils.fromTrustedString(getTimeHTML(timeInSeconds)));
    }


    public void activate(final EventBus eventBus) {
        GWT.log("Activating ByoYomi progress panel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        flowPanel.clear();
    }
}
