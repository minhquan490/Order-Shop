FROM ubuntu
ENV SERVER_VOLUME=web/server
ENV DICTS=dicts
RUN apt-get update && \
    apt-get install -y software-properties-common && \
    add-apt-repository -y ppa:openjdk-r/ppa && \
    apt-get install -y openjdk-17-jdk-headless && \
    apt-get install -y libstdc++6 && \
    apt-get install -y git && \
    apt-get install -y cmake && \
    apt-get install -y g++
RUN mkdir -p ${SERVER_VOLUME} && \
    mkdir -p ${SERVER_VOLUME}/index && \
    mkdir -p ${SERVER_VOLUME}/log && \
    mkdir -p ${SERVER_VOLUME}/crawler && \
    mkdir -p ${SERVER_VOLUME}/git
RUN cd ./${SERVER_VOLUME}/git && \
    git clone https://github.com/duydo/coccoc-tokenizer.git && \
    cd ./coccoc-tokenizer && \
    mkdir build && \
    cd ./build && \
    cmake -DBUILD_JAVA=1 .. && \
    make install && \
    cp /usr/local/lib/libcoccoc_tokenizer_jni.so /usr/lib/libcoccoc_tokenizer_jni.so && \
    cp /usr/local/lib/libcoccoc_tokenizer_jni.so /${SERVER_VOLUME}/libcoccoc_tokenizer_jni.so
VOLUME ${SERVER_VOLUME}
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY ${DICTS}/story-community-db16d0ac040b.json ${SERVER_VOLUME}/story-community-db16d0ac040b.json
COPY ${DICTS}/stop-word.txt ${SERVER_VOLUME}/stop-word.txt
COPY ${DICTS}/localhost.pem ${SERVER_VOLUME}/localhost.pem
COPY ${DICTS}/localhost-key.pem ${SERVER_VOLUME}/localhost-key.pem
COPY ${DICTS}/chromedriver ${SERVER_VOLUME}/chromedriver
COPY ${DICTS}/geckodriver ${SERVER_VOLUME}/geckodriver
COPY ${DICTS}/msedgedriver ${SERVER_VOLUME}/msedgedriver
COPY web/build/libs/web-1.0.0.jar ${SERVER_VOLUME}/Order-shop-web.jar
