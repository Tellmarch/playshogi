package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.tutorial.LessonsListEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.tables.LessonsTable;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.collections.LessonPropertiesForm;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import elemental2.dom.HTMLDivElement;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Styles;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class ManageLessonsView extends Composite {

    private final LessonsTable lessonsTable;

    interface MyEventBinder extends EventBinder<ManageLessonsView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);
    private final LessonPropertiesForm lessonPropertiesForm;

    private EventBus eventBus;

    @Inject
    public ManageLessonsView(final SessionInformation sessionInformation, AppPlaceHistoryMapper historyMapper) {

        lessonPropertiesForm = new LessonPropertiesForm();
        lessonsTable = new LessonsTable(historyMapper);

        HtmlContentBuilder<HTMLDivElement> div = Elements.div();
        div.css(Styles.padding_20);

        div.add(Button.createPrimary(Icons.ALL.add_circle()).setContent("Add Lesson")
                .addClickListener(evt -> lessonPropertiesForm.showInPopup(new LessonDetails()))
                .style().setMarginRight("10px").setMarginBottom("20px"));

        div.add(lessonsTable.getTable());


        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(new ElementWidget(div.element()));
        scrollPanel.setSize("100%", "100%");
        initWidget(scrollPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating ManageLessonsView");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        lessonsTable.activate(eventBus);
        lessonPropertiesForm.activate(eventBus);
    }

    @EventHandler
    public void onLessonsList(final LessonsListEvent event) {
        GWT.log("ManageLessonsView: handle LessonsListEvent");
        lessonsTable.setData(Arrays.asList(event.getLessons()));
    }
}
