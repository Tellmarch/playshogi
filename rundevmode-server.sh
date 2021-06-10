# Starts a local webserver to run the website locally

mvn -B install --file playshogi-library-shogi/pom.xml
mvn -B install --file playshogi-library-shogi-files/pom.xml
mvn -B install --file playshogi-library-database/pom.xml
mvn -B install --file playshogi-library-shogi-engine/pom.xml

mvn jetty:run -pl playshogi-website-server -am -Denv=dev --file playshogi-website/pom.xml
