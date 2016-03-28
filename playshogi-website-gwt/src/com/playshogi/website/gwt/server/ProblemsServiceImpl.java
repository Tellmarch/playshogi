package com.playshogi.website.gwt.server;

import java.io.IOException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.files.GameRecordFileReader;
import com.playshogi.library.shogi.models.formats.kif.KifFormat;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.client.services.ProblemsService;

public class ProblemsServiceImpl extends RemoteServiceServlet implements ProblemsService {

	private static final long serialVersionUID = 1L;

	private static final String PATH = "/playshogi/tsume/7/";

	@Override
	public String getProblemUsf(final String problemId) {
		try {
			GameRecord gameRecord = GameRecordFileReader.read(KifFormat.INSTANCE,
					PATH + "tsume_07_" + problemId + ".kif");
			String tsume = UsfFormat.INSTANCE.write(gameRecord.getGameTree());
			System.out.println(tsume);
			return tsume;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(final String[] args) {
		for (int i = 100; i <= 900; i++) {
			// System.out.println("Reading problem " + i);
			new ProblemsServiceImpl().getProblemUsf("" + i);
		}
	}

}
