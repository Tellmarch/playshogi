# Starts a local webserver to run the website locally
# You may need to comment out the "ExpiresFilter" sections in playshogi-website-gwt-mvn/src/main/webapp/WEB-INF/web.xml for this to work

mvn -B install --file playshogi-library-shogi/pom.xml
mvn -B install --file playshogi-library-shogi-files/pom.xml
mvn -B install --file playshogi-library-database/pom.xml
mvn -B install --file playshogi-library-shogi-engine/pom.xml

mvn jetty:run -pl playshogi-website-server -am -Denv=dev --file playshogi-website/pom.xml
