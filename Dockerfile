FROM openjdk:17
ENV SERVER_VOLUME=web/server
ENV DICTS=dicts
RUN mkdir -p ${SERVER_VOLUME}
RUN mkdir -p ${SERVER_VOLUME}/index
RUN mkdir -p ${SERVER_VOLUME}/log
RUN mkdir -p ${SERVER_VOLUME}/crawler
VOLUME ${SERVER_VOLUME}
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY ${DICTS}/libcoccoc_tokenizer_jni.so ${SERVER_VOLUME}/libcoccoc_tokenizer_jni.so
COPY ${DICTS}/story-community-db16d0ac040b.json ${SERVER_VOLUME}/story-community-db16d0ac040b.json
COPY ${DICTS}/stop-word.txt ${SERVER_VOLUME}/stop-word.txt
COPY ${DICTS}/localhost.pem ${SERVER_VOLUME}/localhost.pem
COPY ${DICTS}/localhost-key.pem ${SERVER_VOLUME}/localhost-key.pem
COPY ${DICTS}/chromedriver ${SERVER_VOLUME}/chromedriver
COPY ${DICTS}/geckodriver ${SERVER_VOLUME}/geckodriver
COPY ${DICTS}/msedgedriver ${SERVER_VOLUME}/msedgedriver
COPY ${DICTS}/tokenizer/. ${SERVER_VOLUME}/tokenizer/
COPY web/build/libs/web-1.0.0.jar ${SERVER_VOLUME}/Order-shop-web.jar
RUN chmod -R 777 ${SERVER_VOLUME}
RUN chmod -R 777 lib