package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserServiceAsync {

    void saveLessonProgress(String sessionId, String lessonId, int timeMs, boolean complete, int percentage,
                            Integer rating, AsyncCallback<Void> callback);

}
