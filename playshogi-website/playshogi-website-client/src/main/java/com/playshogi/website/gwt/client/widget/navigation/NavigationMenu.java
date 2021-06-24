package com.playshogi.website.gwt.client.widget.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.events.user.UserLoggedOutEvent;
import com.playshogi.website.gwt.client.place.*;
import com.playshogi.website.gwt.shared.models.KifuDetails;

public class NavigationMenu extends Composite {

    interface MyEventBinder extends EventBinder<NavigationMenu> {
    }

    private final PlaceController placeController;
    private final SessionInformation sessionInformation;

    private final MenuItem accountMenuItem;
    private final MenuBar accountMenu;

    @Inject
    public NavigationMenu(final SessionInformation sessionInformation,
                          final EventBus eventBus, final PlaceController placeController) {
        GWT.log("Creating navigation menu");

        this.sessionInformation = sessionInformation;
        this.placeController = placeController;

        MyEventBinder eventBinder = GWT.create(MyEventBinder.class);
        eventBinder.bindEventHandlers(this, eventBus);

        MenuBar menu = new MenuBar();
        menu.setAutoOpen(true);
        menu.setAnimationEnabled(true);

        menu.addItem(new MenuItem("PlayShogi.com", (Command) () -> placeController.goTo(new MainPagePlace())));


        MenuBar learnMenu = new MenuBar(true);
        menu.addSeparator();
        menu.addItem(new MenuItem("Learn", learnMenu));
        learnMenu.addItem("How to play", (Command) () -> placeController.goTo(new TutorialPlace()));
//        learnMenu.addItem("Lessons", (Command) () -> placeController.goTo(new LessonsPlace()));
        learnMenu.addItem("Beginner puzzles", (Command) () -> placeController.goTo(new ProblemCollectionsPlace(
                "Beginners")));
        learnMenu.addItem("Links", (Command) () -> placeController.goTo(new LinksPlace()));

        MenuBar puzzlesMenu = new MenuBar(true);
        menu.addSeparator();
        menu.addItem(new MenuItem("Puzzles", puzzlesMenu));
        puzzlesMenu.addItem("TsumeShogi Problems", (Command) () -> placeController.goTo(new TsumePlace()));
        puzzlesMenu.addItem("ByoYomi Survival", (Command) () -> placeController.goTo(new ByoYomiLandingPlace()));
        puzzlesMenu.addItem("Problem Collections", (Command) () -> placeController.goTo(new ProblemCollectionsPlace()));

        MenuBar practiceMenu = new MenuBar(true);
        menu.addSeparator();
        menu.addItem(new MenuItem("Practice", practiceMenu));
        practiceMenu.addItem("Openings Explorer", (Command) () -> placeController.goTo(new OpeningsPlace()));
        practiceMenu.addItem("Play vs Computer", (Command) () -> placeController.goTo(new PlayPlace()));
        practiceMenu.addItem("Free Board", (Command) () -> placeController.goTo(new FreeBoardPlace()));

        MenuBar collectionsMenu = new MenuBar(true);
        menu.addSeparator();
        menu.addItem(new MenuItem("Collections", collectionsMenu));
        collectionsMenu.addItem("Public Collections",
                (Command) () -> placeController.goTo(new PublicCollectionsPlace()));
        //collectionsMenu.addItem("Tournament Collections", (Command) () -> placeController.goTo((new TournamentPlace
        // ()))); TODO new item on menu
        collectionsMenu.addItem("My Collections", (Command) () -> placeController.goTo(new MyCollectionsPlace()));
        collectionsMenu.addItem("My Kifus", (Command) () -> placeController.goTo(new UserKifusPlace()));
        collectionsMenu.addItem("New/Import kifu", (Command) () -> placeController.goTo(new KifuEditorPlace(null,
                KifuDetails.KifuType.GAME, null)));


        MenuBar aboutMenu = new MenuBar(true);
        menu.addSeparator();
        menu.addItem(new MenuItem("About", aboutMenu));
        aboutMenu.addItem("Terms of Service", (Command) () -> Window.open("terms.html", "_blank", ""));
        aboutMenu.addItem("Privacy Policy", (Command) () -> Window.open("terms.html", "_blank", ""));
        aboutMenu.addItem("Change log", (Command) () -> Window.open("changes.html", "_blank", ""));
        aboutMenu.addItem("Contact", (Command) () -> Window.open("mailto:playshogi@hotmail.com", "_blank", ""));

        accountMenu = new MenuBar(true);
        menu.addSeparator();
        accountMenuItem = new MenuItem(getAccountMenuTitle(), accountMenu);
        menu.addItem(accountMenuItem);
        accountMenu.addItem("Login/Register", (Command) () -> placeController.goTo(new LoginPlace("login")));
        accountMenu.addItem("Logout", (Command) () -> placeController.goTo(new LoginPlace("logout")));
        accountMenu.addItem("Statistics", (Command) () -> placeController.goTo(new ProblemStatisticsPlace()));

        initWidget(menu);
    }

    @EventHandler
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        accountMenuItem.setText(getAccountMenuTitle());
        accountMenu.clearItems();
        accountMenu.addItem("Logout", (Command) () -> placeController.goTo(new LoginPlace("logout")));
        accountMenu.addItem("Statistics", (Command) () -> placeController.goTo(new ProblemStatisticsPlace()));
        if (sessionInformation.isAdmin()) {
            accountMenu.addItem("Manage Problems", (Command) () -> placeController.goTo(new ManageProblemsPlace()));
            accountMenu.addItem("Manage Lessons", (Command) () -> placeController.goTo(new ManageLessonsPlace()));
        }
    }

    @EventHandler
    public void onUserLoggedOut(final UserLoggedOutEvent event) {
        accountMenuItem.setText(getAccountMenuTitle());
        accountMenu.clearItems();
        accountMenu.addItem("Login/Register", (Command) () -> placeController.goTo(new LoginPlace("login")));
    }

    private String getAccountMenuTitle() {
        return "Account [" + sessionInformation.getUsername() + "]";
    }
}
