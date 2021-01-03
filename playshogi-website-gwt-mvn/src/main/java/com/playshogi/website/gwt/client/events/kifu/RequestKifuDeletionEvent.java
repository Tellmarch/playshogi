package com.playshogi.website.gwt.client.events.kifu;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class RequestKifuDeletionEvent extends GenericEvent {
    private String kifuId;

    public RequestKifuDeletionEvent(final String kifuId) {
        this.kifuId = kifuId;
    }

    public String getKifuId() {
        return kifuId;
    }
}
