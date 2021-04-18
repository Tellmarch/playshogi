package com.playshogi.website.gwt.client.events.kifu;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class EditModeSelectedEvent extends GenericEvent {
    private final boolean editMode;

    public EditModeSelectedEvent(final boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isEditMode() {
        return editMode;
    }
}
