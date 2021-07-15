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
