FROM amazoncorretto:8-alpine3.14
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
RUN addgroup -S app && adduser -S -G app app 
USER app
ENTRYPOINT ["java","-jar","/app.jar"]
