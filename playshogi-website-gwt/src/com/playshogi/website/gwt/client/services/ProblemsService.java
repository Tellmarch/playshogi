package com.playshogi.website.gwt.client.services;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("problems")
public interface ProblemsService {

	String getProblemUsf(String problemId);

}
