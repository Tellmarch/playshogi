package com.playshogi.library.shogi.engine;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

@Ignore
public class QueuedComputerPlayTest {

    @Test
    public void playMove() {
        QueuedComputerPlay computerPlay = new QueuedComputerPlay(EngineConfiguration.NORMAL_ENGINE);
        String move = computerPlay.playMove("lnsgk1snl/6gb1/p1pppp2p/6pR1/9/1r7/P1PPPPP1P/1BG2S3/LNS1KG1NL b 2P2p");
        System.out.println(move);
        assertNotNull(move);
    }
}