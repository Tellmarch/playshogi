package com.playshogi.website.gwt.client.widget.problems;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
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


    public ByoYomiProgressPanel() {
        flowPanel = new FlowPanel();
        initWidget(flowPanel);
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


    public void activate(final EventBus eventBus) {
        GWT.log("Activating ByoYomi progress panel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        flowPanel.clear();
    }
}
