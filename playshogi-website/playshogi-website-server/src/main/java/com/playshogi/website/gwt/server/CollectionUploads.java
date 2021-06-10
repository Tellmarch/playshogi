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

    public void addOrMergeCollection(final String id, final KifuCollection collection) {
        if (collections.containsKey(id)) {
            collections.get(id).merge(collection);
        } else {
            collections.put(id, collection);
        }
    }

    public KifuCollection getCollection(final String draftCollectionId) {
        return collections.get(draftCollectionId);
    }

    public Map<String, KifuCollection> getCollections() {
        return collections;
    }
}
