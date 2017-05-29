package com.playshogi.website.gwt.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.GameSetRepository;
import com.playshogi.library.database.PositionRepository;
import com.playshogi.library.database.models.PersistentGameSetMove;
import com.playshogi.library.database.models.PersistentGameSetPos;
import com.playshogi.library.models.record.GameInformation;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.models.PositionDetails;
import com.playshogi.website.gwt.shared.models.PositionMoveDetails;
import com.playshogi.website.gwt.shared.services.KifuService;

public class KifuServiceImpl extends RemoteServiceServlet implements KifuService {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(KifuServiceImpl.class.getName());

	private final Map<String, GameRecord> gameRecords = new ConcurrentHashMap<>();

	private final GameSetRepository gameSetRepository;
	private final PositionRepository positionRepository;

	public KifuServiceImpl() {
		DbConnection dbConnection = new DbConnection();
		gameSetRepository = new GameSetRepository(dbConnection);
		positionRepository = new PositionRepository(dbConnection);
	}

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

	@Override
	public PositionDetails getPositionDetails(final String sfen, final int gameSetId) {
		LOGGER.log(Level.INFO, "querying position details:\n" + sfen + " " + gameSetId);
		// TODO validate permissions

		int positionId = positionRepository.getPositionIdBySfen(sfen);

		if (positionId == -1) {
			LOGGER.log(Level.INFO, "position not in database: \n" + sfen);
			return null;
		}

		PersistentGameSetPos stats = gameSetRepository.getGameSetPositionStats(positionId, gameSetId);
		List<PersistentGameSetMove> moveStats = gameSetRepository.getGameSetPositionMoveStats(positionId, gameSetId);

		PositionMoveDetails[] details = new PositionMoveDetails[moveStats.size()];
		for (int i = 0; i < details.length; i++) {
			PersistentGameSetMove move = moveStats.get(i);
			String newSfen = positionRepository.getPositionSfenById(move.getNewPositionId());
			details[i] = new PositionMoveDetails(move.getMoveUsf(), move.getMoveOccurrences(), move.getSenteWins(), move.getGoteWins(), newSfen);
		}

		return new PositionDetails(stats.getTotal(), stats.getSenteWins(), stats.getGoteWins(), details);
	}
}
