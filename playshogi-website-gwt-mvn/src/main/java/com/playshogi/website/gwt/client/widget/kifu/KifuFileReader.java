package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.ProgressEvent;
import elemental.html.File;
import elemental.html.FileReader;
import elemental.js.html.JsFileList;
import elemental.js.html.JsInputElement;

public class KifuFileReader {
    protected void readFiles() {
        JsInputElement jsInputElement = null;
        // JsInputElement jsInputElement = fileUploadField.getFile().cast();
        JsFileList fileList = jsInputElement.getFiles();
        if (fileList == null || fileList.getLength() == 0) {
            GWT.log("file was not selected");
            return;
        }

        for (int i = 0; i < fileList.getLength(); i++) {
            readFile(fileList.item(i));
        }
    }

    private void readFile(final File file) {
        GWT.log("file.name=" + file.getName());

        FileReader fileReader = Browser.getWindow().newFileReader();
        fileReader.setOnload(new EventListener() {
            @Override
            public void handleEvent(final Event evt) {
                onFileLoad(evt);

                GWT.log("kind of evt=" + evt);
            }
        });
        fileReader.readAsDataURL(file);
    }

    private void onFileLoad(final Event evt) {
        ProgressEvent progressEvent = (ProgressEvent) evt;
        FileReader fileReader = (FileReader) evt.getTarget();

        // base64
        String result = (String) fileReader.getResult();

        // GWT.log("result=" + result);
        // GWT.debugger();

        Image image = new Image(result);
        RootPanel.get().add(image);
    }
}
