FROM openjdk:17-jdk-alpine
ENV SERVER_VOLUME=/web/server
RUN mkdir -p ${SERVER_VOLUME}
VOLUME ${SERVER_VOLUME}
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
# COPY ./create-database.sql /web/server/create-database.sql
COPY web/build/libs/web-1.0.0.jar /web/server/Order-shop-web.jar
EXPOSE 8443