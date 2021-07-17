package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("user")
public interface UserService extends RemoteService {

    void saveLessonProgress(String sessionId, String lessonId, int timeMs, boolean complete, int percentage,
                            Integer rating);

}
