package com.playshogi.website.gwt.client.events.gametree;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.PrincipalVariationDetails;

public class InsertVariationEvent extends GenericEvent {
    private final PrincipalVariationDetails selectedVariation;

    public InsertVariationEvent(final PrincipalVariationDetails selectedVariation) {
        this.selectedVariation = selectedVariation;
    }

    public PrincipalVariationDetails getSelectedVariation() {
        return selectedVariation;
    }

    @Override
    public String toString() {
        return "InsertVariationEvent{" +
                "selectedVariation=" + selectedVariation +
                '}';
    }
}
