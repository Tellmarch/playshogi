package com.playshogi.website.gwt.client.util;

import com.google.gwt.core.client.GWT;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;

import java.util.ArrayList;
import java.util.List;

public enum MyChartLoader {
    INSTANCE;

    private boolean initialized = false;
    private boolean initializing = false;

    private final List<Runnable> onLoadCallbacks = new ArrayList<>();

    public void initialize() {
        if (initialized) {
            GWT.log("***** CHARTS LIBRARY ALREADY INITIALIZED");
            return;
        }
        if (initializing) {
            GWT.log("***** CHARTS LIBRARY ALREADY INITIALIZING");
            return;
        }
        initializing = true;
        GWT.log("***** INITIALIZING CHARTS LIBRARY");
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
        chartLoader.loadApi(() -> {
                    GWT.log("***** CHARTS LIBRARY INITIALIZED");
                    initialized = true;
                    for (Runnable onLoadCallback : onLoadCallbacks) {
                        onLoadCallback.run();
                    }
                }
        );
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void runWhenReady(final Runnable runnable) {
        if (initialized) {
            runnable.run();
        } else {
            onLoadCallbacks.add(runnable);
            if (!initializing) initialize();
        }
    }
}
