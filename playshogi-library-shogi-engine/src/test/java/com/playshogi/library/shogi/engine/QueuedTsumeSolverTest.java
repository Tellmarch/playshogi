package com.playshogi.library.shogi.engine;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

@Ignore
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
}