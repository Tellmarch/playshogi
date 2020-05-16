package com.playshogi.website.gwt.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FireAndForgetCallback implements AsyncCallback<Void> {

    private final String name;

    public FireAndForgetCallback(String name) {
        this.name = name;
        GWT.log("Starting RPC " + name + ".");
    }

    public FireAndForgetCallback() {
        this("RPC");
    }

    @Override
    public void onFailure(Throwable throwable) {
        GWT.log("RPC " + name + " failed.");
    }

    @Override
    public void onSuccess(Void aVoid) {
        GWT.log("RPC " + name + " succeeded.");
    }
}
