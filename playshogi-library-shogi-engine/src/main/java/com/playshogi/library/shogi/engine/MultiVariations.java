package com.playshogi.library.shogi.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiVariations {
    private final List<Variation> variations;

    public MultiVariations(final List<Variation> variations) {
        this.variations = variations;
    }

    public MultiVariations(final Variation principalVariation) {
        this.variations = Collections.singletonList(principalVariation);
    }

    public Variation getMainVariation() {
        return variations.get(0);
    }

    public Variation getSecondaryVariation(final int index) {
        return variations.get(index);
    }

    public List<Variation> getVariations() {
        return new ArrayList<>(variations);
    }

    @Override
    public String toString() {
        return "MultiVariations{" +
                "variations=" + variations +
                '}';
    }
}
