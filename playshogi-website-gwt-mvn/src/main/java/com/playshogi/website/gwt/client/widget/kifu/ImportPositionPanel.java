package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.formats.kif.KifFormat;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.ImportGameRecordEvent;

public class ImportPositionPanel extends Composite implements ClickHandler {

    private static final String SFEN_EXAMPLE = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b " +
            "2Pp";
    private static final String KIF_EXAMPLE = "後手の持駒：角　歩\n" +
            "  ９ ８ ７ ６ ５ ４ ３ ２ １\n" +
            "+---------------------------+\n" +
            "|v香v桂 ・v金 ・ ・ ・v桂v香|一\n" +
            "| ・v玉v銀 ・v金 ・ ・v飛 ・|二\n" +
            "| ・v歩v歩v歩v歩 ・ ・v歩v歩|三\n" +
            "|v歩 ・ ・ ・ ・v銀v歩 ・ ・|四\n" +
            "| ・ ・ ・ ・ ・ ・ ・ 歩 ・|五\n" +
            "| 歩 ・ 歩 歩 ・ 銀 歩 ・ 歩|六\n" +
            "| ・ 歩 銀 ・ 歩 ・ ・ ・ ・|七\n" +
            "| ・ 玉 金 ・ ・ ・ ・ 飛 ・|八\n" +
            "| 香 桂 ・ ・ ・ 金 ・ 桂 香|九\n" +
            "+---------------------------+\n" +
            "先手の持駒：角　歩\n" +
            "後手番\n";

    private final Button loadFromTextButton;
    private final TextArea textArea;

    private EventBus eventBus;
    private DialogBox dialogBox;
    private String collectionId;
    private final RadioButton sfenButton;
    private final RadioButton kifButton;
    private final RadioButton psnButton;

    public ImportPositionPanel() {

        FlowPanel verticalPanel = new FlowPanel();

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br/>")));

        verticalPanel.add(new HTML("We support importing positions in the following formats: SFEN, KIF diagram, PSN " +
                "diagram"));

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br/>")));

        textArea = new TextArea();
        textArea.setCharacterWidth(80);
        textArea.setVisibleLines(15);
        verticalPanel.add(textArea);

        sfenButton = new RadioButton("format", "SFEN");
        kifButton = new RadioButton("format", "KIF diagram");
        psnButton = new RadioButton("format", "PSN diagram");

        sfenButton.addClickHandler(this);
        kifButton.addClickHandler(this);
        psnButton.addClickHandler(this);


        FlowPanel radiosPanel = new FlowPanel();
        radiosPanel.add(sfenButton);
        radiosPanel.add(kifButton);
        radiosPanel.add(psnButton);
        verticalPanel.add(radiosPanel);

        loadFromTextButton = new Button("Import from text");
        loadFromTextButton.addClickHandler(this);

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br/>")));

        verticalPanel.add(loadFromTextButton);

        initWidget(verticalPanel);
    }

    @Override
    public void onClick(final ClickEvent event) {
        Object source = event.getSource();
        if (source == loadFromTextButton) {
            importPositionFromText();
        } else if (source == sfenButton && isEmptyOrTemplate()) {
            textArea.setText(SFEN_EXAMPLE);
            textArea.setSelectionRange(0, textArea.getText().length());
            textArea.setFocus(true);
        } else if (source == kifButton && isEmptyOrTemplate()) {
            textArea.setText(KIF_EXAMPLE);
            textArea.setSelectionRange(0, textArea.getText().length());
            textArea.setFocus(true);
        }
    }

    private boolean isEmptyOrTemplate() {
        return textArea.getText().isEmpty()
                || textArea.getText().equals(KIF_EXAMPLE)
                || textArea.getText().equals(SFEN_EXAMPLE);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating position importer panel");
        this.eventBus = eventBus;
    }

    private void importPositionFromText() {
        GWT.log("Importing position...");
        String gameText = textArea.getText();
        ShogiPosition position = null;
        if (sfenButton.getValue()) {
            GWT.log("Will parse as SFEN position");
            position = SfenConverter.fromSFEN(textArea.getText());
        } else if (kifButton.getValue()) {
            GWT.log("Will parse as KIF position");
            position = KifFormat.INSTANCE.readPosition(textArea.getText());
        } else if (psnButton.getValue()) {
            GWT.log("Will parse as PSN position");
        }
        if (position != null) {
            GWT.log("Firing position changed event...");
            eventBus.fireEvent(new PositionChangedEvent(position, true));
        }
        dialogBox.hide();
    }

    private void importGameRecord(final GameRecord gameRecord) {
        eventBus.fireEvent(new ImportGameRecordEvent(gameRecord, collectionId));
        dialogBox.hide();
    }

    private DialogBox createImportDialogBox() {
        final DialogBox dialogBox = new DialogBox();
        dialogBox.ensureDebugId("cwDialogBox");
        dialogBox.setText("Import position");
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
