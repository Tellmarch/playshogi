package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import elemental2.dom.EventListener;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.forms.CheckBox;
import org.dominokit.domino.ui.forms.TextBox;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.modals.ModalDialog;
import org.dominokit.domino.ui.notifications.Notification;
import org.dominokit.domino.ui.upload.FileUpload;
import org.dominokit.domino.ui.utils.TextNode;
import org.jboss.elemento.Elements;

public class ImportCollectionPopup {

    private final ModalDialog defaultSizeModal;

    public ImportCollectionPopup() {
        defaultSizeModal = createModalDialog();
        defaultSizeModal.appendChild(TextBox.create("sample"));
        defaultSizeModal.appendChild(CheckBox.create("sample"));
    }

    private ModalDialog createModalDialog() {

        ModalDialog modal = ModalDialog.create("Import Kifu Collection").setAutoClose(true);
        modal.appendChild(TextNode.of("With this dialog you can import a collection of " +
                "kifus."));

        FileUpload fileUpload =
                FileUpload.create()
                        .setIcon(Icons.ALL.touch_app())
                        .setUrl(GWT.getModuleBaseURL() + "uploadKifu")
                        .multipleFiles()
                        .autoUpload()
                        .setName("file")
                        .accept("zip,usf,kif,psn")
                        .appendChild(Elements.h(3).textContent("Drop files here or click to upload."))
                        .appendChild(
                                Elements.em()
                                        .textContent(
                                                "(Supported formats: Zip file containing .usf, .kif or .psn files.)"))
                        .onAddFile(
                                fileItem -> {
                                    Notification.createInfo("File added. " + fileItem.getFileName()).show();
                                    fileItem.addBeforeUploadHandler((request, formData) -> {
                                        formData.append("collectionId", "new");
                                        formData.append("returnUsf", "false");
                                    });
                                    fileItem.addErrorHandler(
                                            request -> Notification.createDanger("Error while uploading " + request.responseText)
                                                    .show());
                                    fileItem.addSuccessUploadHandler(
                                            request -> {
                                                GWT.log(request.responseText);
                                                Notification.createSuccess("File uploaded successfully").show();
                                            });
                                    fileItem.addRemoveHandler(
                                            file -> Notification.createInfo("File has been removed " + file.name).show());
                                });
        modal.appendChild(fileUpload);


        Button closeButton = Button.create("CLOSE").linkify();
        Button saveButton = Button.create("SAVE CHANGES").linkify();
        EventListener closeModalListener = evt -> modal.close();
        closeButton.addClickListener(evt -> fileUpload.uploadAllFiles());
        saveButton.addClickListener(closeModalListener);
        modal.appendFooterChild(saveButton);
        modal.appendFooterChild(closeButton);
        return modal;

    }

    public void show() {
        defaultSizeModal.open();
    }

    public void activate(final EventBus eventBus) {

    }
}
