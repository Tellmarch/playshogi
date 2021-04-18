# In Dev Mode, refreshing the webpage with F5 will compile code changes on the client side - no need to restart everything

mvn -B install --file playshogi-library-shogi/pom.xml
mvn -B install --file playshogi-library-shogi-files/pom.xml
mvn -B install --file playshogi-library-database/pom.xml
mvn -B install --file playshogi-library-shogi-engine/pom.xml

mvn gwt:codeserver -pl playshogi-website-client -am --file playshogi-website/pom.xml

# Starts a local webserver to run the website locally
# You may need to comment out the "ExpiresFilter" sections in playshogi-website-gwt-mvn/src/main/webapp/WEB-INF/web.xml for this to work