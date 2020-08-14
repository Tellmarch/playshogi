mvn -B install --file playshogi-library-common/pom.xml
mvn -B install --file playshogi-library-shogi/pom.xml
mvn -B install --file playshogi-library-shogi-files/pom.xml
mvn -B install --file playshogi-library-database/pom.xml
mvn -B install --file playshogi-library-shogi-engine/pom.xml
mvn -B war:exploded --file playshogi-website-gwt-mvn/pom.xml
mvn -B gwt:devmode --file playshogi-website-gwt-mvn/pom.xml
