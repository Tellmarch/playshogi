package com.playshogi.library.shogi.engine;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class QueuedTsumeSolverTest {

    @Test
    public void analyseTsume() throws InterruptedException {
        final QueuedTsumeSolver queuedTsumeSolver = new QueuedTsumeSolver(EngineConfiguration.TSUME_ENGINE);
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                System.out.println("Try solving tsume #" + finalI);
                System.out.println(queuedTsumeSolver.analyseTsume("2p6/2+b+B1k3/3PPp3/9/1p7/9/9/9/9 b 2RP4g4s4n4l12p"));
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        queuedTsumeSolver.shutDown();
    }

    @Test
    public void yaneuraOuBug() {
        final QueuedTsumeSolver queuedTsumeSolver = new QueuedTsumeSolver(EngineConfiguration.TSUME_ENGINE);
        PositionEvaluation evaluation = queuedTsumeSolver.analyseTsume("4k4/9/4P4/8r/9/9/9/9/9 b " +
                "Gr2b3g4s4n4l17p");
        assertEquals("G*5b", evaluation.getBestMove());
    }
}