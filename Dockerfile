FROM amazoncorretto:21-al2023-headless
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} /app.jar
COPY ./bin/run_app.sh /run_app.sh
RUN yum install shadow-utils -y
RUN groupadd -r app && adduser -r -g app app
USER app
EXPOSE 8080
CMD ["sh","-c","/run_app.sh"]
