package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.formats.kif.KifFormat;
import com.playshogi.library.shogi.models.formats.psn.PsnFormat;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.website.gwt.client.events.kifu.ImportGameRecordEvent;
import org.dominokit.domino.ui.dialogs.MessageDialog;
import org.dominokit.domino.ui.notifications.Notification;

import java.util.List;

public class ImportKifuPanel extends Composite implements ClickHandler {

    private final Button loadFromTextButton;
    private final TextArea textArea;
    private final HTML errorText;
    private EventBus eventBus;
    private DialogBox dialogBox;
    private String collectionId;

    public ImportKifuPanel() {

        FlowPanel verticalPanel = new FlowPanel();

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br/>")));

        verticalPanel.add(new HTML("We support importing kifus in the following formats: KIF, PSN, USF"));

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br/>")));

        textArea = new TextArea();
        textArea.setCharacterWidth(80);
        textArea.setVisibleLines(15);
        verticalPanel.add(textArea);

        loadFromTextButton = new Button("Import from text");
        loadFromTextButton.addClickHandler(this);

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br/>")));

        verticalPanel.add(loadFromTextButton);

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br/>")));

        errorText = new HTML();
        errorText.setVisible(false);

        verticalPanel.add(errorText);

        initWidget(verticalPanel);
    }

    @Override
    public void onClick(final ClickEvent event) {
        Object source = event.getSource();
        if (source == loadFromTextButton) {
            importGameFromText();
        }
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating kifu importer panel");
        this.eventBus = eventBus;
    }

    private void importGameFromText() {
        GWT.log("Importing game...");
        String gameText = textArea.getText();
        List<GameRecord> gameRecords;
        try {
            if (gameText.startsWith("USF")) {
                GWT.log("Will parse as USF game");
                gameRecords = UsfFormat.INSTANCE.read(gameText);
            } else if (gameText.startsWith("[")) {
                GWT.log("Will parse as PSN game");
                gameRecords = PsnFormat.INSTANCE.read(gameText);
            } else {
                GWT.log("Will parse as KIF game");
                gameRecords = KifFormat.INSTANCE.read(gameText);
            }
        } catch (Exception ex) {
            errorText.setVisible(true);
            errorText.setHTML(ex.getMessage());
            return;
        }
        errorText.setVisible(false);
        GWT.log("Firing game record changed event...");
        for (GameRecord gameRecord : gameRecords) {
            importGameRecord(gameRecord);
        }
    }

    private void importGameRecord(final GameRecord gameRecord) {
        GWT.log(UsfFormat.INSTANCE.write(gameRecord));
        eventBus.fireEvent(new ImportGameRecordEvent(gameRecord, collectionId));
        dialogBox.hide();
    }

    private DialogBox createImportDialogBox() {
        final DialogBox dialogBox = new DialogBox();
        dialogBox.ensureDebugId("cwDialogBox");
        dialogBox.setText("Import kifu");
        dialogBox.setGlassEnabled(true);

        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        dialogContents.add(this);
        dialogContents.setCellHorizontalAlignment(this, HasHorizontalAlignment.ALIGN_CENTER);

        Button closeButton = new Button("Close", (ClickHandler) event -> dialogBox.hide());
        dialogContents.add(closeButton);

        dialogContents.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);

        return dialogBox;
    }

    public void showInDialog(final String collectionId) {
        if (dialogBox == null) {
            dialogBox = createImportDialogBox();
        }
        this.collectionId = collectionId;
        dialogBox.center();
        dialogBox.show();
    }
}
