package com.playshogi.website.gwt.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("computer")
public interface ComputerService extends RemoteService {

    String getComputerMove(String sessionId, String sfen);

}
