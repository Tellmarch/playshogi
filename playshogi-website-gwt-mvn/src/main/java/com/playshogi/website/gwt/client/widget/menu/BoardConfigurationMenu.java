package com.playshogi.website.gwt.client.widget.menu;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class BoardConfigurationMenu extends Composite {

    private final ShogiBoard shogiBoard;

    String[] phrases = new String[]{"test1", "test2", "test3", "test4"};

    public BoardConfigurationMenu(final ShogiBoard shogiBoard) {
        this.shogiBoard = shogiBoard;
        Command menuCommand = new Command() {
            private int curPhrase = 0;
            private final String[] phrases = BoardConfigurationMenu.this.phrases;

            @Override
            public void execute() {
                Window.alert(phrases[curPhrase]);
                curPhrase = (curPhrase + 1) % phrases.length;
            }
        };

        // Create a menu bar
        MenuBar menu = new MenuBar();
        menu.setAutoOpen(true);
        menu.setWidth("500px");
        menu.setAnimationEnabled(true);

        // Create a sub menu of recent documents
        MenuBar recentDocsMenu = new MenuBar(true);
        String[] recentDocs = phrases;
        for (int i = 0; i < recentDocs.length; i++) {
            recentDocsMenu.addItem(recentDocs[i], menuCommand);
        }

        // Create the file menu
        MenuBar fileMenu = new MenuBar(true);
        fileMenu.setAnimationEnabled(true);
        menu.addItem(new MenuItem("File", fileMenu));
        fileMenu.addItem("Open...", recentDocsMenu);
        String[] fileOptions = phrases;
        for (int i = 0; i < fileOptions.length; i++) {
            if (i == 3) {
                fileMenu.addSeparator();
                fileMenu.addItem(fileOptions[i], recentDocsMenu);
                fileMenu.addSeparator();
            } else {
                fileMenu.addItem(fileOptions[i], menuCommand);
            }
        }

        // Create the edit menu
        MenuBar editMenu = new MenuBar(true);
        menu.addItem(new MenuItem("Edit", editMenu));
        editMenu.addItem("Copy", menuCommand);

        // Create the help menu
        MenuBar helpMenu = new MenuBar(true);
        menu.addSeparator();
        menu.addItem(new MenuItem("Help", helpMenu));
        helpMenu.addItem("About", menuCommand);

        menu.ensureDebugId("cwMenuBar");

        initWidget(menu);
    }
}
