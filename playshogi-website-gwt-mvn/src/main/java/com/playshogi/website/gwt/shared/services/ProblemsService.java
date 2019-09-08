package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("problems")
public interface ProblemsService extends RemoteService {

    String getProblemUsf(String problemId);

    String getRandomProblemUsf();

}
