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
import com.playshogi.website.gwt.client.events.tutorial.LessonsListEvent;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import org.dominokit.domino.ui.breadcrumbs.Breadcrumb;
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


    private final HtmlContentBuilder<HTMLHeadingElement> difficulty;

    interface MyEventBinder extends EventBinder<LessonsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final Breadcrumb breadcrumb;
    private final Tree<LessonDetails> lessonsTree;
    private final Card previewCard;
    private final HtmlContentBuilder<HTMLElement> previewDescription;
    private final HtmlContentBuilder<HTMLElement> previewTags;

    private final PlaceController placeController;

    @Inject
    public LessonsView(final PlaceController placeController) {
        this.placeController = placeController;
        GWT.log("Creating lessons view");

        breadcrumb = Breadcrumb.create().setColor(Color.ORANGE);
        lessonsTree = Tree.create("Lessons", null);

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.add(new ElementWidget(breadcrumb.element()));

        previewDescription = Elements.span();
        previewTags = Elements.span();
        difficulty = Elements.h(4);
        previewCard =
                Card.create("Select a Lesson on the left")
                        .appendChild(previewTags)
                        .appendChild(Elements.p())
                        .appendChild(difficulty)
                        .appendChild(Elements.p())
                        .appendChild(previewDescription);

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
            } else {
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

    }
}
