package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.tutorial.LessonsListEvent;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.ProblemsPlace;
import com.playshogi.website.gwt.client.place.ViewLessonPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.BoardPreview;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import elemental2.dom.*;
import jsinterop.base.Js;
import org.dominokit.domino.ui.alerts.Alert;
import org.dominokit.domino.ui.badges.Badge;
import org.dominokit.domino.ui.breadcrumbs.Breadcrumb;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.cards.Card;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.labels.Label;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.tree.Tree;
import org.dominokit.domino.ui.tree.TreeItem;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class LessonsView extends Composite {

    interface MyEventBinder extends EventBinder<LessonsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final Breadcrumb breadcrumb;
    private final Tree<LessonDetails> lessonsTree;
    private final Card previewCard;
    private final HtmlContentBuilder<HTMLElement> previewDescription;
    private final HtmlContentBuilder<HTMLElement> previewTags;
    private final HtmlContentBuilder<HTMLHeadingElement> difficulty;
    private final Node previewDiagram;
    private final HtmlContentBuilder<HTMLAnchorElement> openButton;
    private final BoardPreview boardPreview;
    private final Alert loginAlert;

    private final SessionInformation sessionInformation;
    private final AppPlaceHistoryMapper historyMapper;

    private TreeItem<LessonDetails> selectedLesson;
    private Map<String, TreeItem<LessonDetails>> treeItems;

    @Inject
    public LessonsView(final SessionInformation sessionInformation, final AppPlaceHistoryMapper historyMapper) {
        GWT.log("Creating lessons view");

        this.sessionInformation = sessionInformation;
        this.historyMapper = historyMapper;

        breadcrumb = Breadcrumb.create().setColor(Color.ORANGE);
        loginAlert = Alert.info().appendChild("Tip: register an account or login to keep track of your progress!")
                .dismissible();
        lessonsTree = createLessonsTree();

        previewDescription = Elements.span();
        previewTags = Elements.span();
        difficulty = Elements.h(4);
        boardPreview = new BoardPreview(SfenConverter.fromSFEN(SfenConverter.INITIAL_POSITION_SFEN), false,
                sessionInformation.getUserPreferences());
        previewDiagram = Js.uncheckedCast(boardPreview.getElement());
        openButton = Elements.a("#");
        openButton.add(Button.createSuccess(Icons.ALL.library_mdi()).setContent("Open Lesson!"));
        openButton.hidden(true);

        previewCard =
                Card.create("Select a Lesson on the left")
                        .appendChild(previewTags)
                        .appendChild(Elements.p())
                        .appendChild(difficulty)
                        .appendChild(Elements.p())
                        .appendChild(previewDescription)
                        .appendChild(Elements.p())
                        .appendChild(previewDiagram)
                        .appendChild(Elements.p())
                        .appendChild(openButton);


        HtmlContentBuilder<HTMLDivElement> root = Elements.div();
        root.add(breadcrumb);
        root.add(loginAlert);
        root.add(Row.create()
                .addColumn(Column.span3().appendChild(lessonsTree))
                .addColumn(Column.span9().appendChild(previewCard)));
        initWidget(new ElementWidget(root.element()));
    }

    private Tree<LessonDetails> createLessonsTree() {
        final Tree<LessonDetails> lessonsTree;
        lessonsTree = Tree.create("Lessons", null);
        lessonsTree.setAutoCollapse(false);
        lessonsTree.enableFolding();
        lessonsTree.enableSearch();
        lessonsTree.autoExpandFound();
        lessonsTree.style().setOverFlowY("auto").setHeight("calc(100vh - 80px)");
        return lessonsTree;
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating lessons view");
        selectedLesson = null;
        eventBinder.bindEventHandlers(this, eventBus);
        breadcrumb.removeAll();
        breadcrumb.appendChild(Icons.ALL.home(), "Lessons", e -> selectLesson(null));
        if (sessionInformation.isLoggedIn()) {
            loginAlert.hide();
        }
    }

    private void showLessonSelection() {
        if (sessionInformation.isLoggedIn()) {

            int total = 0;
            int completed = 0;

            for (TreeItem<LessonDetails> lesson : treeItems.values()) {
                if (lesson.getValue().getKifuId() != null || lesson.getValue().getProblemCollectionId() != null) {
                    total++;
                    if (lesson.getValue().isCompleted()) {
                        completed++;
                    }
                }
            }

            HtmlContentBuilder<HTMLUListElement> perDifficulty = Elements.ul();
            for (int difficulty = 1; difficulty <= 5; difficulty++) {
                int todo = 0;

                TreeItem<LessonDetails> suggested = null;

                for (TreeItem<LessonDetails> treeItem : treeItems.values()) {
                    LessonDetails lesson = treeItem.getValue();
                    if (lesson.getDifficulty() == difficulty
                            && (lesson.getKifuId() != null || lesson.getProblemCollectionId() != null)
                            && !lesson.isCompleted()) {
                        todo++;
                        if (suggested == null) {
                            suggested = treeItem;
                        }
                    }
                }

                if (todo > 0) {
                    HtmlContentBuilder<HTMLDivElement> div = Elements.div();
                    for (int i = 1; i <= 5; i++) {
                        if (i > difficulty) {
                            div.add(Icons.ALL.star_border());
                        } else {
                            div.add(Icons.ALL.star());
                        }
                    }

                    TreeItem<LessonDetails> finalSuggested = suggested;
                    perDifficulty.add(Elements.li().add(div)
                            .add((todo == 1 ? "There is 1 new lesson" : "There are " + todo + " new lessons")
                                    + " for the difficulty " + getDifficulty(difficulty) + "!")
                            .add(Elements.p())
                            .add("Suggested next: ")
                            .add(Button.createPrimary(suggested.getTitle())
                                    .addClickListener(e -> selectLesson(finalSuggested)))
                            .add(Elements.p()));
                }

            }

            previewCard.setTitle("Welcome back, " + sessionInformation.getUsername() + "!");
            previewDescription.textContent("");
            previewDescription.add(TextNode.of("In total, you have completed " + completed + " out of " + total + " " +
                    "available lessons."));
            previewDescription.add(Elements.p());
            previewDescription.add(perDifficulty);
            difficulty.textContent("");
            previewTags.textContent("");
            boardPreview.showPosition(ShogiInitialPositionFactory.READ_ONLY_INITIAL_POSITION);
            boardPreview.setVisible(false);
            openButton.hidden(true);

        } else {
            previewCard.setTitle("Select a Lesson on the left");
            previewDescription.textContent("");
            difficulty.textContent("");
            previewTags.textContent("");
            boardPreview.showPosition(ShogiInitialPositionFactory.READ_ONLY_INITIAL_POSITION);
            boardPreview.setVisible(true);
            openButton.hidden(true);
        }
    }

    private String getDifficulty(final int difficulty) {
        switch (difficulty) {
            case 1:
                return "Beginner";
            case 2:
                return "Basic";
            case 3:
                return "Intermediate";
            case 4:
                return "Advanced";
            case 5:
                return "Expert";
        }
        return "";
    }

    private void showLessonPreview(final LessonDetails lesson) {
        previewCard.setTitle(lesson.getTitle());
        previewDescription.textContent(lesson.getDescription());

        difficulty.textContent("Difficulty: ");
        for (int i = 1; i <= 5; i++) {
            if (i > lesson.getDifficulty()) {
                difficulty.add(Icons.ALL.star_border());
            } else {
                difficulty.add(Icons.ALL.star());
            }
        }

        previewTags.textContent("");
        for (String tag : lesson.getTags()) {
            previewTags.add(Label.createPrimary(tag).style().setMargin("1em"));
        }

        if (lesson.getPreviewSfen() != null && !lesson.getPreviewSfen().isEmpty()) {
            boardPreview.showPosition(SfenConverter.fromSFEN(lesson.getPreviewSfen()));
            boardPreview.setVisible(true);
        } else {
            boardPreview.setVisible(false);
        }

        if (lesson.getKifuId() != null) {
            String exploreHRef = "#" + historyMapper.getToken(new ViewLessonPlace(lesson.getLessonId(),
                    lesson.getKifuId(), 0));
            openButton.attr("href", exploreHRef);
            openButton.hidden(false);
        } else if (lesson.getProblemCollectionId() != null) {
            String exploreHRef = "#" + historyMapper.getToken(new ProblemsPlace(lesson.getProblemCollectionId(), 0,
                    lesson.getLessonId()));
            openButton.attr("href", exploreHRef);
            openButton.hidden(false);
        } else {
            openButton.hidden(true);
        }
    }

    private void fillLessonsTree(final LessonDetails[] lessons) {
        for (TreeItem<LessonDetails> subItem : lessonsTree.getSubItems()) {
            lessonsTree.removeItem(subItem);
        }

        treeItems = new HashMap<>();

        for (LessonDetails lesson : lessons) {
            if (lesson.getKifuId() != null) {
                TreeItem<LessonDetails> item = TreeItem.create(lesson.getTitle(), Icons.ALL.library_mdi(), lesson);
                treeItems.put(lesson.getLessonId(), item);
            } else if (lesson.getProblemCollectionId() != null) {
                TreeItem<LessonDetails> item = TreeItem.create(lesson.getTitle(), Icons.ALL.weight_lifter_mdi(),
                        lesson);
                treeItems.put(lesson.getLessonId(), item);
            } else {
                TreeItem<LessonDetails> item = TreeItem.create(lesson.getTitle(), Icons.ALL.folder_outline_mdi(),
                        lesson);
                treeItems.put(lesson.getLessonId(), item);
            }
        }

        for (LessonDetails lesson : lessons) {
            TreeItem<LessonDetails> item = treeItems.get(lesson.getLessonId());
            if (lesson.getParentLessonId() == null) {
                lessonsTree.appendChild(item);
            } else {
                TreeItem<LessonDetails> parentLessonItem = treeItems.get(lesson.getParentLessonId());
                if (parentLessonItem != null) {
                    parentLessonItem.appendChild(item);
                }
            }

            item.addClickListener(evt -> selectLesson(item));
        }

        propagateNewIndicators();

        lessonsTree.deactivateAll();
    }

    private void propagateNewIndicators() {
        if (!sessionInformation.isLoggedIn()) {
            return;
        }

        for (TreeItem<LessonDetails> subItem : lessonsTree.getSubItems()) {
            propagateNewIndicators(subItem);
        }
    }

    private int propagateNewIndicators(final TreeItem<LessonDetails> item) {
        if (item.getSubItems().size() > 0) {
            int notCompleted = 0;

            for (TreeItem<LessonDetails> subItem : item.getSubItems()) {
                notCompleted += propagateNewIndicators(subItem);
            }
            if (notCompleted > 0) {
                item.setIndicatorContent(Badge.create(notCompleted + " New").setBackground(Color.LIGHT_GREEN));
            }
            return notCompleted;
        } else {
            LessonDetails lessonDetails = item.getValue();
            if (lessonDetails != null && (lessonDetails.getKifuId() != null || lessonDetails.getProblemCollectionId() != null)) {
                if (lessonDetails.isCompleted()) {
                    item.setIndicatorContent(Badge.create("✓"));
                    return 0;
                } else {
                    item.setIndicatorContent(Badge.create("New").setBackground(Color.LIGHT_GREEN));
                    return 1;
                }
            } else {
                return 0;
            }
        }
    }

    private void selectLesson(final TreeItem<LessonDetails> item) {
        selectedLesson = item;
        if (item != null) {
            showLessonPreview(item.getValue());
        } else {
            showLessonSelection();
        }

        breadcrumb.removeAll();
        breadcrumb.appendChild(Icons.ALL.home(), "Lessons", e -> selectLesson(null));

        if (item != null) {
            for (TreeItem<LessonDetails> treeItem : item.getPath()) {
                breadcrumb.appendChild(Icons.ALL.library_books(), treeItem.getValue().getTitle(),
                        e -> selectLesson(treeItem));
            }

            GWT.log("Activating " + item.getValue().getTitle());
            lessonsTree.deactivateAll();
            item.activate(true);
            item.expand(true);
        }
    }

    @EventHandler
    public void onLessonsList(final LessonsListEvent event) {
        GWT.log("LessonsView: handle LessonsListEvent: " + event);
        fillLessonsTree(event.getLessons());
        selectLesson(selectedLesson);
    }

    @EventHandler
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        loginAlert.hide();
    }
}
