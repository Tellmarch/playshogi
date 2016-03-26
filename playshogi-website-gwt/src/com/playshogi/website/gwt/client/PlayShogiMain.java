package com.playshogi.website.gwt.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.playshogi.library.models.record.GameNavigation;
import com.playshogi.library.models.record.GameTree;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.mvp.AppActivityMapper;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.MainPagePlace;
import com.playshogi.website.gwt.client.widget.board.GameImporter;
import com.playshogi.website.gwt.client.widget.board.GameNavigator;
import com.playshogi.website.gwt.client.widget.board.PositionSharing;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.client.widget.navigation.NavigationBar;

public class PlayShogiMain implements EntryPoint {

	private final SimplePanel appWidget = new SimplePanel();
	private final Place defaultPlace = new MainPagePlace();

	@Override
	public void onModuleLoad() {
		ClientFactory clientFactory = GWT.create(ClientFactory.class);
		EventBus eventBus = clientFactory.getEventBus();
		PlaceController placeController = clientFactory.getPlaceController();

		ActivityMapper activityMapper = new AppActivityMapper(clientFactory);
		ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		activityManager.setDisplay(appWidget);

		AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(placeController, eventBus, defaultPlace);

		DockLayoutPanel p = new DockLayoutPanel(Unit.EM);
		p.addNorth(new NavigationBar(historyMapper), 2);
		// p.addSouth(new HTML("footer"), 2);
		// p.addWest(new HTML("navigation"), 10);
		p.add(appWidget);

		RootLayoutPanel.get().add(p);
		historyHandler.handleCurrentHistory();
	}

	private void myOldMain() {
		ShogiBoard shogiBoard = new ShogiBoard();

		ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
		GameNavigation<ShogiPosition> gameNavigation = new GameNavigation<>(shogiRulesEngine, new GameTree(),
				shogiBoard.getPosition());

		PositionSharing positionSharing = new PositionSharing(shogiBoard);
		GameNavigator gameNavigator = new GameNavigator(shogiBoard, gameNavigation);
		GameImporter gameImporter = new GameImporter(shogiBoard, gameNavigation);
		// BoardConfigurationMenu boardConfigurationMenu = new
		// BoardConfigurationMenu(shogiBoard);
		// RootPanel.get().add(boardConfigurationMenu);
		// shogiBoard.setShogiBoardHandler(positionSharing);
		shogiBoard.setShogiBoardHandler(gameNavigator);

		RootPanel.get().add(positionSharing);
		RootPanel.get().add(gameNavigator);
		RootPanel.get().add(shogiBoard);
		RootPanel.get().add(gameImporter);
	}

}
