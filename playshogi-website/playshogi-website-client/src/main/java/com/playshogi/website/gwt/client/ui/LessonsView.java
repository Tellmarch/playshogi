package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.tutorial.LessonsListEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.ProblemsPlace;
import com.playshogi.website.gwt.client.place.ViewLessonPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.BoardPreview;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.Node;
import jsinterop.base.Js;
import org.dominokit.domino.ui.breadcrumbs.Breadcrumb;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.cards.Card;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.grid.Row_12;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.labels.Label;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.tree.Tree;
import org.dominokit.domino.ui.tree.TreeItem;
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

    private final PlaceController placeController;
    private final SessionInformation sessionInformation;
    private final AppPlaceHistoryMapper historyMapper;

    private LessonDetails lesson;

    @Inject
    public LessonsView(final PlaceController placeController, final SessionInformation sessionInformation,
                       final AppPlaceHistoryMapper historyMapper) {
        this.placeController = placeController;
        this.sessionInformation = sessionInformation;
        this.historyMapper = historyMapper;
        GWT.log("Creating lessons view");

        breadcrumb = Breadcrumb.create().setColor(Color.ORANGE);
        lessonsTree = Tree.create("Lessons", null);

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.add(new ElementWidget(breadcrumb.element()));

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

        Row_12 row_12 = Row.create()
                .addColumn(Column.span3().appendChild(lessonsTree))
                .addColumn(Column.span9().appendChild(previewCard));

        flowPanel.add(new ElementWidget(row_12.element()));
        initWidget(flowPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating lessons view");
        eventBinder.bindEventHandlers(this, eventBus);
        breadcrumb.removeAll();
        breadcrumb.appendChild(Icons.ALL.home(), "Lessons", e -> {
        });
    }

    private void showLessonPreview(final LessonDetails lesson) {
        this.lesson = lesson;
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
            GWT.log("Showing position " + lesson.getPreviewSfen());
            boardPreview.showPosition(SfenConverter.fromSFEN(lesson.getPreviewSfen()));
            boardPreview.setVisible(true);
        } else {
            boardPreview.setVisible(false);
        }

        if (lesson.getKifuId() != null) {
            String exploreHRef = "#" + historyMapper.getToken(new ViewLessonPlace(lesson.getKifuId(), 0));
            openButton.attr("href", exploreHRef);
            openButton.hidden(false);
        } else if (lesson.getProblemCollectionId() != null) {
            String exploreHRef = "#" + historyMapper.getToken(new ProblemsPlace(lesson.getProblemCollectionId(), 0));
            openButton.attr("href", exploreHRef);
            openButton.hidden(false);
        } else {
            openButton.hidden(true);
        }
    }

    @EventHandler
    public void onLessonsList(final LessonsListEvent event) {
        GWT.log("LessonsView: handle LessonsListEvent");

        for (TreeItem<LessonDetails> subItem : lessonsTree.getSubItems()) {
            lessonsTree.removeItem(subItem);
        }

        Map<String, TreeItem<LessonDetails>> items = new HashMap<>();

        for (LessonDetails lesson : event.getLessons()) {
            items.put(lesson.getLessonId(), TreeItem.create(lesson.getTitle(), lesson));
        }

        for (LessonDetails lesson : event.getLessons()) {
            TreeItem<LessonDetails> item = items.get(lesson.getLessonId());
            if (lesson.getParentLessonId() == null) {
                lessonsTree.appendChild(item);
            } else if (items.get(lesson.getParentLessonId()) != null) {
                items.get(lesson.getParentLessonId()).appendChild(item);
            }

            item.addClickListener(evt -> {
                showLessonPreview(lesson);

                breadcrumb.removeAll();
                breadcrumb.appendChild(Icons.ALL.home(), "Lessons", e -> {
                });

                for (LessonDetails l : item.getPathValues()) {
                    breadcrumb.appendChild(Icons.ALL.library_books(), l.getTitle(), e -> {
                    });
                }

            });
        }

    }
}
