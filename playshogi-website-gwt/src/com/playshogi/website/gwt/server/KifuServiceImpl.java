package com.playshogi.website.gwt.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.playshogi.library.models.record.GameInformation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.services.KifuService;

public class KifuServiceImpl implements KifuService {

	private static final Logger LOGGER = Logger.getLogger(KifuServiceImpl.class.getName());

	private final Map<String, GameRecord> gameRecords = new ConcurrentHashMap<>();

	@Override
	public String saveKifu(final String sessionId, final String kifuUsf) {
		LOGGER.log(Level.INFO, "saving kifu:\n" + kifuUsf);
		GameRecord gameRecord = UsfFormat.INSTANCE.read(kifuUsf);
		String id = UUID.randomUUID().toString();
		gameRecords.put(id, gameRecord);
		return id;
	}

	@Override
	public String getKifuUsf(final String sessionId, final String kifuId) {
		LOGGER.log(Level.INFO, "querying kifu:\n" + kifuId);
		GameRecord gameRecord = gameRecords.get(kifuId);
		if (gameRecord == null) {
			LOGGER.log(Level.INFO, "invalid kifu id:\n" + kifuId);
			return null;
		} else {
			return UsfFormat.INSTANCE.write(gameRecord);
		}
	}

	@Override
	public KifuDetails[] getAvailableKifuDetails(final String sessionId) {
		List<KifuDetails> result = new ArrayList<>(gameRecords.size());
		for (Entry<String, GameRecord> entry : gameRecords.entrySet()) {
			result.add(createKifuDetails(entry.getKey(), entry.getValue()));
		}
		return result.toArray(new KifuDetails[result.size()]);
	}

	private KifuDetails createKifuDetails(final String key, final GameRecord value) {
		GameInformation gameInformation = value.getGameInformation();

		KifuDetails kifuDetails = new KifuDetails();
		kifuDetails.setId(key);
		kifuDetails.setSente(gameInformation.getSente());
		kifuDetails.setGote(gameInformation.getGote());
		kifuDetails.setVenue(gameInformation.getVenue());
		kifuDetails.setDate(gameInformation.getDate());
		return kifuDetails;
	}
}
