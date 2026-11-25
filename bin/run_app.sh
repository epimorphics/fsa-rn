#!/bin/sh
# Run generator with add-opens flags needed for java 21
exec java --add-opens=java.base/java.lang=ALL-UNNAMED \
          --add-opens=java.base/java.lang.invoke=ALL-UNNAMED \
          --add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
          --add-opens=java.base/java.net=ALL-UNNAMED \
          --add-opens=java.base/sun.net.www.protocol.http=ALL-UNNAMED \
          --add-opens=java.base/sun.net.www.protocol.https=ALL-UNNAMED  \
          --add-opens=java.base/sun.net.www=ALL-UNNAMED \
          -Xmx128m \
     -jar /app.jar "$@"
    