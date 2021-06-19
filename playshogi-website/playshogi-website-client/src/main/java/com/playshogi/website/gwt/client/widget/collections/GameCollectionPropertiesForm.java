package com.playshogi.website.gwt.client.widget.collections;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.collections.SaveGameCollectionDetailsEvent;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import elemental2.dom.HTMLDivElement;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.forms.Select;
import org.dominokit.domino.ui.forms.SelectOption;
import org.dominokit.domino.ui.forms.TextArea;
import org.dominokit.domino.ui.forms.TextBox;
import org.dominokit.domino.ui.modals.ModalDialog;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

public class GameCollectionPropertiesForm {

    private TextArea description;
    private TextBox title;
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
        title.setValue("My New Game Collection");

        div = Elements.div().add(title).add(description).add(visibility);

        return div;
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

    public void showInPopup(final GameCollectionDetails details) {
        ModalDialog modal = ModalDialog.create("Collection properties").setAutoClose(false);
        modal.appendChild(getForm());
        fillWithDetails(details);
        Button closeButton = Button.create("CANCEL").linkify();
        closeButton.addClickListener(evt -> modal.close());
        Button saveButton = Button.create("SAVE CHANGES").linkify();
        saveButton.addClickListener(evt -> {
            eventBus.fireEvent(new SaveGameCollectionDetailsEvent(updateDetails(details)));
            modal.close();
        });
        modal.appendFooterChild(saveButton);
        modal.appendFooterChild(closeButton);
        modal.open();
    }

    private GameCollectionDetails updateDetails(final GameCollectionDetails details) {
        GameCollectionDetails result = new GameCollectionDetails(getTitle(), getDescription(), getVisibility());
        result.setId(details.getId());
        return result;
    }

    private void fillWithDetails(final GameCollectionDetails details) {
        title.setValue(details.getName() == null ? "" : details.getName());
        description.setValue(details.getDescription() == null ? "" : details.getDescription());
        visibility.selectAt("PUBLIC".equalsIgnoreCase(details.getVisibility()) ? 0 : 1);
    }

    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
