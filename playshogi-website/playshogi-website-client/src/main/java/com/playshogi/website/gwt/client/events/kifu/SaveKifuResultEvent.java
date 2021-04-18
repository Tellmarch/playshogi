package com.playshogi.website.gwt.client.events.kifu;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class SaveKifuResultEvent extends GenericEvent {

    private final boolean success;
    private final String kifuId;

    public SaveKifuResultEvent(final boolean success, final String kifuId) {
        this.success = success;
        this.kifuId = kifuId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getKifuId() {
        return kifuId;
    }
}
