FROM openjdk:8-jdk-alpine

COPY target/formatter-*-allinone.jar /opt/formatter/formatter.jar

RUN addgroup -S appgroup &&\
    adduser -S -G appgroup appuser &&\
    chmod -R 777 /opt/ &&\
    chown -R appuser:appgroup /opt/

USER appuser

EXPOSE 8080

WORKDIR /opt/formatter

ENTRYPOINT java -jar formatter.jar
