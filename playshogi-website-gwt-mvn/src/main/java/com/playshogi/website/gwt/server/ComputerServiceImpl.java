package com.playshogi.website.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.shogi.engine.EngineConfiguration;
import com.playshogi.library.shogi.engine.QueuedComputerPlay;
import com.playshogi.website.gwt.shared.services.ComputerService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ComputerServiceImpl extends RemoteServiceServlet implements ComputerService {

    private static final Logger LOGGER = Logger.getLogger(ComputerServiceImpl.class.getName());

    private final QueuedComputerPlay queuedComputerPlay = new QueuedComputerPlay(EngineConfiguration.NORMAL_ENGINE);

    @Override
    public String getComputerMove(final String sessionId, final String sfen) {
        LOGGER.log(Level.INFO, "Requesting computer move:\n" + sfen);
        return queuedComputerPlay.playMove(sfen);
    }
}
