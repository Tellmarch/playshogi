# Starts a local webserver to run the website locally

call "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.2.1\plugins\maven\lib\maven3\bin\mvn.cmd" -B install --file playshogi-library-shogi/pom.xml
call "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.2.1\plugins\maven\lib\maven3\bin\mvn.cmd" -B install --file playshogi-library-shogi-files/pom.xml
call "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.2.1\plugins\maven\lib\maven3\bin\mvn.cmd" -B install --file playshogi-library-database/pom.xml
call "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.2.1\plugins\maven\lib\maven3\bin\mvn.cmd" -B install --file playshogi-library-shogi-engine/pom.xml

call "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.2.1\plugins\maven\lib\maven3\bin\mvn.cmd" jetty:run -pl playshogi-website-server -am -Denv=dev --file playshogi-website/pom.xml
