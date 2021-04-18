package com.playshogi.website.gwt.client.events.kifu;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.KifuDetails;

public class SaveKifuEvent extends GenericEvent {
    private final String kifuUsf;
    private final KifuDetails.KifuType kifuType;
    private final String name;

    public SaveKifuEvent(final String kifuUsf, final KifuDetails.KifuType kifuType, final String name) {
        this.kifuUsf = kifuUsf;
        this.kifuType = kifuType;
        this.name = name;
    }

    public String getKifuUsf() {
        return kifuUsf;
    }

    public KifuDetails.KifuType getKifuType() {
        return kifuType;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "SaveKifuEvent{" +
                "kifuUsf='" + kifuUsf + '\'' +
                ", kifuType=" + kifuType +
                ", name='" + name + '\'' +
                '}';
    }
}
