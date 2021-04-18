# Build everything. The WAR file can be found under playshogi-website/playshogi-website-server/target

mvn -B install --file playshogi-library-shogi/pom.xml
mvn -B install --file playshogi-library-shogi-files/pom.xml
mvn -B install --file playshogi-library-database/pom.xml
mvn -B install --file playshogi-library-shogi-engine/pom.xml
mvn -B install --file playshogi-website/pom.xml
