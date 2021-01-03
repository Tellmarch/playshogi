package com.playshogi.library.database;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class KifuRepositoryTest {

    private KifuRepository kifuRepository;

    @Before
    public void setup() {
        kifuRepository = new KifuRepository(new DbConnection());
    }

    @Test
    public void getUserKifus() {
        System.out.println(kifuRepository.getUserKifus(1));
    }
}