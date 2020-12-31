mvn -B install --file playshogi-library-shogi/pom.xml
mvn -B install --file playshogi-library-shogi-files/pom.xml
mvn -B install --file playshogi-library-database/pom.xml
mvn -B install --file playshogi-library-shogi-engine/pom.xml
mvn -B package --file playshogi-website-gwt-mvn/pom.xml
