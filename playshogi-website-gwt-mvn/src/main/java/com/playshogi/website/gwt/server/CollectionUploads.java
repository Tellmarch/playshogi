package com.playshogi.website.gwt.server;

import com.playshogi.library.models.record.GameCollection;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum CollectionUploads {
    INSTANCE;

    private final Map<String, GameCollection> collections = new ConcurrentHashMap<>();

    public String addCollection(final GameCollection collection) {
        String id = UUID.randomUUID().toString();

        collections.put(id, collection);
        return id;
    }

    public GameCollection getCollection(final String draftCollectionId) {
        return collections.get(draftCollectionId);
    }

    public Map<String, GameCollection> getCollections() {
        return collections;
    }
}
