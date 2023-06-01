#!/bin/bash

gradle clean bootJar
docker run cf83b596cc88

docker rm network nginx_network
docker-compose down --remove-orphans --volumes
docker rm -f $(docker ps | grep -v "mcr.microsoft.com/mssql/server:2022-latest")
docker network create nginx_network
docker-compose up -d