package com.playshogi.website.gwt.client.events.util;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.Event.Type;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class LoggingEventBus extends SimpleEventBus {

    @Override
    public <H> HandlerRegistration addHandler(final Type<H> type, final H handler) {
        GWT.log("addHandler: " + handler.getClass().getCanonicalName());
        return wrap(super.addHandler(type, handler));
    }

    @Override
    public <H> HandlerRegistration addHandlerToSource(final Type<H> type, final Object source, final H handler) {
        GWT.log("addHandlerToSource: " + handler.getClass().getSimpleName());
        return wrap(super.addHandlerToSource(type, source, handler));
    }

    @Override
    public void fireEvent(final Event<?> event) {
        GWT.log("firingevent: " + event.toDebugString());
        //noinspection deprecation
        GWT.log("#handlers: " + getHandlerCount(event.getAssociatedType()));
        super.fireEvent(event);
    }

    @Override
    public void fireEventFromSource(final Event<?> event, final Object source) {
        GWT.log("fireEventFromSource: " + event.toDebugString());
        super.fireEventFromSource(event, source);
    }

    private HandlerRegistration wrap(final HandlerRegistration registration) {
        return () -> {
            GWT.log("Removing handler");
            registration.removeHandler();
        };
    }
}
