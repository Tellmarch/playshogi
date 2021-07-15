# Contributing

## Setup development environment

### On Linux

Recommended setup: IntelliJ Idea + local MySQL server

- Download and install IntelliJ (The community edition is free)
- Install MySQL (refer to DEPLOY.md, "Install MySQL" and "Setup database" sections)

#### Running/debugging the website locally

1. (Can be From IDEA) run the "rundevmode-client.sh" script
2. (Can be From IDEA) In another tab, run the "rundevmode-server.sh" script
3. Navigate to http://127.0.0.1:8080/ (works on Chrome or Firefox). You should first see a "compiling" message, then the landing page of PlayShogi.
4. If you make changes in the client code (playshogi-website-client). Refresh the browser page with F5 to load your changes (no need to restart everything).
5. If you make changes in the server code (playshogi-website-server). Re-run the rundevmode-server.sh script to load your changes.
6. If you make changes in a library project and/or the shared api. Re-run both server and client to load your changes.

#### Loading test data

1. Import any collection of Tsume (For instance the "Solution" folder from https://github.com/francoiswnel/Pony-Canyon-Shogi-GB-Tsume-Problems as a zip) as a problem collection.
2. For game collections, you can use e.g. SC24 zip download of all your games.

#### Deploying the website on a server

Run "buildprod.sh" to build the WAR file.  
Refer to DEPLOY.md for the server setup.

### On Windows

Most of the steps above still apply: you can still use IntelliJ with a local MySQL server.
The scripts "rundevmode-client.sh" and "rundevmode-server.sh" will need to be adapted to run on windows (you can also manually run the mvn goals from IDEA)

## Code structure

### Projects
1. **playshogi-library-shogi**: Library implementing Shogi models and rules. Fully GWT-compatible, to be used client or server side.
3. **playshogi-library-shogi-engine**: Library with USI protocol implementation, to communicate with engine (e.g. YaneuraOu). Server-side only.
4. **playshogi-library-shogi-files**: Filesystem access library. Server-side only.
5. **playshogi-library-shogi-database**: Library with SQL connection, persistent models and queries.
6. **playshogi-website-client**: Playshogi Website, client side. Fully writtent in Java with the GWT library, the code is compiled to JavaScript.
7. **playshogi-website-server**: Server backend for the website. Contains the RPC endpoints and upload servlet.
8. **playshogi-website-shared**: API for client/server communication.

### Website Code Concepts

#### Events
Used to communicate between the different components.  
They are defined in the package com.playshogi.website.gwt.client.events  

Fire an event:

    eventBus.fireEvent(new UserLoggedInEvent());
    
Listen to an event:

    @EventHandler
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        refresh();
    }
    
    
#### Places
Represents a URL for the website.  
They are defined in the package com.playshogi.website.gwt.client.place  
The Tokeniser encodes and decodes the URL.

#### Activities
Represents... an activity of the user (e.g. TutorialActivity for the tutorial, etc.)
A new instance is created everytime the user navigates to a place.
The activity has a start and stop method. It handles most of the logic, and communications with the server.

#### Views
The UI for the activities.
They are defined in the com.playshogi.website.gwt.client.ui package.

Views are singleton, only created once (per browser tab). When the user opens an activity, the activity will start by loading and activating the view.
Views contain all the layout and web components for the display.

#### Dependency injection and mappings
The AppActivityMapper injects all singleton views, and map Places to Activities.

#### ShogiBoard
Main class for the ShogiBoard widget - contains the logic to display the board, pieces and komadai, and handling user mouse events.

#### GWT widget vs DominoUI widgets
The website started with a pure GWT UI. It is being modernized and now uses a combination of GWT widgets and DominoUI (https://demo.dominokit.org/components/).
When possible prefer using the Domino alternatives.

#### RPC
The client and server mostly communicate with RPCs (Remote procedure calls).
The APIs are defined in com.playshogi.website.gwt.shared.services
The server implementation are in com.playshogi.website.gwt.server

#### SessionInformation
Class representing the user session - used for authentication, contains user properties and user preferences.

