package com.playshogi.website.gwt.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.playshogi.website.gwt.shared.Version;
import com.playshogi.website.gwt.shared.services.LoginService;
import com.playshogi.website.gwt.shared.services.LoginServiceAsync;

public class VersionInformation {

    private final LoginServiceAsync loginService = GWT.create(LoginService.class);

    public void checkVersion() {
        loginService.getVersion(new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable throwable) {
                GWT.log("ERROR validating version", throwable);
            }

            @Override
            public void onSuccess(String version) {
                if (Version.VERSION.equals(version)) {
                    GWT.log("Correct version: " + version);
                } else {
                    GWT.log("Incorrect version: " + version + " != " + Version.VERSION);
                    Window.alert(" A new version of the website is available: " + version + ". You are currently " +
                            "running version " + Version.VERSION + ". Please refresh the page with CTRL + F5.");
                }
            }
        });
    }
}
