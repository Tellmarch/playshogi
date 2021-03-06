package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.*;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.place.MyCollectionsPlace;
import com.playshogi.website.gwt.client.ui.MyCollectionsView;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

public class MyCollectionsActivity extends MyAbstractActivity {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);
    private EventBus eventBus;

    interface MyEventBinder extends EventBinder<MyCollectionsActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final MyCollectionsPlace place;
    private final MyCollectionsView view;
    private final SessionInformation sessionInformation;

    public MyCollectionsActivity(final MyCollectionsPlace place, final MyCollectionsView view,
                                 final SessionInformation sessionInformation) {
        this.place = place;
        this.view = view;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting my collections activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        view.activate(eventBus);

        fetchData();

        containerWidget.setWidget(view.asWidget());
    }

    private void fetchData() {
        if (!sessionInformation.isLoggedIn()) {
            return;
        }
        kifuService.getUserGameCollections(sessionInformation.getSessionId(), sessionInformation.getUsername(),
                new AsyncCallback<GameCollectionDetails[]>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("MyCollectionsActivity: error retrieving collections list");
                    }

                    @Override
                    public void onSuccess(GameCollectionDetails[] gameCollectionDetails) {
                        GWT.log("MyCollectionsActivity: retrieved collections list");
                        eventBus.fireEvent(new ListGameCollectionsEvent(gameCollectionDetails, null));
                    }
                });
        problemsService.getUserProblemCollections(sessionInformation.getSessionId(), sessionInformation.getUsername()
                , new AsyncCallback<ProblemCollectionDetails[]>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("MyCollectionsActivity: error retrieving pb collections list");
                    }

                    @Override
                    public void onSuccess(final ProblemCollectionDetails[] problemCollectionDetails) {
                        GWT.log("MyCollectionsActivity: retrieved pb collections list");
                        eventBus.fireEvent(new ListProblemCollectionsEvent(null, problemCollectionDetails));
                    }
                });
    }

    @EventHandler
    public void onSaveDraftCollection(final SaveDraftCollectionEvent event) {
        GWT.log("MyCollectionsActivity: Handling SaveDraftCollectionEvent: " + event);

        if (event.getId() != null) {
            Window.alert("Your collection is uploading - it may take a few minutes to import all kifus to the " +
                    "database. " +
                    "You can keep using the website during that time.");
        }

        switch (event.getType()) {
            case KIFUS:
                kifuService.saveDraftCollectionKifus(sessionInformation.getSessionId(), event.getId(),
                        new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(final Throwable throwable) {
                                GWT.log("MyCollectionsActivity: error saving draft kifus");
                                Window.alert("Failed to upload the kifus.");
                            }

                            @Override
                            public void onSuccess(final Void s) {
                                GWT.log("MyCollectionsActivity: saved draft kifus");
                                refresh();
                            }
                        });
                break;
            case GAMES:
                if (event.getId() == null) {
                    GameCollectionDetails gcDetails = new GameCollectionDetails(event.getTitle(),
                            event.getDescription(), event.getVisibility());
                    kifuService.createGameCollection(sessionInformation.getSessionId(), gcDetails,
                            new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(final Throwable throwable) {
                                    GWT.log("MyCollectionsActivity: error creating new game collection");
                                    Window.alert("Failed to create the game collection.");
                                }

                                @Override
                                public void onSuccess(final Void unused) {
                                    GWT.log("MyCollectionsActivity: created new game collection");
                                    refresh();
                                }
                            });
                } else if (event.getCollectionId() != null) {
                    kifuService.addDraftToGameCollection(sessionInformation.getSessionId(), event.getId(),
                            event.getCollectionId(), new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(final Throwable throwable) {
                                    GWT.log("MyCollectionsActivity: error adding draft game collection");
                                    Window.alert("Failed to upload the game collection.");
                                }

                                @Override
                                public void onSuccess(final Void unused) {
                                    GWT.log("MyCollectionsActivity: added draft game collection");
                                    refresh();
                                }
                            });
                } else {
                    GameCollectionDetails gcDetails = new GameCollectionDetails(event.getTitle(),
                            event.getDescription(), event.getVisibility());
                    kifuService.saveGameCollection(sessionInformation.getSessionId(), event.getId(), gcDetails,
                            new AsyncCallback<String>() {
                                @Override
                                public void onFailure(final Throwable throwable) {
                                    GWT.log("MyCollectionsActivity: error saving draft game collection");
                                    Window.alert("Failed to upload the game collection.");
                                }

                                @Override
                                public void onSuccess(final String s) {
                                    GWT.log("MyCollectionsActivity: saved draft game collection");
                                    refresh();
                                }
                            });
                }
                break;
            case PROBLEMS:
                if (event.getId() == null) {
                    ProblemCollectionDetails pcDetails = new ProblemCollectionDetails(event.getTitle(),
                            event.getDescription(), event.getVisibility(), event.getDifficulty(), event.getTags());

                    problemsService.createProblemCollection(sessionInformation.getSessionId(), pcDetails,
                            new AsyncCallback<String>() {
                                @Override
                                public void onFailure(final Throwable throwable) {
                                    GWT.log("MyCollectionsActivity: error creating new problem collection");
                                    Window.alert("Failed to create the problem collection.");
                                }

                                @Override
                                public void onSuccess(final String s) {
                                    GWT.log("MyCollectionsActivity: created problem collection");
                                    refresh();
                                }
                            });
                } else if (event.getCollectionId() != null) {
                    problemsService.addDraftToProblemCollection(sessionInformation.getSessionId(), event.getId(),
                            event.getCollectionId(), new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(final Throwable throwable) {
                                    GWT.log("MyCollectionsActivity: error adding draft problem collection");
                                    Window.alert("Failed to upload the problem collection.");
                                }

                                @Override
                                public void onSuccess(final Void unused) {
                                    GWT.log("MyCollectionsActivity: added draft problem collection");
                                    refresh();
                                }
                            });
                } else {
                    ProblemCollectionDetails pcDetails = new ProblemCollectionDetails(event.getTitle(),
                            event.getDescription(), event.getVisibility(), event.getDifficulty(), event.getTags());

                    problemsService.saveProblemsCollection(sessionInformation.getSessionId(), event.getId(), pcDetails,
                            new AsyncCallback<String>() {
                                @Override
                                public void onFailure(final Throwable throwable) {
                                    GWT.log("MyCollectionsActivity: error saving draft problem collection");
                                    Window.alert("Failed to upload the problem collection.");
                                }

                                @Override
                                public void onSuccess(final String s) {
                                    GWT.log("MyCollectionsActivity: saved draft problem collection");
                                    refresh();
                                }
                            });
                }
                break;
        }
    }

    @EventHandler
    public void onSaveGameCollectionDetails(final SaveGameCollectionDetailsEvent event) {
        GWT.log("MyCollectionsActivity: Handling SaveGameCollectionDetailsEvent: " + event.getDetails());
        kifuService.updateGameCollectionDetails(sessionInformation.getSessionId(), event.getDetails(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("MyCollectionsActivity: error during updateGameCollectionDetails");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(false));
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("MyCollectionsActivity: updateGameCollectionDetails success");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(true));
                        refresh();
                    }
                });
    }

    @EventHandler
    public void onSaveProblemCollectionDetails(final SaveProblemCollectionDetailsEvent event) {
        GWT.log("MyCollectionsActivity: Handling SaveProblemCollectionDetailsEvent: " + event.getDetails());
        problemsService.updateProblemCollectionDetails(sessionInformation.getSessionId(), event.getDetails(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("MyCollectionsActivity: error during updateProblemCollectionDetails");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(false));
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("MyCollectionsActivity: updateProblemCollectionDetails success");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(true));
                        refresh();
                    }
                });
    }


    @EventHandler
    public void onCreateGameCollection(final CreateGameCollectionEvent event) {
        GWT.log("MyCollectionsActivity: Handling CreateGameCollectionEvent: " + event.getDetails());
        kifuService.createGameCollection(sessionInformation.getSessionId(), event.getDetails(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("MyCollectionsActivity: error during createGameCollection");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(false));
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("MyCollectionsActivity: createGameCollection success");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(true));
                        refresh();
                    }
                });
    }

    @EventHandler
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        refresh();
    }

    @EventHandler
    public void onDeleteGameCollection(final DeleteGameCollectionEvent event) {
        GWT.log("MyCollectionsActivity Handling DeleteGameCollectionEvent");
        kifuService.deleteGameCollection(sessionInformation.getSessionId(), event.getCollectionId(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("MyCollectionsActivity: error during deleteGameCollection");
                        Window.alert("Deletion failed - maybe you do not have permission?");
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("MyCollectionsActivity: deleteGameCollection success");
                        refresh();
                    }
                });
    }

    @EventHandler
    public void onConvertGameCollection(final ConvertGameCollectionEvent event) {
        GWT.log("MyCollectionsActivity Handling ConvertGameCollectionEvent");
        problemsService.convertGameCollection(sessionInformation.getSessionId(), event.getCollectionId(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("MyCollectionsActivity: error during convertGameCollection");
                        Window.alert("Conversion failed - maybe you do not have permission?");
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("MyCollectionsActivity: convertGameCollection success");
                        refresh();
                    }
                });
    }

    @EventHandler
    public void onDeleteProblemCollection(final DeleteProblemCollectionEvent event) {
        GWT.log("MyCollectionsActivity Handling DeleteProblemCollectionEvent");
        problemsService.deleteProblemCollection(sessionInformation.getSessionId(), event.getCollectionId(), false,
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("MyCollectionsActivity: error during deleteProblemCollection");
                        Window.alert("Deletion failed - maybe you do not have permission?");
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("MyCollectionsActivity: deleteProblemCollection success");
                        refresh();
                    }
                });
    }

    private void refresh() {
        fetchData();
    }

}
