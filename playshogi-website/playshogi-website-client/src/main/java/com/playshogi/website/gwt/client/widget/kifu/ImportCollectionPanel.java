package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.collections.DraftCollectionUploadedEvent;

public class ImportCollectionPanel extends Composite {

    private EventBus eventBus;
    private DialogBox dialogBox;

    public ImportCollectionPanel() {

        FlowPanel verticalPanel = new FlowPanel();

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("With this dialog you can import a collection of " +
                "games. </br> Supported format: Zip file containing .usf, .kif or .psn files. <p/>")));

        FormPanel form = createUploadForm();
        verticalPanel.add(form);

        verticalPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br/>")));

        initWidget(verticalPanel);
    }

    private FormPanel createUploadForm() {
        FormPanel form = new FormPanel();
        form.setAction(GWT.getModuleBaseURL() + "uploadKifu");
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        VerticalPanel panel = new VerticalPanel();
        form.setWidget(panel);

        FileUpload upload = new FileUpload();
        upload.setName("file");
        panel.add(upload);
        panel.add(new Hidden("collectionId", "new"));
        panel.add(new Hidden("returnUsf", "false"));

        panel.add(new Button("Upload", (ClickHandler) event -> form.submit()));

        form.addSubmitHandler(event -> GWT.log("Submit event"));
        form.addSubmitCompleteHandler(event -> {
            GWT.log("Submit complete");
            String result = event.getResults();
            if (result.startsWith("ERROR")) {
                Window.alert(event.getResults());
            } else if (result.startsWith("COLLECTION:")) {
                String draftCollectionId = result.substring(11).trim();
                GWT.log("Successfully uploaded draft collection: " + draftCollectionId);

                eventBus.fireEvent(new DraftCollectionUploadedEvent(draftCollectionId));
                dialogBox.hide();
            } else {
                GWT.log("Don't know how to handle the response: " + result);
            }
        });
        return form;
    }

    private DialogBox createImportDialogBox() {
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Import Game Collection");
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

    public void showInDialog() {
        if (dialogBox == null) {
            dialogBox = createImportDialogBox();
        }
        dialogBox.center();
        dialogBox.show();
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating kifu importer panel");
        this.eventBus = eventBus;
    }

}
