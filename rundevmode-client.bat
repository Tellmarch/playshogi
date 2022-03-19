::# In Dev Mode, refreshing the webpage with F5 will compile code changes on the client side - no need to restart everything

call "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.2.1\plugins\maven\lib\maven3\bin\mvn.cmd" -B install --file playshogi-library-shogi/pom.xml
call "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.2.1\plugins\maven\lib\maven3\bin\mvn.cmd" -B install --file playshogi-library-shogi-files/pom.xml
call "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.2.1\plugins\maven\lib\maven3\bin\mvn.cmd" -B install --file playshogi-library-database/pom.xml
call "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.2.1\plugins\maven\lib\maven3\bin\mvn.cmd" -B install --file playshogi-library-shogi-engine/pom.xml

call "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.2.1\plugins\maven\lib\maven3\bin\mvn.cmd" gwt:codeserver -pl playshogi-website-client -am --file playshogi-website/pom.xml

pause

::# Starts a local webserver to run the website locally
::# You may need to comment out the "ExpiresFilter" sections in playshogi-website-gwt-mvn/src/main/webapp/WEB-INF/web.xml for this to work