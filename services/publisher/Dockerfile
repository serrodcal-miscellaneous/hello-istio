FROM openjdk:8-jdk-alpine

COPY target/publisher-*-allinone.jar /opt/publisher/publisher.jar

RUN addgroup -S appgroup &&\
    adduser -S -G appgroup appuser &&\
    chmod -R 777 /opt/ &&\
    chown -R appuser:appgroup /opt/

USER appuser

EXPOSE 8080

WORKDIR /opt/publisher

ENTRYPOINT java -jar publisher.jar
