package com.playshogi.website.gwt.client.widget.collections;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.website.gwt.client.events.collections.ListKifusEvent;
import com.playshogi.website.gwt.client.events.tutorial.LessonsListEvent;
import com.playshogi.website.gwt.client.events.tutorial.SaveLessonDetailsEvent;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import elemental2.dom.HTMLDivElement;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.forms.Select;
import org.dominokit.domino.ui.forms.SelectOption;
import org.dominokit.domino.ui.forms.TextArea;
import org.dominokit.domino.ui.forms.TextBox;
import org.dominokit.domino.ui.modals.ModalDialog;
import org.dominokit.domino.ui.tag.TagsInput;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

public class LessonPropertiesForm {

    interface MyEventBinder extends EventBinder<LessonPropertiesForm> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private TextArea description;
    private TextBox title;
    private Select<String> kifuId;
    private Select<String> parentLessonId;
    private TextBox previewSfen;
    private TextBox author;
    private TagsInput<String> tags;
    private Select<Integer> difficulty;
    private Select<Boolean> visibility;
    private HtmlContentBuilder<HTMLDivElement> div = null;
    private EventBus eventBus;
    private TextBox problemCollectionId;

    public LessonPropertiesForm() {
        getForm();
    }

    public HtmlContentBuilder<HTMLDivElement> getForm() {
        if (div != null) {
            return div;
        }
        visibility = Select.<Boolean>create()
                .appendChild(SelectOption.create(true, "Visibility: Hidden"))
                .appendChild(SelectOption.create(false, "Visibility: Public"))
                .setSearchable(false)
                .selectAt(1);
        description = TextArea.create("Description").setHelperText("Less than 5000 characters");
        title = TextBox.create("Title");
        title.setValue("My New Problem Collection");
        kifuId = Select.create();
        kifuId.setHelperText("Lesson Kifu");
        problemCollectionId = TextBox.create("problemCollectionId");
        parentLessonId = Select.create();
        parentLessonId.setHelperText("Parent Lesson");
        previewSfen = TextBox.create("previewSfen");
        previewSfen.setValue(SfenConverter.INITIAL_POSITION_SFEN);
        author = TextBox.create("author");

        tags = TagsInput.create("Tags").setPlaceholder("Tags...");

        difficulty = Select.<Integer>create()
                .appendChild(SelectOption.create(1, "Difficulty: Very Easy (★☆☆☆☆)"))
                .appendChild(SelectOption.create(2, "Difficulty: Easy (★★☆☆☆)"))
                .appendChild(SelectOption.create(3, "Difficulty: Medium (★★★☆☆)"))
                .appendChild(SelectOption.create(4, "Difficulty: Hard (★★★★☆)"))
                .appendChild(SelectOption.create(5, "Difficulty: Very Hard (★★★★★)"))
                .setSearchable(false)
                .selectAt(2);

        div = Elements.div()
                .add(title)
                .add(description)
                .add(kifuId)
                .add(problemCollectionId)
                .add(parentLessonId)
                .add(previewSfen)
                .add(author)
                .add(difficulty)
                .add(visibility)
                .add(tags);

        return div;
    }

    public String[] getTags() {
        return tags.getValue().toArray(new String[0]);
    }

    public Integer getDifficulty() {
        return difficulty.getValue();
    }

    public void showInPopup(final LessonDetails details) {
        ModalDialog modal = ModalDialog.create("Lesson properties").setAutoClose(false);
        modal.appendChild(getForm());
        fillWithDetails(details);
        Button closeButton = Button.create("CANCEL").linkify();
        closeButton.addClickListener(evt -> modal.close());
        Button saveButton = Button.create("SAVE CHANGES").linkify();
        saveButton.addClickListener(evt -> {
            eventBus.fireEvent(new SaveLessonDetailsEvent(updateDetails(details)));
            modal.close();
        });
        modal.appendFooterChild(saveButton);
        modal.appendFooterChild(closeButton);
        modal.open();
    }

    private LessonDetails updateDetails(final LessonDetails details) {
        return new LessonDetails(details.getLessonId(), kifuId.getSelectedOption().getValue(),
                parentLessonId.getSelectedOption().getValue(), problemCollectionId.getStringValue(),
                title.getStringValue(),
                description.getStringValue(), getTags(), previewSfen.getStringValue(), author.getStringValue(),
                getDifficulty(), details.getLikes(), details.isCompleted(), visibility.getValue(), details.getIndex());
    }

    private void fillWithDetails(final LessonDetails details) {
        title.setValue(details.getTitle() == null ? "" : details.getTitle());
        description.setValue(details.getDescription() == null ? "" : details.getDescription());
        kifuId.setValue(details.getKifuId() == null ? "" : details.getKifuId());
        problemCollectionId.setValue(details.getProblemCollectionId() == null ? "" : details.getProblemCollectionId());
        parentLessonId.setValue(details.getParentLessonId() == null ? "" : details.getParentLessonId());
        previewSfen.setValue(details.getPreviewSfen() == null ? "" : details.getPreviewSfen());
        author.setValue(details.getAuthor() == null ? "" : details.getAuthor());
        visibility.selectAt(details.isHidden() ? 0 : 1);
        difficulty.setValue(details.getDifficulty());
        tags.clear();
        if (details.getTags() != null) {
            for (String tag : details.getTags()) {
                tags.addValue(tag);
            }
        }
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onLessonsList(final LessonsListEvent event) {
        GWT.log("LessonPropertiesForm: handle LessonsListEvent");
        parentLessonId.removeAllOptions();
        parentLessonId.appendChild(SelectOption.create("", " - None - "));
        for (LessonDetails details : event.getLessons()) {
            parentLessonId.appendChild(SelectOption.create(details.getLessonId(), details.getTitle()));
        }
    }

    @EventHandler
    public void onListKifusEvent(final ListKifusEvent event) {
        GWT.log("LessonPropertiesForm: handle ListKifusEvent");
        kifuId.removeAllOptions();
        kifuId.appendChild(SelectOption.create("", " - None - "));
        for (KifuDetails details : event.getKifus()) {
            kifuId.appendChild(SelectOption.create(details.getId(), details.getName()));
        }
    }
}
