package com.playshogi.website.gwt.server;

import com.playshogi.library.shogi.models.record.KifuCollection;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum CollectionUploads {
    INSTANCE;

    private final Map<String, KifuCollection> collections = new ConcurrentHashMap<>();

    public String addCollection(final KifuCollection collection) {
        String id = UUID.randomUUID().toString();

        collections.put(id, collection);
        return id;
    }

    public KifuCollection getCollection(final String draftCollectionId) {
        return collections.get(draftCollectionId);
    }

    public Map<String, KifuCollection> getCollections() {
        return collections;
    }
}
