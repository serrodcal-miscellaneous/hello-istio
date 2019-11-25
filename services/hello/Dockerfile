FROM openjdk:8-jdk-alpine

COPY target/hello-*-allinone.jar /opt/hello/hello.jar

RUN addgroup -S appgroup &&\
    adduser -S -G appgroup appuser &&\
    chmod -R 777 /opt/ &&\
    chown -R appuser:appgroup /opt/

USER appuser

EXPOSE 8080

WORKDIR /opt/hello

ENTRYPOINT java -jar hello.jar
