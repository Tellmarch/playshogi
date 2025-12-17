package com.playshogi.website.gwt.server.services;


import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.playshogi.library.database.DbConnection;
import com.playshogi.library.database.KifuRepository;
import com.playshogi.library.database.LessonRepository;
import com.playshogi.library.database.models.*;
import com.playshogi.library.shogi.models.record.GameRecord;
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
    private final DbConnection dbConnection = new DbConnection();
    private final LessonRepository lessonRepository = new LessonRepository(dbConnection);
    private final KifuRepository kifuRepository = new KifuRepository(dbConnection);
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

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can create a lesson");
        }

        authenticator.validateAdminSession(sessionId);

        return lessonRepository.saveLesson(getPersistentLesson(loginResult.getUserId(), lesson));
    }

    public void updateLesson(final String sessionId, final LessonDetails lesson) {
        LOGGER.log(Level.INFO, "updateLesson: " + lesson);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can update a lesson");
        }

        authenticator.validateAdminSession(sessionId);

        lessonRepository.updateLesson(getPersistentLesson(loginResult.getUserId(), lesson));
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

    public void updateCampaignNode(final String sessionId, final String campaignId, final CampaignLessonNode node) {
        LOGGER.log(Level.INFO, "updateCampaignNode: " + node);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can delete a lesson in campaign");
        }

        if (!lessonRepository.updateCampaignLesson(getPersistentCampaignLesson(Integer.parseInt(campaignId), node))) {
            throw new IllegalArgumentException("Could not update the lesson in campaign");
        }

        lessonRepository.updateLessonPrerequisites(Integer.parseInt(campaignId), Integer.parseInt(node.getLessonId()),
                node.getPrerequisites().stream().map(Integer::parseInt).collect(Collectors.toList()));
    }

    public void setPrerequisites(final String sessionId, final String campaignId, final String lessonId,
                                 List<String> prereqs) {
        //TODO auth
        lessonRepository.updateLessonPrerequisites(Integer.parseInt(campaignId), Integer.parseInt(lessonId),
                prereqs.stream().map(Integer::parseInt).collect(Collectors.toList()));
    }

    public void addChapter(final String sessionId, final LessonChapterDto chapterDto) {
        LOGGER.log(Level.INFO, "addChapter for lesson: " + chapterDto.getLessonId());

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can add a chapter");
        }

        // Authorization Check: Check if the current user is the author of the lesson
        if (!lessonRepository.isLessonAuthor(Integer.parseInt(chapterDto.getLessonId()), loginResult.getUserId())) {
            throw new IllegalStateException("User " + loginResult.getUserId() + " is not authorized to modify lesson "
                    + chapterDto.getLessonId());
        }

        String name = "Lesson " + chapterDto.getLessonId() + " Chapter " + chapterDto.getTitle();
        String truncatedName = name.length() <= 255 ? name : name.substring(0, 255);
        int kifuId = kifuRepository.saveKifu(new GameRecord(), truncatedName, loginResult.getUserId(),
                PersistentKifu.KifuType.LESSON);

        if (!lessonRepository.addChapter(
                Integer.parseInt(chapterDto.getLessonId()),
                kifuId,
                chapterDto.getType(),
                chapterDto.getTitle(),
                chapterDto.getOrientation(),
                chapterDto.isHidden())) {

            throw new IllegalArgumentException("Could not add the chapter to lesson " + chapterDto.getLessonId() + "." +
                    " Check for duplicate chapter number.");
        }
    }

    public void modifyChapter(final String sessionId, final LessonChapterDto chapterDto) {
        LOGGER.log(Level.INFO, "modifyChapter: " + chapterDto.getChapterId());

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can modify a chapter");
        }

        int chapterId = Integer.parseInt(chapterDto.getChapterId());
        int userId = loginResult.getUserId();

        // The lessonRepository.updateChapter method already performs the authorization check
        // (by joining ps_lesson_chapter and ps_lessons using userId).

        if (!lessonRepository.updateChapter(
                chapterId,
                userId, // Passed for authorization check within the repository
                Integer.parseInt(chapterDto.getKifuId()),
                chapterDto.getType(),
                chapterDto.getTitle(),
                chapterDto.getOrientation(),
                chapterDto.isHidden())) {

            // This exception covers both "Not authorized" and "Chapter ID not found/Duplicate chapter number"
            throw new IllegalArgumentException("Could not update chapter " + chapterId + ". Verify authorization or " +
                    "check for duplicate chapter number.");
        }
    }

    public void deleteChapter(final String sessionId, final String chapterId) {
        LOGGER.log(Level.INFO, "deleteChapter: " + chapterId);

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can delete a chapter");
        }

        int userId = loginResult.getUserId();

        // The lessonRepository.deleteChapter method performs the authorization check
        // (by joining ps_lesson_chapter and ps_lessons using userId).

        if (!lessonRepository.deleteChapter(Integer.parseInt(chapterId), userId)) {

            // This exception covers both "Not authorized" and "Chapter ID not found"
            throw new IllegalArgumentException("Could not delete chapter " + chapterId + ". Verify authorization.");
        }
    }

    //TODO: public vs hidden chapters
    public List<LessonChapterDto> getChaptersForLesson(final String sessionId, final String lessonIdString) {

        LoginResult loginResult = authenticator.checkSession(sessionId);

        try {
            int lessonId = Integer.parseInt(lessonIdString);

            List<LessonChapterDto> chapters = lessonRepository.listLessonChapters(lessonId);

            if (loginResult == null || !loginResult.isAdmin()) {
                chapters = chapters.stream()
                        .filter(c -> !c.isHidden())
                        .collect(Collectors.toList());
            }

            return chapters;

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid lesson ID format: " + lessonIdString);
            throw new IllegalArgumentException("Invalid lesson ID format.");
        }
    }

    public void swapChapterOrder(final String sessionId, final String chapterIdString1, final String chapterIdString2) {
        LOGGER.log(Level.INFO, "swapChapterOrder between {0} and {1}", new Object[]{chapterIdString1,
                chapterIdString2});

        LoginResult loginResult = authenticator.checkSession(sessionId);
        if (loginResult == null || !loginResult.isLoggedIn()) {
            throw new IllegalStateException("Only logged in users can swap chapters.");
        }

        try {
            int chapterId1 = Integer.parseInt(chapterIdString1);
            int chapterId2 = Integer.parseInt(chapterIdString2);
            int userId = loginResult.getUserId();

            if (chapterId1 == chapterId2) {
                // Cannot swap a chapter with itself
                throw new IllegalArgumentException("Chapter IDs must be different for a swap.");
            }

            // The repository method performs authorization check, same-lesson check, and the swap within a transaction.
            if (!lessonRepository.swapChapterOrder(chapterId1, chapterId2, userId)) {
                throw new IllegalArgumentException("Could not swap chapters. Verify that both chapter IDs exist, " +
                        "belong to the same lesson, and the user is the lesson author.");
            }

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid chapter ID format.");
            throw new IllegalArgumentException("Invalid chapter ID format.");
        }
    }

    private PersistentCampaignLesson getPersistentCampaignLesson(int campaignId, final CampaignLessonNode node) {
        return new PersistentCampaignLesson(campaignId, Integer.parseInt(node.getLessonId()), node.getX(),
                node.getY(), false, false, false,
                false);
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

    private PersistentLesson getPersistentLesson(final int userId, final LessonDetails details) {
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
                userId,
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