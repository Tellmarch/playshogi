package com.playshogi.library.database;

import org.junit.Test;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class GameSetRepositoryTest {

    @Test
    public void parseDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        assertEquals("2021/06/15",
                simpleDateFormat.format(GameSetRepository.parseDate("2021-06-15T01:04:00.000Z"))); // db
        assertEquals("2021/06/15",
                simpleDateFormat.format(GameSetRepository.parseDate("2021/06/15"))); // wiki; 81
        assertEquals("2021/05/21",
                simpleDateFormat.format(GameSetRepository.parseDate("2021/05/21 19:52:07"))); // SC24
        assertEquals("1999/06/19",
                simpleDateFormat.format(GameSetRepository.parseDate("19990619"))); // OLD PSN
        assertEquals("1801/06/15",
                simpleDateFormat.format(GameSetRepository.parseDate("1801/06/15"))); // old date
        assertEquals("2021/03/02",
                simpleDateFormat.format(GameSetRepository.parseDate("02/03/2021"))); // old date
        assertEquals("2020/08/31",
                simpleDateFormat.format(GameSetRepository.parseDate("Aug 31, 2020 6:39:53 PM"))); // old SC24
        assertEquals("2013/07/19",
                simpleDateFormat.format(GameSetRepository.parseDate("19/07/2013"))); // old date
    }
}