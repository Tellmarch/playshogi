package com.playshogi.website.gwt.server.services;


import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.LessonRepository;
import com.playshogi.library.database.models.CampaignGraph;
import com.playshogi.library.database.models.PersistentCampaignLesson;
import com.playshogi.library.database.models.PersistentLesson;
import com.playshogi.website.gwt.server.controllers.Authenticator;
import com.playshogi.website.gwt.server.controllers.UsersCache;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import com.playshogi.website.gwt.shared.models.LoginResult;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LessonServiceImpl {

    private static final Logger LOGGER = Logger.getLogger(LessonServiceImpl.class.getName());
    private final LessonRepository lessonRepository = new LessonRepository(new DbConnection());
    private final Authenticator authenticator = Authenticator.INSTANCE;

    // -------------------------
    // Campaign graph
    // -------------------------

    public CampaignGraph getCampaign(final String sessionId, int campaignId) {
        return lessonRepository.getFullCampaignGraph(campaignId);
    }

    // -------------------------
    // Lesson CRUD
    // -------------------------

    public List<PersistentLesson> getAllLessons(final String sessionId) {
        return lessonRepository.getAllLessons();
    }

    public int createLesson(final String sessionId, final LessonDetails lesson) {
        LOGGER.log(Level.INFO, "createLesson: " + lesson);

        authenticator.validateAdminSession(sessionId);

        return lessonRepository.saveLesson(getPersistentLesson(lesson));
    }

    public void updateLesson(final String sessionId, final LessonDetails lesson) {
        lessonRepository.updateLesson(getPersistentLesson(lesson));
    }

    // -------------------------
    // Modify campaign graph
    // -------------------------

    public boolean addLessonToCampaign(final String sessionId, int campaignId, int lessonId, int x, int y) {
        LOGGER.log(Level.INFO, "addLessonToCampaign: " + campaignId + ":" + lessonId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can add a lesson in campaign");
        }
        //TODO: auth

        return lessonRepository.addLessonToCampaign(new PersistentCampaignLesson(campaignId, lessonId, x, y, false,
                false, false, false));
    }

    public void deleteCampaignLesson(final String sessionId, int campaignId, int lessonId) {
        LOGGER.log(Level.INFO, "deleteCampaignLesson: " + campaignId + ":" + lessonId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can delete a lesson in campaign");
        }

        if (!lessonRepository.deleteCampaignLesson(campaignId, lessonId, loginResult.getUserId())) {
            throw new IllegalArgumentException("Could not delete the lesson in campaign");
        }
    }

    public void setPrerequisites(final String sessionId, int campaignId, int lessonId, List<Integer> prereqs) {
        //TODO auth
        lessonRepository.updateLessonPrerequisites(campaignId, lessonId, prereqs);
    }

    private PersistentLesson getPersistentLesson(final LessonDetails details) {
        return new PersistentLesson(
                Strings.isNullOrEmpty(details.getLessonId()) ? 0 : Integer.parseInt(details.getLessonId()),
                Strings.isNullOrEmpty(details.getKifuId()) ? null : Integer.parseInt(details.getKifuId()),
                Strings.isNullOrEmpty(details.getProblemCollectionId()) ? null :
                        Integer.parseInt(details.getProblemCollectionId()),
                Strings.isNullOrEmpty(details.getParentLessonId()) ? null :
                        Integer.parseInt(details.getParentLessonId()),
                details.getTitle(),
                details.getDescription(),
                details.getTags(),
                details.getPreviewSfen(),
                details.getDifficulty(),
                details.getLikes(),
                UsersCache.INSTANCE.getUserId(details.getAuthor()),
                details.isHidden(),
                null,
                null,
                details.getProblemCollectionId() != null ? PersistentLesson.LessonType.PRACTICE :
                        (details.getKifuId() != null ? PersistentLesson.LessonType.LECTURE :
                                PersistentLesson.LessonType.UNSPECIFIED),
                details.getIndex()
        );
    }
}