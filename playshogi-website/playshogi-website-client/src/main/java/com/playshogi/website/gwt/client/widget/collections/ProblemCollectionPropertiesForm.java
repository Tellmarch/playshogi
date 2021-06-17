package com.playshogi.website.gwt.client.widget.collections;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.collections.SaveProblemCollectionDetailsEvent;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import elemental2.dom.HTMLDivElement;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.forms.Select;
import org.dominokit.domino.ui.forms.SelectOption;
import org.dominokit.domino.ui.forms.TextArea;
import org.dominokit.domino.ui.forms.TextBox;
import org.dominokit.domino.ui.modals.ModalDialog;
import org.dominokit.domino.ui.tag.TagsInput;
import org.dominokit.domino.ui.tag.store.LocalTagsStore;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

public class ProblemCollectionPropertiesForm {

    private TextArea description;
    private TextBox title;
    private TagsInput<String> tags;
    private Select<Integer> difficulty;
    private Select<String> visibility;
    private HtmlContentBuilder<HTMLDivElement> div = null;
    private EventBus eventBus;

    public HtmlContentBuilder<HTMLDivElement> getForm() {
        if (div != null) {
            return div;
        }
        visibility = Select.<String>create()
                .appendChild(SelectOption.create("PUBLIC", "Visibility: Public"))
                .appendChild(SelectOption.create("UNLISTED", "Visibility: Unlisted"))
                .setSearchable(false)
                .selectAt(1);
        description = TextArea.create("Description").setHelperText("Less than 5000 characters");
        title = TextBox.create("Title");
        title.setValue("My New Problem Collection");

        LocalTagsStore<String> tagsStore =
                LocalTagsStore.<String>create()
                        .addItem("Openings", "Openings")
                        .addItem("Hisshi", "Hisshi")
                        .addItem("Castle", "Castle")
                        .addItem("Endgame", "Endgame")
                        .addItem("Find the next move", "Find the next move")
                        .addItem("For beginners", "For beginners")
                        .addItem("Tsume", "Tsume");
        tags = TagsInput.create("Tags", tagsStore).setPlaceholder("Tags...");

        difficulty = Select.<Integer>create()
                .appendChild(SelectOption.create(1, "Difficulty: Very Easy (★☆☆☆☆)"))
                .appendChild(SelectOption.create(2, "Difficulty: Easy (★★☆☆☆)"))
                .appendChild(SelectOption.create(3, "Difficulty: Medium (★★★☆☆)"))
                .appendChild(SelectOption.create(4, "Difficulty: Hard (★★★★☆)"))
                .appendChild(SelectOption.create(5, "Difficulty: Very Hard (★★★★★)"))
                .setSearchable(false)
                .selectAt(3);

        div = Elements.div().add(title).add(description).add(difficulty).add(visibility).add(tags);

        return div;
    }

    public String[] getTags() {
        return tags.getValue().toArray(new String[0]);
    }

    public Integer getDifficulty() {
        return difficulty.getValue();
    }

    public String getVisibility() {
        return visibility.getValue();
    }

    public String getDescription() {
        return description.getStringValue();
    }

    public String getTitle() {
        return title.getStringValue();
    }

    public void showInPopup(final ProblemCollectionDetails details) {
        ModalDialog modal = ModalDialog.create("Collection properties").setAutoClose(false);
        modal.appendChild(getForm());
        fillWithDetails(details);
        Button closeButton = Button.create("CANCEL").linkify();
        closeButton.addClickListener(evt -> modal.close());
        Button saveButton = Button.create("SAVE CHANGES").linkify();
        saveButton.addClickListener(evt -> {
            eventBus.fireEvent(new SaveProblemCollectionDetailsEvent(updateDetails(details)));
            modal.close();
        });
        modal.appendFooterChild(saveButton);
        modal.appendFooterChild(closeButton);
        modal.open();
    }

    private ProblemCollectionDetails updateDetails(final ProblemCollectionDetails details) {
        ProblemCollectionDetails result = new ProblemCollectionDetails(getTitle(), getDescription(), getVisibility(),
                getDifficulty(), getTags());
        result.setId(details.getId());
        return result;
    }

    private void fillWithDetails(final ProblemCollectionDetails details) {
        title.setValue(details.getName() == null ? "" : details.getName());
        description.setValue(details.getDescription() == null ? "" : details.getDescription());
        visibility.selectAt("PUBLIC".equalsIgnoreCase(details.getVisibility()) ? 0 : 1);
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
    }
}
