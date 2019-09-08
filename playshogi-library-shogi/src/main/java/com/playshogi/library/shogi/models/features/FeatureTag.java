package com.playshogi.library.shogi.models.features;

public enum FeatureTag {
    GOLD_AT_HEAD(1, GoldAtHeadFeature.INSTANCE);

    private int dbIndex;
    private Feature feature;

    FeatureTag(int dbIndex, Feature feature) {
        this.dbIndex = dbIndex;
        this.feature = feature;
    }

    public int getDbIndex() {
        return dbIndex;
    }

    public Feature getFeature() {
        return feature;
    }
}
