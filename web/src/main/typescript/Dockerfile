FROM node:lts-alpine3.17
ENV CLIENT_VOLUME=/web/client
RUN mkdir -p ${CLIENT_VOLUME}
COPY web/src/main/typescript ${CLIENT_VOLUME}
VOLUME ${CLIENT_VOLUME}
WORKDIR ${CLIENT_VOLUME}
RUN npm install
RUN npm run build
ENV HOST 0.0.0.0