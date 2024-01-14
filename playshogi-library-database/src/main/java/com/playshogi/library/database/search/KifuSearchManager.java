package com.playshogi.library.database.search;

import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.GameRepository;
import com.playshogi.library.database.KifuRepository;
import com.playshogi.library.database.models.PersistentGame;
import com.playshogi.library.database.models.PersistentKifu;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ReadOnlyShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.library.shogi.models.record.GameNavigation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KifuSearchManager {

    private static final Logger LOGGER = Logger.getLogger(KifuSearchManager.class.getName());


    private final GameRepository gameRepository = new GameRepository(new DbConnection());
    private final KifuRepository kifuRepository = new KifuRepository(new DbConnection());

    private final List<PersistentKifu> kifus = new ArrayList<>();

    public KifuSearchManager() {
        System.out.println("Loading games from db...");
        List<PersistentGame> games = gameRepository.getGamesFromGameSet(1, true);
        for (PersistentGame game : games) {
            if (game.getKifuId() % 1000 == 0) System.out.println("Loading #" + game.getKifuId());
            try {
                kifus.add(kifuRepository.getKifuById(game.getKifuId(), true));
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error loading the kifu " + game.getKifuId(), ex);
            }
        }
        System.out.println("Loaded games from db!");
    }

    public List<KifuSearchResult> searchGames(final KifuSearchFilter filter) {
        List<KifuSearchResult> result = new ArrayList<>();
        for (PersistentKifu persistentKifu : kifus) {
            GameNavigation navigation = new GameNavigation(persistentKifu.getKifu().getGameTree());
            int movec = 0;
            while (navigation.canMoveForward() && movec++ < 40) {
                navigation.moveForward();
                if (positionContains(navigation.getPosition(), filter.getPartialPositionSearch())) {
                    System.out.println(persistentKifu.getKifu().getGameInformation() + " - In kifu #" + persistentKifu.getId() + ":");
                    System.out.println(navigation.getPosition());

                    result.add(new KifuSearchResult(persistentKifu, navigation.getPosition()));
                    break;
                }
            }
        }

        return result;
    }

    private boolean positionContains(final ReadOnlyShogiPosition fullPosition,
                                     final ReadOnlyShogiPosition partialPosition) {
        for (Square square : partialPosition.getAllSquares()) {
            if (partialPosition.isEmptySquare(square)) continue;
            if (fullPosition.isEmptySquare(square)) return false;
            if (partialPosition.getPieceAt(square).get() != fullPosition.getPieceAt(square).get()) return false;
        }

        //TODO komadai?
        return true;
    }

    public static void main(String[] args) {
        KifuSearchManager manager = new KifuSearchManager();
        manager.searchGames(new KifuSearchFilter(SfenConverter.fromSFEN("9/9/9/9/9/9/9/6R2/9 b kr2b4g4s4n4l18p")));
    }
}
