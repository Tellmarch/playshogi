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
import java.util.stream.Collectors;

public class LessonServiceImpl {

    private static final Logger LOGGER = Logger.getLogger(LessonServiceImpl.class.getName());
    private final LessonRepository lessonRepository = new LessonRepository(new DbConnection());
    private final Authenticator authenticator = Authenticator.INSTANCE;

    // -------------------------
    // Campaign graph
    // -------------------------

    public CampaignGraph getCampaign(final String sessionId, final String campaignId) {
        LOGGER.log(Level.INFO, "getCampaign: " + campaignId);
        return lessonRepository.getFullCampaignGraph(Integer.parseInt(campaignId));
    }

    // -------------------------
    // Lesson CRUD
    // -------------------------

    public LessonDetails[] getAllLessons(final String sessionId) {
        LOGGER.log(Level.INFO, "getAllLessons");

        authenticator.validateAdminSession(sessionId);

        List<PersistentLesson> allVisibleLessons = lessonRepository.getAllLessons();
        return allVisibleLessons.stream().map(this::getLessonDetails).toArray(LessonDetails[]::new);
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

    public void addLessonToCampaign(final String sessionId, final String campaignId, final String lessonId, int x,
                                    int y) {
        LOGGER.log(Level.INFO, "addLessonToCampaign: " + campaignId + ":" + lessonId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can add a lesson in campaign");
        }
        //TODO: auth

        if (!lessonRepository.addLessonToCampaign(new PersistentCampaignLesson(Integer.parseInt(campaignId),
                Integer.parseInt(lessonId), x, y, false,
                false, false, false))) {
            throw new IllegalStateException("Failed to add the lesson to the campaign");
        }
    }

    public void deleteCampaignLesson(final String sessionId, final String campaignId, final String lessonId) {
        LOGGER.log(Level.INFO, "deleteCampaignLesson: " + campaignId + ":" + lessonId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can delete a lesson in campaign");
        }

        if (!lessonRepository.deleteCampaignLesson(Integer.parseInt(campaignId), Integer.parseInt(lessonId),
                loginResult.getUserId())) {
            throw new IllegalArgumentException("Could not delete the lesson in campaign");
        }
    }

    public void setPrerequisites(final String sessionId, final String campaignId, final String lessonId,
                                 List<String> prereqs) {
        //TODO auth
        lessonRepository.updateLessonPrerequisites(Integer.parseInt(campaignId), Integer.parseInt(lessonId),
                prereqs.stream().map(Integer::parseInt).collect(Collectors.toList()));
    }

    private LessonDetails getLessonDetails(final PersistentLesson lesson) {
        LessonDetails details = new LessonDetails();
        details.setLessonId(String.valueOf(lesson.getId()));
        details.setIndex(lesson.getIndex());
        details.setTitle(lesson.getTitle());
        details.setDescription(lesson.getDescription());
        details.setKifuId(lesson.getKifuId() == null ? null : String.valueOf(lesson.getKifuId()));
        details.setProblemCollectionId(lesson.getProblemCollectionId() == null ? null :
                String.valueOf(lesson.getProblemCollectionId()));
        details.setDifficulty(lesson.getDifficulty());
        details.setTags(lesson.getTags());
        details.setPreviewSfen(lesson.getPreviewSfen());
        details.setHidden(lesson.isHidden());
        details.setLikes(lesson.getLikes());
        details.setAuthor(lesson.getAuthorId() == null ? null : UsersCache.INSTANCE.getUserName(lesson.getAuthorId()));
        details.setParentLessonId(lesson.getParentId() == null ? null : String.valueOf(lesson.getParentId()));
        return details;
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